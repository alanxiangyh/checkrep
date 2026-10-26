# CheckRep

The annotation processor uses `javac` internals to instrument methods. Gradle
clients can apply the bundled Gradle plugin to add the required compiler
exports automatically.

You can check `checkrep-demo` for usage and test details.

### Gradle
```kotlin
plugins {
    java
    id("com.axiangyh.checkrep") version "1.0.1"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks.test {
    useJUnitPlatform()
}

// Optional: process only methods annotated with @VerifyRep.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Acheckrep.mode=release")
}
```
You may need to publish to local before use the plugin: `.\gradlew publishToMavenLocal`

### `javac` without Gradle
Build or download the CheckRep JAR (for a local build, run `./gradlew jar`; on Windows use `.\gradlew.bat jar`). Set `CHECKREP_JAR` to its path, for example `build/libs/checkrep-1.0.1.jar`. The JAR contains both the annotations and the annotation processor, so put it on both the class path and processor path.
```sh
javac \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
  -J--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
  --add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
  -cp "$CHECKREP_JAR" \
  -processorpath "$CHECKREP_JAR" \
  -Acheckrep.mode=test \
  -d out \
  src/main/java/com/example/MyType.java
```

`test` mode (the default) instruments constructors and public instance methods of `@CheckRep` classes, and instruments `@VerifyRep` methods. Use `-Acheckrep.mode=release` to process only `@VerifyRep` methods. Add other compile dependencies to `-cp` (separated with `:` on Unix/macOS or `;` on Windows), and include generated classes in the runtime class path as usual.
