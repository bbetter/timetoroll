buildscript {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Plugins.gradlePlugin)
        classpath(Dependencies.Plugins.kotlinPlugin)
        classpath(Dependencies.Plugins.koinPlugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://dl.bintray.com/ekito/koin")
    }
}