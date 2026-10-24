package com.axiangyh.checkrep;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;

public final class CheckRepGradlePlugin implements Plugin<Project> {

    private static final List<String> JAVAC_EXPORTS = List.of(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    );

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("java", appliedPlugin -> {
            Object pluginArtifact = project.files(getPluginArtifact());
            project.getDependencies().add("compileOnly", pluginArtifact);
            project.getDependencies().add("runtimeOnly", pluginArtifact);
            project.getDependencies().add("annotationProcessor", pluginArtifact);
        });

        project.getTasks().withType(JavaCompile.class).configureEach(task -> {
            task.getOptions().setFork(true);
            List<String> jvmArgs = task.getOptions().getForkOptions().getJvmArgs();
            for (String export : JAVAC_EXPORTS) {
                if (!jvmArgs.contains(export)) {
                    jvmArgs.add(export);
                }
                if (!task.getOptions().getCompilerArgs().contains(export)) {
                    task.getOptions().getCompilerArgs().add(export);
                }
            }
        });
    }

    private File getPluginArtifact() {
        try {
            return new File(CheckRepGradlePlugin.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        } catch (URISyntaxException exception) {
            throw new GradleException("Could not locate CheckRep plugin artifact", exception);
        }
    }
}
