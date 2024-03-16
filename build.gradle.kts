import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
	kotlin("plugin.jpa") version "1.9.20"
}

group = "pl.slaszu"
version = "1.0.6"
tasks.bootJar {
	this.archiveFileName.set("${project.name}.jar")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

springBoot{
	buildInfo()
}

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
//	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
	implementation("org.jfree:jfreechart:1.5.3")
	implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
	implementation("io.github.redouane59.twitter:twittered:2.23")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:testcontainers:1.18.1")
	testImplementation("org.testcontainers:junit-jupiter:1.18.1")
	testImplementation("org.testcontainers:mongodb:1.18.1")
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
