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
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}