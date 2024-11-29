pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Define que solo se usen repos de settings.gradle
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AppJotDownPlanner"
include(":app")
