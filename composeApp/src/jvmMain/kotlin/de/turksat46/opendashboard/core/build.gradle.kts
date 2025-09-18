sourceSets {
    val jvmMain by getting {
        dependencies {
            // Fügt die API als compile-time Abhängigkeit hinzu
            implementation(project(":api"))
        }
    }
}