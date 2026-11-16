plugins {
    kotlin("jvm") version "2.1.0"
    idea
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

val http4k = "5.47.0.0"

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.http4k:http4k-core:$http4k")
    implementation("org.http4k:http4k-config:$http4k")
    implementation("org.http4k:http4k-client-okhttp:$http4k")
    implementation("org.http4k:http4k-format-jackson:$http4k")
    implementation("com.natpryce:result4k:2.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4k")
    testImplementation("com.natpryce:hamkrest:1.7.0.0")
}

idea {
    project {
        jdkName = "17"
        languageLevel = org.gradle.plugins.ide.idea.model.IdeaLanguageLevel("17")
        vcs = "Git"
    }
    module {
        outputDir = file("${layout.buildDirectory.get()}/classes/kotlin/main")
        testOutputDir = file("${layout.buildDirectory.get()}/classes/kotlin/test")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileTestKotlin {
    friendPaths.setFrom(sourceSets["main"].output.classesDirs)
}

val webAppFiles = fileTree(project.file("src/main/react/build")).matching {
    include("**/*")
}

tasks.register<Exec>("pull_submodules") {
    description = "Pulls git submodules"
    commandLine("git", "submodule", "update", "--init", "--recursive")
}

tasks.register<Exec>("init_borgy") {
    description = "Initializes borgy dependencies"
    dependsOn(tasks.named("pull_submodules"))
    workingDir("src/main/react")
    commandLine("yarn", "install")
}

tasks.register<Exec>("ui_clean") {
    description = "Cleans the react UI build artifacts"
    workingDir("src/main/react")
    commandLine("yarn", "clean")
}

tasks.register<Exec>("ui_build") {
    description = "Builds the react UI"
    workingDir("src/main/react")
    commandLine("yarn", "build")
}

tasks.register<JavaExec>("start_back") {
    description = "Starts the backend server in dev mode"
    mainClass.set("com.raymanoz.borgy.MainKt")
    systemProperty("config", "dev")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<Exec>("start_front") {
    description = "Starts the frontend dev server"
    workingDir("src/main/react")
    commandLine("yarn", "start")
}

tasks.register<JavaExec>("start_jar") {
    description = "Runs the application from the packaged JAR"
    dependsOn(tasks.jar)
    classpath = files("build/libs/borgy.jar")
    mainClass.set("com.raymanoz.borgy.MainKt")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to "com.raymanoz.borgy.MainKt")
    }
    from(webAppFiles) {
        into("public")
    }
    from({
        configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) }
    })
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    dependsOn(tasks.named("ui_build"))
}

tasks.clean {
    dependsOn(tasks.named("ui_clean"))
}
