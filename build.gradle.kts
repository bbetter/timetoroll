buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(Plugins.gradle)
        classpath(Plugins.kotlin)
        classpath(Plugins.kotlinSerialization)
        classpath(Plugins.googleServices)
        classpath(Plugins.crashlytics)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}