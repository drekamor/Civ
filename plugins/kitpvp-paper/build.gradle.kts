plugins {
    id("io.papermc.paperweight.userdev")
    id("com.github.johnrengelman.shadow")
}

version = "1.0.0"

dependencies {
    paperweight {
        paperDevBundle(libs.versions.paper)
    }

    compileOnly(project(":plugins:civmodcore-paper"))
    compileOnly(project(":plugins:finale-paper"))

    compileOnly(files("../../ansible/src/paper-plugins/TAB v5.0.3.jar"))
    compileOnly(libs.aswm.api)
}
