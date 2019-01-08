import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

buildscript {
    dependencies {
        classpath("com.moowork.gradle:gradle-node-plugin:1.2.0")
    }
}

plugins {
    id("kotlin2js") version "1.3.10"
    id("com.moowork.node") version "1.2.0"
}

dependencies {
    implementation(kotlin("stdlib-js"))
    testImplementation(kotlin("test-js"))
}

repositories {
    jcenter()
}

tasks {
    node {
        download = true
    }
    compileKotlin2Js {
        kotlinOptions {
            outputFile = "${sourceSets.main.get().output.resourcesDir}/output.js"
            sourceMap = true
            sourceMapEmbedSources = "always"
            main = "call"
            metaInfo = true
            noStdlib = false
            moduleKind = "commonjs"
        }
    }
    compileTestKotlin2Js {
        kotlinOptions {
            main = "call"
            outputFile = "${sourceSets.test.get().output.resourcesDir}/full_test.js"
            moduleKind = "commonjs"
            noStdlib = false
            sourceMap = true
        }
    }
    val assembleWeb by registering(Copy::class) {
        group = "build"
        description = "Assemble the web application"
        includeEmptyDirs = false
        from(sourceSets.main.get().output) {
            exclude("**/*.kjsm")
        }
        into("$buildDir/web")
    }
    val prodToTest by registering(Copy::class) {
        group = "build"
        description = "Add prod code to test code to enable testing"
        includeEmptyDirs = false
        from(sourceSets.main.get().output) {
            include("output.js")
        }
        into("${sourceSets.test.get().output.resourcesDir}/node_modules/output")
    }
    assemble {
        dependsOn(assembleWeb, prodToTest)
    }
}

