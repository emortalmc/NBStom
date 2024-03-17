import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
//    implementation("com.github.Minestom:Minestom:8ad2c7701f")
    compileOnly("net.minestom:minestom-snapshots:4b31570c9d")
    testImplementation("net.minestom:minestom-snapshots:4b31570c9d")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}


tasks {

//    test {
//        useJUnitPlatform()
//    }

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
        minimize()
    }

    build { dependsOn(shadowJar) }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
