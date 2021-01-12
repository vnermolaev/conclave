val mode = findProperty("enclaveMode")?.toString()?.toLowerCase() ?: "simulation"

plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.ing.counter.host.HostKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    val conclaveVersion: String by project
    implementation("com.r3.conclave:conclave-host:$conclaveVersion")
    runtimeOnly(project(path = ":enclave", configuration = mode))

    // implementation(group="org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.4.2")

    runtimeOnly("org.slf4j:slf4j-simple:1.7.30")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}