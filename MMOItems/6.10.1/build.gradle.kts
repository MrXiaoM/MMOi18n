plugins {
    java
    id ("com.gradleup.shadow") version "8.3.0"
}

val nbtapi: String by ext
val targetJavaVersion = 17
version = "${version}_mi-6.10.1"

repositories {
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") {
        mavenContent {
            includeModule("io.lumine", "MythicLib-dist")
            includeModule("net.Indyuce", "MMOItems-API")
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("io.lumine:MythicLib-dist:1.7.1-SNAPSHOT")
    compileOnly("net.Indyuce:MMOItems-API:6.10.1-SNAPSHOT")

    compileOnly("commons-lang:commons-lang:2.6")
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("de.tr7zw:item-nbt-api-plugin:${nbtapi}")
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
    }
    val copyTask = create<Copy>("copyBuildArtifact") {
        dependsOn(shadowJar)
        from(shadowJar.get().outputs)
        rename { "${shadowJar.get().archiveBaseName.get()}-$version.jar" }
        into(rootProject.file("out"))
    }
    build {
        dependsOn(copyTask)
    }
    withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}
