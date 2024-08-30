/*
 * Copyright 2020 SpotBugs plugin contributors
 *
 * This file is part of IntelliJ SpotBugs plugin.
 *
 * IntelliJ SpotBugs plugin is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IntelliJ SpotBugs plugin is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IntelliJ SpotBugs plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.kover)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter("-", "").substringBefore(".").ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

val thirdPartyPlugins by configurations.creating
val fbContribJava6 by configurations.creating

dependencies {
    testImplementation (libs.assertjcore)
    testImplementation (libs.junit)
    testImplementation (libs.mockitocore)

    implementation (libs.spotbugs) {
        exclude (group = "xml-apis", module = "xml-apis")
        exclude (group = "org.apache.logging.log4j", module = "log4j-api")
        exclude (group = "org.apache.logging.log4j", module = "log4j-core")
        exclude (group = "org.slf4j", module = "slf4j-api")
    }
    implementation (libs.saxonhe) {
        exclude (group = "org.slf4j", module = "slf4j-api")
    }
    implementation (libs.jsoup)
    implementation (libs.tablelayout)
    thirdPartyPlugins (libs.fbcontrib)
    thirdPartyPlugins (libs.findsecbugs)
    fbContribJava6 (libs.fbcontrib6)

    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(",") })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(",") })

        instrumentationTools()
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Plugin.Java)
    }
}

tasks.register<Copy>("copyGradleProperties") {
    description = "Copy gradle.properties to project resources."
    from ("gradle.properties")
    into ("build/resources/main/org/jetbrains/plugins/spotbugs/common")
    rename { _ -> "version.properties" }
}

tasks.register<Copy>("downloadThirdPartyPlugins") {
    description = "Downloads third-party plugins Find Security Bugs and FB-Contrib."
    from (thirdPartyPlugins)
    from (fbContribJava6)
    into ("build/resources/main/org/jetbrains/plugins/spotbugs/plugins")
}

tasks.register<Copy>("copyThirdPartyPlugins") {
    description = "Copy plugins into sandbox."
    dependsOn(tasks["downloadThirdPartyPlugins"], tasks["prepareSandbox"])
    from ("build/resources/main/org/jetbrains/plugins/spotbugs/plugins")
    into ("build/idea-sandbox/plugins/spotbugs-idea/customPlugins")
}

tasks.register<Delete>("deleteThirdPartyPlugins") {
    delete ("build/resources/main/org/jetbrains/plugins/spotbugs/plugins")
}

tasks.compileJava {
    dependsOn(tasks["downloadThirdPartyPlugins"], tasks["copyGradleProperties"])
}
tasks.buildPlugin {
    dependsOn(tasks["copyThirdPartyPlugins"])
}
tasks.buildSearchableOptions {
    dependsOn(tasks["copyThirdPartyPlugins"])
}

tasks.withType<Test> {
    val ideaHomePath = "idea.home.path"
    // Set the IDEA_HOME_PATH environment variable to the IntelliJ IDEA Community sources that have been cloned locally.
    systemProperty(ideaHomePath, project.properties[ideaHomePath] ?: System.getenv("IDEA_HOME_PATH"))
}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                            "-Drobot-server.port=8082",
                            "-Dide.mac.message.dialogs.as.sheets=false",
                            "-Djb.privacy.policy.text=<!--999.999-->",
                            "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }
        }
    }
}

kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}
