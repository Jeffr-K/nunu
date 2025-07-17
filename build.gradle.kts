plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.jooq.jooq-codegen-gradle") version "3.20.5"
}

group = "com.haco"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }

    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "ch.qos.logback", module = "logback-core")
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JOOQ 의존성
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.20.0")
    implementation("org.jooq:jooq-codegen:3.20.0")
    jooqCodegen("com.mysql:mysql-connector-j")
    jooqCodegen("org.jooq:jooq-meta:3.20.0")
    jooqCodegen("org.jooq:jooq-codegen:3.20.0")
    jooqCodegen("org.jooq:jooq:3.20.0")


    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// JOOQ 설정 파일 생성
tasks.register("generateJooqConfig") {
    doLast {
        val configFile = file("src/main/resources/jooq-config.xml")
        configFile.parentFile.mkdirs()
        configFile.writeText(
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <configuration xmlns="http://www.jooq.org/xsd/jooq-codegen-3.20.0.xsd">
                <logging>WARN</logging>
                <jdbc>
                    <driver>com.mysql.cj.jdbc.Driver</driver>
                    <url>jdbc:mysql://localhost:3306/haco_shop_db</url>
                    <user>oscarous</user>
                    <password>Haco${'$'}hop2024!</password>
                </jdbc>
                <generator>
                    <name>org.jooq.codegen.KotlinGenerator</name>
                    <database>
                        <name>org.jooq.meta.mysql.MySQLDatabase</name>
                        <inputSchema>haco_shop_db</inputSchema>
                        <excludes>flyway_schema_history</excludes>
                    </database>
                    <target>
                        <packageName>com.haco.shop.infrastructure.jooq.generated</packageName>
                        <directory>src/main/kotlin</directory>
                    </target>
                    <generate>
                        <records>true</records>
                        <pojos>true</pojos>
                        <immutablePojos>false</immutablePojos>
                        <fluentSetters>true</fluentSetters>
                        <javaTimeTypes>true</javaTimeTypes>
                    </generate>
                </generator>
            </configuration>
        """.trimIndent()
        )
    }
}

task("jooqCodegenTask", JavaExec::class) {
    dependsOn("generateJooqConfig")
    group = "jooq"
    description = "Generate JOOQ classes"
    classpath = configurations.getByName("jooqCodegen")
    mainClass.set("org.jooq.codegen.GenerationTool")
    args = listOf("src/main/resources/jooq-config.xml")
}