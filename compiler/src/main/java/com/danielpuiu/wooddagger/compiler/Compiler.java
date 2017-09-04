package com.danielpuiu.wooddagger.compiler;

import com.danielpuiu.wooddagger.annotations.Getter;
import com.danielpuiu.wooddagger.annotations.Setter;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static com.sun.tools.javac.tree.JCTree.*;

/**
 * Created: 18/04/2017
 * Author:  dpuiu
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Compiler extends AbstractProcessor {

    private JavacElements trees;
    private TreeMaker treeMaker;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        trees = (JavacElements) processingEnvironment.getElementUtils();
        treeMaker = TreeMaker.instance(((JavacProcessingEnvironment) processingEnvironment).getContext());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element: roundEnv.getElementsAnnotatedWith(Getter.class)) {
            createGetMethod(element);
        }

        for (Element element: roundEnv.getElementsAnnotatedWith(Setter.class)) {
            createSetMethod(element);
        }

        return true;
    }

    private void createGetMethod(Element element) {
        String methodName = getMethodName(element, element.getAnnotation(Getter.class).name(), MethodType.GET);

        JCExpression expression = treeMaker.Ident((JCVariableDecl) trees.getTree(element));
        JCReturn statement = treeMaker.Return(expression);
        JCBlock body = treeMaker.Block(0, List.of(statement));

        createMethod(element, methodName, getElementType(element), List.nil(), body);
    }

    private void createSetMethod(Element element) {
        String methodName = getMethodName(element, element.getAnnotation(Setter.class).name(), MethodType.SET);

        Name name = trees.getName("new" + capitalize(element.getSimpleName().toString()));
        JCVariableDecl parameter = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), name, getElementType(element), null);

        JCExpression assignment = treeMaker.Assign(treeMaker.Ident((JCVariableDecl) trees.getTree(element)), treeMaker.Ident(name));
        JCStatement statement = treeMaker.Exec(assignment);
        JCBlock body = treeMaker.Block(0, List.of(statement));

        createMethod(element, methodName, treeMaker.TypeIdent(TypeTag.VOID), List.of(parameter), body);
    }

    private String getMethodName(Element element, String name, MethodType methodType) {
        if (name.isEmpty()) {
            name = methodType.type + capitalize(element.getSimpleName().toString());
        }

        return name;
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private JCExpression getElementType(Element element) {
        String type = element.asType().toString();
        Symbol.ClassSymbol symbol = trees.getTypeElement(type);

        return symbol == null? treeMaker.TypeIdent(TypeTag.valueOf(type.toUpperCase())): treeMaker.Ident(symbol);
    }

    private void createMethod(Element element, String methodName, JCExpression returnType, List<JCVariableDecl> parameters, JCBlock body) {
        JCModifiers modifiers = element.getModifiers().contains(Modifier.STATIC)? treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC): treeMaker.Modifiers(Flags.PUBLIC);
        JCMethodDecl method = treeMaker.MethodDef(modifiers, trees.getName(methodName), returnType, List.nil(), parameters, List.nil(), body, null);

        addNewMethodToClassMembers(element, method);
    }

    private void addNewMethodToClassMembers(Element element, JCMethodDecl method) {
        List<JCTree> members = getClassMembers(element);
        members.setTail(getNewTail(members.tail, method));
    }

    private List<JCTree> getClassMembers(Element element) {
        TypeElement parent = (TypeElement) element.getEnclosingElement();
        JCClassDecl classDeclaration = (JCClassDecl) trees.getTree(parent);

        return classDeclaration.getMembers();
    }

    private List<JCTree> getNewTail(List<JCTree> tail, JCMethodDecl method) {
        ListBuffer<JCTree> newTail = new ListBuffer<>();
        newTail.addAll(tail);
        newTail.add(method);

        return newTail.toList();
    }
}

