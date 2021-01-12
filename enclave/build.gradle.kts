plugins {
    kotlin("jvm")
    id("com.r3.conclave.enclave")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.r3.conclave:conclave-enclave")
    testImplementation("com.r3.conclave:conclave-testing")
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

conclave {
    productID.set(1)
    revocationLevel.set(0)
    // runtime.set(graalvm_native_image)
    runtime.set(avian)
}