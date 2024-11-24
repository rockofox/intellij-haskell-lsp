plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.0.1"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
}

group = "boo.fox"
version = "1.3.4"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.3.6")
        plugin("com.redhat.devtools.lsp4ij:0.5.0")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

grammarKit {
    task<org.jetbrains.grammarkit.tasks.GenerateLexerTask>("generateHaskellLexer") {
        sourceFile.set(file("src/main/kotlin/boo/fox/haskelllsp/language/Haskell.flex"))
        targetOutputDir.set(file("src/main/gen/boo/fox/haskelllsp/language"))
    }
    task<org.jetbrains.grammarkit.tasks.GenerateParserTask>("generateHaskellParser") {
        dependsOn("generateHaskellLexer")
        sourceFile.set(file("src/main/kotlin/boo/fox/haskelllsp/language/haskell.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("boo/fox/haskelllsp/language/parser")
        pathToPsiRoot.set("boo/fox/haskelllsp/language/psi")
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        dependsOn("generateHaskellParser")
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        dependsOn("generateHaskellParser")
    }

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

sourceSets["main"].java.srcDirs("src/main/gen")
sourceSets["main"].kotlin.srcDirs("src/main/gen")
