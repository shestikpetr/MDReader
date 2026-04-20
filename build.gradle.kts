import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
}

group = "com.shestikpetr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.commonmark:commonmark:0.24.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.24.0")
    implementation("org.commonmark:commonmark-ext-gfm-strikethrough:0.24.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
            packageName = "MDReader"
            packageVersion = "1.0.0"
            description = "Markdown Viewer"
            vendor = "shestikpetr"

            fileAssociation(
                mimeType = "text/markdown",
                extension = "md",
                description = "Markdown File"
            )

            windows {
                menuGroup = "MDReader"
                shortcut = true
                dirChooser = true
                perUserInstall = true
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}
