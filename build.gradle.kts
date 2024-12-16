plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.beauver.discord.bots"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }

    maven {
        name = "m2-duncte123"
        url = uri("https://m2.duncte123.dev/releases")
    }

    maven {
        url = uri("https://m2.chew.pro/snapshots")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    //Discord Bot For Java
    implementation("io.github.freya022:JDA:7c7d09bf4d")
    //Database
    implementation("mysql:mysql-connector-java:8.0.33")
    //DotEnv
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar{
    manifest {
        attributes["Main-Class"] = "com.beauver.discord.bots.MainKt"
    }
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "com.beauver.discord.bots.MainKt"
    }
}