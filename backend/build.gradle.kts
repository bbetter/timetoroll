plugins {
    id("kotlin-platform-jvm")
    application
    kotlin("plugin.serialization")
}

dependencies {
    implementation(Libs.Coroutines.core)

    implementation(Libs.KtorServer.core)
    implementation(Libs.KtorServer.netty)
    implementation(Libs.KtorServer.serialization)
    implementation(Libs.KtorServer.websockets)

    implementation(Libs.kotlinSerialization) // JVM dependency

    implementation("ch.qos.logback:logback-classic:1.2.1")

    implementation(project(":shared"))

    testImplementation(Libs.KtorServer.test)
}

application {
    mainClass.set("com.owlsoft.backend.ServerKt")
}

kotlin {
    sourceSets {
        val test by getting
    }
}