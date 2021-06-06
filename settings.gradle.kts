plugins {
    id("com.louiscad.complete-kotlin") version "1.0.0"
}

rootProject.name = "timetoroll"
enableFeaturePreview("GRADLE_METADATA")

var includeAndroid = System.getProperty("INCLUDE_ANDROID") ?: "true"

if (includeAndroid == "true") {
    include("androidApp")
}
include(":shared", ":backend")
