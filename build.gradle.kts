import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import pl.allegro.tech.build.axion.release.domain.hooks.HooksConfig

plugins {
    kotlin("jvm") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("pl.allegro.tech.build.axion-release") version "1.10.2"
}

val mcApiVersion = "1.15"

group = "org.simplemc"
version = scmVersion.version

scmVersion {
    hooks(closureOf<HooksConfig> {
        pre(
            "fileUpdate",
            mapOf(
                "file" to "src/main/resources/plugin.yml",
                "pattern" to KotlinClosure2<String, HookContext, String>({ v, _ -> "version: $v\\napi-version: \".+\"" }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ v, _ -> "version: $v\napi-version: \"$mcApiVersion\"" })
            )
        )
        pre("commit")
    })
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(group = "org.spigotmc", name = "spigot-api", version = "$mcApiVersion+")
}

tasks {
    wrapper {
        gradleVersion = "6.0.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    withType<ShadowJar> {
        minimize()
    }

    build {
        dependsOn(":shadowJar")
    }
}
