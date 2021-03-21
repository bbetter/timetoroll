rootProject.name = "timetoroll"
enableFeaturePreview("GRADLE_METADATA")

var includeAndroid = System.getProperty("INCLUDE_ANDROID")

if (includeAndroid == "true") {
    include("androidApp")
}
include(":shared", ":backend")
