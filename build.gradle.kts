import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
//    id("io.sentry.jvm.gradle") version "4.4.1"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
//    id("com.google.protobuf") version "0.9.4"
}

group = "pl.slaszu"
version = "2.2.0"
tasks.bootJar {
    this.archiveFileName.set("${project.name}.jar")
}
//
//sentry {
//    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
//    // This enables source context, allowing you to see your source
//    // code as part of your stack traces in Sentry.
//    //includeSourceContext = true
//
//    org = "piotr-flasza"
//    projectName = "stockanalyzer"
//    authToken = System.getenv("SENTRY_AUTH_TOKEN")
//}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

springBoot {
    buildInfo()
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("io.github.redouane59.twitter:twittered:2.23")
    implementation("com.github.hkirk:java-html2image:0.9")
    implementation("com.twitter.twittertext:twitter-text:3.1.0")
    implementation("io.qdrant:client:1.9.1")
    implementation("com.google.protobuf:protobuf-java:3.24.0")

    implementation("org.jetbrains.bio:viktor:1.2.0")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.7.0")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.3.0")

    implementation("com.google.apis:google-api-services-blogger:v3-rev20221220-2.0.0")
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.18.1")
    testImplementation("org.testcontainers:junit-jupiter:1.18.1")
    testImplementation("org.testcontainers:mongodb:1.18.1")
    testImplementation("com.tngtech.archunit:archunit:1.3.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//tasks.register<Copy>("copyConfigCredentials") {
//	from(layout.projectDirectory.dir("config_credentials"))
//	into(layout.buildDirectory.dir("resources/main/config_credentials"))
//}
//tasks.named("processResources") {
//	dependsOn(tasks.named("copyConfigCredentials"))
//}
