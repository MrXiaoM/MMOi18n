plugins {
    java
    id ("com.gradleup.shadow") version "8.3.0"
}

val nbtapi: String by ext
val targetJavaVersion = 17
version = "${version}_mi-6.10"

repositories {
    mavenCentral()
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") {
        mavenContent {
            includeModule("io.lumine", "MythicLib-dist")
            includeModule("net.Indyuce", "MMOItems-API")
        }
    }
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-releases/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.10-SNAPSHOT")

    compileOnly("commons-lang:commons-lang:2.6")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation(project(":common"))
}
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}
tasks {
    shadowJar {
        archiveBaseName.set("MMOi18n")
        archiveClassifier.set("")
        destinationDirectory.set(rootProject.file("out"))
        mapOf(
            "org.intellij.lang.annotations" to "annotations.intellij",
            "org.jetbrains.annotations" to "annotations.jetbrains",
            "de.tr7zw.changeme.nbtapi" to "nbtapi",
        ).forEach { (original, target) ->
            relocate(original, "top.mrxiaom.mmoi18n.libs.$target")
        }
    }
    build {
        dependsOn(shadowJar)
    }
    withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}
