plugins {
    id("java")
    id("war")
}

group = "com.tarasov"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework:spring-framework-bom:6.2.11"))
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework:spring-aspects")
    implementation("org.springframework.data:spring-data-jdbc:3.5.4")
    implementation("org.postgresql:postgresql:42.7.8")
    testImplementation("org.springframework:spring-test")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.20.0"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("com.h2database:h2:2.4.240")

    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.2")
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-api:2.25.2")
}

tasks.test {
    useJUnitPlatform()
}