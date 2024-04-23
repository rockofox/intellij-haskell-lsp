package boo.fox.haskelllsp

import com.intellij.openapi.util.IconLoader


interface Icons {
    companion object {
        val Cabal = IconLoader.getIcon("icons/cabal.svg", Icons::class.java)
        val Haskell = IconLoader.getIcon("icons/haskell.svg", Icons::class.java)
    }
}