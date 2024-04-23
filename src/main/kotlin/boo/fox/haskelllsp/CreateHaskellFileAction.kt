package boo.fox.haskelllsp

import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class CreateHaskellFileAction : CreateFileFromTemplateAction("Haskell File", "Create new Haskell file", Icons.Haskell) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Haskell File")
            .addKind("Empty file", Icons.Haskell, "Haskell File")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Haskell File"
    }

    
}