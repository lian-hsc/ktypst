dependencies {
    api(project(":"))
    api(project(":stdlib"))

    implementation(kotlin("reflect"))
    implementation("tools.jackson.core:jackson-databind:3.1.4")
}
