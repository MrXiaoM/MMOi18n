plugins {
    java
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "top.mrxiaom"
version = "1.0.2-6.10.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") {
        mavenContent {
            includeModule("io.lumine", "MythicLib-dist")
            includeModule("net.Indyuce", "MMOItems-API")
        }
    }
    maven("https://maven.fastmirror.net/repositories/minecraft")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-releases/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("io.lumine:MythicLib-dist:1.7.1-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.10.1-SNAPSHOT")

    compileOnly("commons-lang:commons-lang:2.6")
    implementation("de.tr7zw:item-nbt-api:2.14.1")
    implementation("org.jetbrains:annotations:21.0.0")
}
val targetJavaVersion = 17
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
        archiveClassifier.set("")
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
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
}
