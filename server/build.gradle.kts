import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

buildscript {

    dependencies {
        classpath("com.moowork.gradle:gradle-node-plugin:1.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.10")
    }
}

plugins {
    id("kotlin2js") version "1.3.10"
    id("com.moowork.node") version "1.2.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    testImplementation(kotlin("test-js"))
}

tasks {
    node {
        download = false
    }
    compileKotlin2Js {
        kotlinOptions {
            moduleKind = "commonjs"
            destinationDir = file("$buildDir/js")
            //outputFile = "$buildDir/js/output.js"
            noStdlib = false
        }
    }
    compileTestKotlin2Js {
        kotlinOptions {
            moduleKind = "commonjs"
            outputFile = "$buildDir/jstest.js"
            noStdlib = false
        }
    }
    val populateNodeModules by registering(Copy::class) {
        group = "build"
        description = "Assemble the web application"
        dependsOn(compileKotlin2Js)
        from("$buildDir/js")

        configurations.testCompile.get().forEach {
            from(zipTree(it.absolutePath).filter { f -> f.name.endsWith(".js") })
        }

        into("$buildDir/node_modules")
    }
    val npmTest by registering(NpmTask::class) {
        dependsOn(compileTestKotlin2Js, populateNodeModules, npmInstall)
        setArgs(listOf("run", "test"))
    }
    test {
        dependsOn(npmTest)
    }
    create("serve", NpmTask::class){
        setArgs(listOf("run", "serve"))
    }
    create("rebuild"){
        val prodFiles = fileTree("src/main/kotlin")
        prodFiles.include("**/*.kt")
        this.inputs.files(prodFiles)
        dependsOn(compileKotlin2Js)
    }
}
