plugins {
    java
    `java-library`
    `java-gradle-plugin`
    signing
    id("com.vanniktech.maven.publish") version "0.36.0"
    //id("com.gradle.plugin-publish") version "2.2.1"
}

group = "com.axiangyh"
version = "1.0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://github.com/alanxiangyh/checkrep"
    vcsUrl = "https://github.com/alanxiangyh/checkrep.git"
    plugins {
        create("checkRep") {
            id = "com.axiangyh.checkrep"
            displayName = "Plugin to simplify #checkRep call in testing ADT"
            description = "An annotation based gradle plugin to simplify #checkRep call in testing"
            tags = listOf("testing", "unitTesting")
            implementationClass = "com.axiangyh.checkrep.CheckRepGradlePlugin"
        }
    }
}

val javacExports = listOf(
    "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
)

tasks.withType<JavaCompile>().configureEach {
    options.isFork = true
    options.forkOptions.jvmArgs = javacExports
    options.compilerArgs.addAll(javacExports)
}

tasks.withType<Javadoc> {
    isFailOnError = false
}