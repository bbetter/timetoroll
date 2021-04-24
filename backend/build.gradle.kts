plugins {
    application
    kotlin("jvm")
    id("kotlinx-serialization")
}

application {
    mainClass.set("com.owlsoft.backend.ServerKt")
}


dependencies {
    implementation(Libs.Coroutines.core)

    implementation(Libs.KtorServer.core)
    implementation(Libs.KtorServer.netty)
    implementation(Libs.KtorServer.serialization)
    implementation(Libs.KtorServer.websockets)

    implementation(Libs.kotlinSerialization) // JVM dependency

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation(files("../shared/build/libs/shared-jvm.jar"))
    testImplementation(Libs.KtorServer.test)
}

tasks.getByName("installDist"){
    dependsOn(":shared:build")
}

tasks.register("stage") {
    dependsOn("installDist")
}