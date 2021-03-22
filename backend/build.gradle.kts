plugins {
    application
    kotlin("multiplatform")
    id("kotlinx-serialization")
}


kotlin {
    jvm {
        withJava()
    }
}

dependencies {
    implementation(Libs.Coroutines.core)

    implementation(Libs.KtorServer.core)
    implementation(Libs.KtorServer.netty)
    implementation(Libs.KtorServer.serialization)
    implementation(Libs.KtorServer.websockets)

    implementation(Libs.kotlinSerialization) // JVM dependency

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation(project(":shared"))
    testImplementation(Libs.KtorServer.test)
}
application {
    @Suppress("DEPRECATION")
    mainClassName = "com.owlsoft.backend.ServerKt"
}

tasks.register("stage") {
    dependsOn("installDist")
}
