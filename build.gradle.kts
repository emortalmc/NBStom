import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.goooler.shadow") version "8.1.8"
    `maven-publish`
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
//    implementation("com.github.Minestom:Minestom:8ad2c7701f")
    compileOnly("net.minestom:minestom-snapshots:4f1017d398")
    testImplementation("net.minestom:minestom-snapshots:4f1017d398")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}


tasks {

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        minimize()
    }

    build { dependsOn(shadowJar) }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.properties["group"] as? String?
            artifactId = project.name
            version = project.properties["version"] as? String?

            from(components["java"])
        }
    }
}
