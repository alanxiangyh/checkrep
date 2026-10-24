plugins {
    java
    `java-library`
    `java-gradle-plugin`
    signing
    id("com.vanniktech.maven.publish") version "0.36.0"
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
    plugins {
        create("CheckRep") {
            id = "com.axiangyh.checkrep"
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
