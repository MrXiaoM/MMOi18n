allprojects {
    group = "top.mrxiaom"
    version = "1.0.3"
    ext["nbtapi"] = "2.15.3"

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.org/repository/maven-public/")
    }
}
