package boo.fox.haskelllsp.evaluation

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.VirtualFile
import boo.fox.haskelllsp.language.psi.HaskellTypes
import com.intellij.openapi.util.TextRange
import kotlin.math.min

class EvaluateQuickFix(private val expression: String, private val filePath: String? = null) : LocalQuickFix, IntentionAction {
    override fun getName(): String = "Evaluate: $expression"

    override fun getFamilyName(): String = "Haskell Evaluation"

    // IntentionAction API
    override fun getText(): String = getName()

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val effectiveFilePath = filePath ?: file?.virtualFile?.path

        HaskellEvaluationService.getInstance(project).evaluateExpressionAsync(expression, effectiveFilePath) { result ->
            val message = if (result.error != null && result.error.isNotEmpty()) {
                "Error: ${result.error}"
            } else {
                result.result
            }

            ApplicationManager.getApplication().invokeLater {
                if (editor != null && file != null) {
                    WriteCommandAction.runWriteCommandAction(project) {
                        try {
                            val caretOffset = editor.caretModel.offset
                            var element = file.findElementAt(caretOffset)
                            while (element != null && element.node.elementType != HaskellTypes.HS_COMMENT) {
                                element = element.parent
                            }

                            val doc = editor.document

                            if (element != null) {
                                val insertOffset = element.textRange.endOffset

                                // Prepare formatted result lines, each prefixed with "-- "
                                val formattedLines = message.lines().map { "-- " + it }
                                val formattedBlock = formattedLines.joinToString("\n")

                                val nextLineIndex = doc.getLineNumber(insertOffset) + 1

                                // If there's an existing result block (consecutive lines starting with "-- ") replace the whole block
                                if (nextLineIndex < doc.lineCount) {
                                    var line = nextLineIndex
                                    val sb = StringBuilder()
                                    var foundBlock = false
                                    while (line < doc.lineCount) {
                                        val lineStart = doc.getLineStartOffset(line)
                                        val lineEnd = doc.getLineEndOffset(line)
                                        val lineText = doc.getText(TextRange(lineStart, lineEnd))
                                        if (lineText.trimStart().startsWith("-- ")) {
                                            foundBlock = true
                                            sb.append(lineText).append('\n')
                                            line++
                                        } else {
                                            break
                                        }
                                    }

                                    if (foundBlock) {
                                        val replaceStart = doc.getLineStartOffset(nextLineIndex)
                                        val replaceEnd = doc.getLineEndOffset(line - 1)
                                        val toInsert = formattedBlock + "\n"
                                        doc.replaceString(replaceStart, replaceEnd, toInsert)
                                    } else {
                                        val toInsert = "\n" + formattedBlock + "\n"
                                        doc.insertString(insertOffset, toInsert)
                                    }
                                } else {
                                    val toInsert = "\n" + formattedBlock + "\n"
                                    doc.insertString(insertOffset, toInsert)
                                }

                                PsiDocumentManager.getInstance(project).commitDocument(doc)
                            } else if (caretOffset >= 0) {
                                val line = doc.getLineNumber(caretOffset)
                                val endOffset = doc.getLineEndOffset(line)
                                val formattedLines = message.lines().map { "-- " + it }
                                val toInsert = "\n" + formattedLines.joinToString("\n") + "\n"
                                doc.insertString(endOffset, toInsert)
                                PsiDocumentManager.getInstance(project).commitDocument(doc)
                            } else {
                                Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Result", message, NotificationType.INFORMATION), project)
                            }
                        } catch (e: Exception) {
                            Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Error", e.message ?: "Unknown error", NotificationType.ERROR), project)
                        }
                    }
                } else if (effectiveFilePath != null) {
                    val manager = FileDocumentManager.getInstance()
                    val vfile = com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(effectiveFilePath)
                    if (vfile != null) {
                        val doc = manager.getDocument(vfile)
                        if (doc != null) {
                            WriteCommandAction.runWriteCommandAction(project) {
                                try {
                                    val insertOffset = doc.textLength
                                    val formattedLines = message.lines().map { "-- " + it }
                                    val toInsert = "\n" + formattedLines.joinToString("\n") + "\n"
                                    doc.insertString(insertOffset, toInsert)
                                    manager.saveDocument(doc)
                                } catch (e: Exception) {
                                    Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Error", e.message ?: "Unknown error", NotificationType.ERROR), project)
                                }
                            }
                        } else {
                            Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Result", message, NotificationType.INFORMATION), project)
                        }
                    } else {
                        Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Result", message, NotificationType.INFORMATION), project)
                    }
                } else {
                    Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Result", message, NotificationType.INFORMATION), project)
                }
            }
        }
    }

    override fun startInWriteAction(): Boolean = false

    // LocalQuickFix API
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val psi = descriptor.psiElement ?: return
        val containingFile = psi.containingFile
        val doc = PsiDocumentManager.getInstance(project).getDocument(containingFile) ?: return
        val insertOffset = psi.textRange.endOffset
        val effectiveFilePath = filePath ?: containingFile.virtualFile?.path

        HaskellEvaluationService.getInstance(project).evaluateExpressionAsync(expression, effectiveFilePath) { result ->
            val message = if (result.error != null && result.error.isNotEmpty()) {
                "Error: ${result.error}"
            } else {
                result.result
            }

            ApplicationManager.getApplication().invokeLater {
                WriteCommandAction.runWriteCommandAction(project) {
                    try {
                        // Prepare formatted lines
                        val formattedLines = message.lines().map { "-- " + it }
                        val formattedBlock = formattedLines.joinToString("\n")

                        val nextLineIndex = doc.getLineNumber(insertOffset) + 1

                        if (nextLineIndex < doc.lineCount) {
                            var line = nextLineIndex
                            var foundBlock = false
                            while (line < doc.lineCount) {
                                val lineStart = doc.getLineStartOffset(line)
                                val lineEnd = doc.getLineEndOffset(line)
                                val lineText = doc.getText(TextRange(lineStart, lineEnd))
                                if (lineText.trimStart().startsWith("-- ")) {
                                    foundBlock = true
                                    line++
                                } else {
                                    break
                                }
                            }

                            if (foundBlock) {
                                val replaceStart = doc.getLineStartOffset(nextLineIndex)
                                val replaceEnd = doc.getLineEndOffset(line - 1)
                                val toInsert = formattedBlock + "\n"
                                doc.replaceString(replaceStart, replaceEnd, toInsert)
                            } else {
                                val toInsert = "\n" + formattedBlock + "\n"
                                doc.insertString(insertOffset, toInsert)
                            }
                        } else {
                            val toInsert = "\n" + formattedBlock + "\n"
                            doc.insertString(insertOffset, toInsert)
                        }

                        PsiDocumentManager.getInstance(project).commitDocument(doc)
                    } catch (e: Exception) {
                        Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Error", e.message ?: "Unknown error", NotificationType.ERROR), project)
                    }
                }
            }
        }
    }
}
