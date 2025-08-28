import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("io.ktor:ktor-server-netty:3.2.3")
            implementation("com.juul.kable:kable-core:0.39.2")
            implementation("com.benasher44:uuid:0.8.4")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.androidx.lifecycle.livedata.core.ktx)
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.usb.library.core)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.usb.library.jvm)

            }
        }
    }
}


compose.desktop {
    application {
        mainClass = "de.turksat46.opendashboard.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "openDashboard"
            packageVersion = "1.0.0"
        }
    }
}
