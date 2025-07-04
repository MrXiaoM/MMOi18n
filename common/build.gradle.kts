plugins {
    java
}

val nbtapi: String by ext
val targetJavaVersion = 17

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("commons-lang:commons-lang:2.6")
    implementation("de.tr7zw:item-nbt-api:${nbtapi}")
    implementation("org.jetbrains:annotations:24.0.0")
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
    withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}
