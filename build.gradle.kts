import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import pl.allegro.tech.build.axion.release.domain.hooks.HooksConfig
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

val mcApiVersion = "1.18"

group = "org.simplemc"
version = scmVersion.version

scmVersion {
    versionIncrementer("incrementMinorIfNotOnRelease", mapOf(releaseBranchPattern to "v.*"))

    hooks(
        closureOf<HooksConfig> {
            pre(
                "fileUpdate",
                mapOf(
                    "file" to "CHANGELOG.md",
                    "pattern" to KotlinClosure2<String, HookContext, String>({ v, _ ->
                        "\\[Unreleased\\]([\\s\\S]+?)\\n(?:^\\[Unreleased\\]: https:\\/\\/github\\.com\\/SimpleMC\\/SimpleHealthbars2\\/compare\\/release-$v\\.\\.\\.HEAD\$([\\s\\S]*))?\\z"
                    }),
                    "replacement" to KotlinClosure2<String, HookContext, String>({ v, c ->
                        """
                            \[Unreleased\]
                            
                            ## \[$v\] - ${currentDateString()}$1
                            \[Unreleased\]: https:\/\/github\.com\/SimpleMC\/SimpleHealthbars2\/compare\/release-$v...HEAD
                            \[$v\]: https:\/\/github\.com\/SimpleMC\/SimpleHealthbars2\/compare\/release-${c.previousVersion}...release-$v$2
                        """.trimIndent()
                    })
                )
            )
            pre("commit")
        }
    )
}

fun currentDateString() = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(group = "org.spigotmc", name = "spigot-api", version = "$mcApiVersion+")
}

tasks {
    wrapper {
        gradleVersion = "7.4.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    processResources {
        val placeholders = mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion
        )

        filesMatching("plugin.yml") {
            expand(placeholders)
        }
    }

    // standard jar should be ready to go with all dependencies
    shadowJar {
        minimize()
        archiveClassifier.set("")
    }

    // nokt jar without the kotlin runtime
    register<ShadowJar>("nokt") {
        minimize()
        archiveClassifier.set("nokt")
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())

        dependencies {
            exclude(dependency("org.jetbrains.*:"))
        }
    }

    build {
        dependsOn(":shadowJar", ":nokt")
    }
}
