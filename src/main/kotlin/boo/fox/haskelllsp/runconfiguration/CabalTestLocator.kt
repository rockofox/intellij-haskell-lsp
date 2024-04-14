package boo.fox.haskelllsp.runconfiguration

import com.intellij.execution.Location
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope

class CabalTestLocator: SMTestLocator{
    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        scope: GlobalSearchScope
    ): MutableList<Location<PsiElement>> = mutableListOf()
}