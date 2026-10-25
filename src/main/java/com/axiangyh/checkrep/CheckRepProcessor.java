package com.axiangyh.checkrep;

import com.axiangyh.checkrep.annotation.CheckRep;
import com.axiangyh.checkrep.annotation.VerifyRep;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.util.ArrayList;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"com.axiangyh.checkrep.annotation.CheckRep", "com.axiangyh.checkrep.annotation.VerifyRep"})
@SupportedOptions(CheckRepProcessor.MODE_OPTION)
public final class CheckRepProcessor extends AbstractProcessor {

    public static final String MODE_OPTION = "checkrep.mode";
    private TreeMaker maker;
    private JavacTrees trees;

    private JCTree.JCStatement checkRepCall;

    @Override
    public synchronized void init(javax.annotation.processing.ProcessingEnvironment environment) {
        super.init(environment);
        Context context = ((JavacProcessingEnvironment) environment).getContext();
        maker = TreeMaker.instance(context);
        trees = JavacTrees.instance(context);

        checkRepCall = maker.Exec(maker.Apply(
            List.nil(),
            maker.Ident(Names.instance(context).fromString("checkRep")),
            List.nil()
        ));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        String mode = processingEnv.getOptions().getOrDefault(MODE_OPTION, "test");
        switch (mode) {
            case "test": {
                for (Element element : roundEnvironment.getElementsAnnotatedWith(CheckRep.class)) {
                    processClass((TypeElement) element);
                }
                // Intentional fall through, VerifyRep should be process in all mode
            }
            case "release": {
                for (Element element : roundEnvironment.getElementsAnnotatedWith(VerifyRep.class)) {
                    processMethod((ExecutableElement) element);
                }
                break;
            }
            default: {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR, "-A" + MODE_OPTION + " must be either test or release");
                return true;
            }
        }
        return true;
    }

    private void processClass(TypeElement type) {
        ExecutableElement checkRep = type.getEnclosedElements().stream()
            .filter(element -> element.getKind() == ElementKind.METHOD)
            .map(ExecutableElement.class::cast)
            .filter(method -> method.getSimpleName().contentEquals("checkRep")
                && method.getModifiers().contains(Modifier.PRIVATE)
                && method.getReturnType().getKind().name().equals("VOID")
                && method.getParameters().isEmpty())
            .findFirst().orElse(null);
        if (checkRep == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "@CheckRep class must declare private void checkRep()", type);
            return;
        }
        JCTree.JCClassDecl declaration = trees.getTree(type);
        for (JCTree definition : declaration.defs) {
            if (definition instanceof JCTree.JCMethodDecl method && method.body != null) {
                if (method.name.contentEquals("<init>")) {
                    instrumentConstructor(method);
                } else if ((method.mods.flags & Flags.PUBLIC) != 0
                    && !method.name.contentEquals("checkRep")
                    && (method.mods.flags & Flags.STATIC) == 0) {
                    instrument(method);
                }
            }
        }
    }

    private void processMethod(ExecutableElement element) {
        if (element.getKind() != ElementKind.METHOD) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "@VerifyRep can only annotate a method", element);
            return;
        }
        JCTree.JCMethodDecl method = trees.getTree(element);
        if (method != null && method.body != null) {
            if ((method.mods.flags & Flags.STATIC) != 0) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "@VerifyRep cannot annotate a static method", element);
            } else {
                instrument(method);
            }
        }
    }

    private void instrument(JCTree.JCMethodDecl method) {
        List<JCTree.JCStatement> stats = method.body.stats;
        JCTree.JCStatement firstStatement = !stats.isEmpty() ? stats.head : null;
        JCTree.JCStatement lastStatement = !stats.isEmpty() ? stats.last() : null;
        TreeTranslator translator = new TreeTranslator() {
            @Override
            public void visitReturn(JCTree.JCReturn tree) {
                result = tree == firstStatement ? tree : maker.Block(0, List.of(checkRepCall, tree));
            }

            @Override
            public void visitLambda(JCTree.JCLambda tree) {
                result = tree;
            }

            @Override
            public void visitClassDef(JCTree.JCClassDecl tree) {
                result = tree;
            }
        };
        ArrayList<JCTree.JCStatement> newStats = new ArrayList<>();
        newStats.add(checkRepCall);
        for (JCTree.JCStatement statement : method.body.stats) {
            newStats.add(translator.translate(statement));
        }
        if (!stats.isEmpty() && !(lastStatement instanceof JCTree.JCReturn)) {
            newStats.add(checkRepCall);
        }
        method.body.stats = List.from(newStats);
    }

    private void instrumentConstructor(JCTree.JCMethodDecl constructor) {
        constructor.body.stats = constructor.body.stats.append(checkRepCall);
    }
}
