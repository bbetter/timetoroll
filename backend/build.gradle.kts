plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.0.0"
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
    @Suppress("DEPRECATION")
    mainClassName ="com.owlsoft.backend.ServerKt"
}

tasks.shadowJar{
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.owlsoft.backend.ServerKt"
            )
        )
    }
}