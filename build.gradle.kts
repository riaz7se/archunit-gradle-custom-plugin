plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    `java-library`
    `maven-publish`
    `java-gradle-plugin`
}

group = "com.iz.arunit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    testCompileOnly(gradleTestKit())

    implementation("com.tngtech.archunit:archunit-junit5-engine:1.0.0-rc1")
    implementation("com.tngtech.archunit:archunit-junit5-api:1.0.0-rc1")
    implementation("com.tngtech.archunit:archunit-junit5:1.0.0-rc1")

}

gradlePlugin {
    plugins {
        create("izArchUnitPlugin") {
            id = "com.iz.arunit"
            implementationClass = "com.iz.arunit.IzArchUnitPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.iz.arunit"
            artifactId = "iz-arunit"
            version = "1.0.0-SNAPSHOT"

            from(components["java"])
        }
    }
}