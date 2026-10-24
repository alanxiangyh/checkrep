pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "checkrep"
if (!providers.gradleProperty("publishPluginOnly").isPresent) include("demo")
