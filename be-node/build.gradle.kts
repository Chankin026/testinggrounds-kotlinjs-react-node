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
    id("com.moowork.node") version "1.2.0"
}

repositories {
    jcenter()
}

tasks {
    node {
        download = false
    }

    val buildTask = create("build", NpmTask::class) {
        group = "build"
        dependsOn(npmInstall)
        setArgs(listOf("run", "build"))
    }
    create("test", NpmTask::class) {
        group = "verification"
        dependsOn(buildTask)
        setArgs(listOf("run", "test"))
    }
    create("run", NpmTask::class) {
        dependsOn(buildTask)
        setArgs(listOf("run", "start"))
    }

}