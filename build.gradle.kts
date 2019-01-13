import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

group = "se.jensim.testinggrounds"
version = "1.0-SNAPSHOT"

buildscript {

    dependencies {
        classpath("com.moowork.gradle:gradle-node-plugin:1.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.10")
    }
}

configurations {
    maybeCreate("main")
    maybeCreate("test")
}

plugins {
    id("kotlin2js") version "1.3.10"
    id("com.moowork.node") version "1.2.0"
}

repositories {
    jcenter()
}

dependencies {
    add("main", kotlin("stdlib-js"))
    add("test", kotlin("stdlib-js"))
    add("test", kotlin("test-js"))
}

sourceSets {
    main {
        java.srcDir("src/server/main/kotlin").matching { include("**/*.kt") }
        resources { srcDir("src/server/main/resources") }
        compileClasspath = configurations.getByName("main")
    }
    test {
        java.srcDir("src/server/test/kotlin").matching { include("**/*.kt") }
        resources { srcDir("src/server/test/resources") }
        compileClasspath = configurations.getByName("test")
    }
    create("client") {
        java.srcDir("src/client/main/kotlin").matching { include("**/*.kt") }
        resources { srcDir("src/client/main/resources") }
        compileClasspath = configurations.getByName("main")
    }
    create("clientTest") {
        java.srcDir("src/client/test/kotlin").matching { include("**/*.kt") }
        resources { srcDir("src/client/test/resources") }
        compileClasspath = configurations.getByName("test")
    }
}

tasks {
    node {
        download = false
    }
    val compileClientKotlin2Js = named<Kotlin2JsCompile>("compileClientKotlin2Js")
    val compileClientTestKotlin2Js = named<Kotlin2JsCompile>("compileClientTestKotlin2Js")
    val prodConfigs = listOf(compileKotlin2Js, compileClientKotlin2Js)
    val testConfigs = listOf(compileTestKotlin2Js, compileClientTestKotlin2Js)
    prodConfigs.forEach {
        with(it.get()) {
            destinationDir = file("$buildDir/dist")
            kotlinOptions {
                moduleKind = "commonjs"
            }
        }
    }
    testConfigs.forEach {
        with(it.get()) {
            destinationDir = file("$buildDir/test")
            kotlinOptions {
                moduleKind = "commonjs"
            }
        }
    }
    val populateTestNodeModules by registering(Copy::class) {
        group = "build"
        description = "Assemble the web application"
        dependsOn(prodConfigs)
        prodConfigs.forEach {
            with(it.get().outputFile) {
                println("Adding from source: $this")
                from(this)
            }
        }

        configurations.testCompile.get().forEach {
            println("Assing testCompile configuration: $it")
            from(zipTree(it.absolutePath).filter { f -> f.name.endsWith(".js") })
        }

        into("$buildDir/test/node_modules")
    }
    val npmTest by registering(NpmTask::class) {
        dependsOn(testConfigs, populateTestNodeModules, npmInstall)
        setArgs(listOf("run", "test"))
    }
    test {
        dependsOn(npmTest)
    }
    build {
        dependsOn(prodConfigs, testConfigs)
    }
    create("npmServe", NpmTask::class) {
        setArgs(listOf("run", "serve"))
    }
    create("rebuild") {
        val prodFiles = fileTree("src")
        prodFiles.include("**/*.kt")
        this.inputs.files(prodFiles)
        dependsOn(compileKotlin2Js, compileClientKotlin2Js)
    }
    create("print") {
        println("sourceSets: ${sourceSets.asMap}")
        sourceSets.asMap.forEach { k, v ->
            println("$k :: ${v.java.sourceDirectories.files}")
        }
    }
}
