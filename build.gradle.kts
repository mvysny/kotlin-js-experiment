plugins {
    id("org.jetbrains.kotlin.js") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
}

defaultTasks("clean", "build")

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
}

kotlin {
    js {
        browser {
            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
                cssSupport.enabled = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }
}