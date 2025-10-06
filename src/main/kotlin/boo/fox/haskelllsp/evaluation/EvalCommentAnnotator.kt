package boo.fox.haskelllsp.evaluation

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import boo.fox.haskelllsp.language.psi.HaskellTypes
import boo.fox.haskelllsp.settings.HaskellEvaluationSettingsComponent

class EvalCommentAnnotator : Annotator {
    companion object {
        private val EVAL_PATTERN = Regex("""--\s*>>>\s*(.+)""")
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.node.elementType != HaskellTypes.HS_COMMENT) {
            return
        }

        val commentText = element.text
        val matchResult = EVAL_PATTERN.find(commentText)
        
        if (matchResult != null) {
            val expressionStart = element.textRange.startOffset + matchResult.range.first
            val expressionEnd = element.textRange.startOffset + matchResult.range.last + 1
            val expressionRange = TextRange(expressionStart, expressionEnd)
            val expression = matchResult.groupValues[1].trim()
            
            holder.newAnnotation(HighlightSeverity.INFORMATION, "Evaluate: $expression")
                .range(expressionRange)
                .textAttributes(boo.fox.haskelllsp.language.HaskellSyntaxHighlighter.DocComment)
                .withFix(EvaluateQuickFix(expression, element.containingFile?.virtualFile?.path))
                .create()

            // Auto-evaluate if enabled in settings
            val settings = HaskellEvaluationSettingsComponent()
            if (settings.autoEvaluate) {
                val path = element.containingFile?.virtualFile?.path
                HaskellEvaluationService.getInstance(element.project).evaluateExpressionAsync(expression, path) { evalResult ->
                    val message = if (evalResult.error != null && evalResult.error.isNotEmpty()) {
                        "Error: ${evalResult.error}"
                    } else {
                        "Result: ${evalResult.result}"
                    }
                    Notifications.Bus.notify(Notification("Haskell Eval", "Evaluation Result", message, NotificationType.INFORMATION), element.project)
                }
            }
        }
    }
}
