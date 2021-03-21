buildscript {

    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(Plugins.gradle)
        classpath(Plugins.kotlin)
        classpath(Plugins.koin)
        classpath(Plugins.kotlinSerialization)
        classpath(Plugins.googleServices)
        classpath(Plugins.crashlytics)
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