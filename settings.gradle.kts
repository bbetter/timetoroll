rootProject.name = "timetoroll"
enableFeaturePreview("GRADLE_METADATA")

var includeAndroid = System.getProperty("INCLUDE_ANDROID") ?: "true"

if (includeAndroid == "true") {
    include("androidApp")
}
include(":shared", ":backend")
