plugins {
    java
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.ing.counter.client.ClientKt"
}


dependencies {
    val conclaveVersion: String by project
    implementation("com.r3.conclave:conclave-client:$conclaveVersion")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
}