rootProject.name = "conclave-counter"

pluginManagement {
    repositories {
        maven {
            val conclaveRepo: String by settings
            val repoPath = rootDir.resolve(conclaveRepo).canonicalFile

            require(repoPath.exists()) {
                """Make sure the 'conclaveRepo' setting exists in gradle.properties,
                    or your gradle.properties file. See the Conclave tutorial on https://docs.conclave.net
                """.trimIndent()
            }

            require(repoPath.resolve("com").isDirectory) {
                """The $repoPath directory doesn't seem to exist or isn't a Maven repository;
                    it should be the SDK 'repo' subdirectory. See the Conclave tutorial on https://docs.conclave.net
                """.trimIndent()
            }

            url  = repoPath.toURI()
        }
        // Add standard repositories back.
        gradlePluginPortal()
        jcenter()
        mavenCentral()
    }

    plugins {
        val conclaveVersion: String by settings
        id("com.r3.conclave.enclave") version conclaveVersion apply false
    }
}

include("host")
include("client")
include("enclave")
