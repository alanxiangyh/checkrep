# CheckRep

The annotation processor uses `javac` internals to instrument methods. Gradle
clients can apply the bundled Gradle plugin to add the required compiler
exports automatically.

You can check `checkrep-demo` for usage and test details.
```kotlin
plugins {
    java
    id("com.axiangyh.checkrep") version "1.0.1"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks.test {
    useJUnitPlatform()
}
```

You may need to publish to local before use the plugin: `.\gradlew publishToMavenLocal`

