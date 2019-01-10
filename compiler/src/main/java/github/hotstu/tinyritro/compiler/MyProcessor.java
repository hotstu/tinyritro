package github.hotstu.tinyritro.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import github.hotstu.tinyritro.lib.anotations.EntryPoint;
import github.hotstu.tinyritro.lib.anotations.PathParam;
import github.hotstu.tinyritro.lib.anotations.Query;
import github.hotstu.tinyritro.lib.anotations.QueryParam;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"github.hotstu.tinyritro.lib.anotations.Query"})
public class MyProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements utils;
    private Types typeUtils;
    private static final String FLOWABLE_TYPE = "io.reactivex.Flowable";
    private static final String RXFETCH_TYPE = "github.hotstu.labo.rxfetch.RxFetch";
    private static final String UrlFormatter_TYPE = "github.hotstu.tinyritro.lib.http.UrlFormatter";
    private static final String OKHTTPCLIENT_TYPE = "okhttp3.OkHttpClient";
    private static final String TYPETOKEN_TYPE = "com.google.gson.reflect.TypeToken";
    private static final String PAKAGE_NAME = "github.hotstu.tinyritro.gen";
    private static final String FILE_NAME = "TinyRitro";
    private ClassName rxFetchClass;
    private ClassName okhttpclientClass;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        info("MyProcessor init");

        filer = processingEnvironment.getFiler();
        utils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        rxFetchClass = ClassName.bestGuess(RXFETCH_TYPE);
        okhttpclientClass = ClassName.bestGuess(OKHTTPCLIENT_TYPE);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        info("getSupportedSourceVersion?...,");
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        info("processingOver?...,");

        info("processingOver?...," + roundEnvironment.processingOver());
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Query.class);
        if (elements.size() == 0) {
            return true;
        }
        Map<TypeElement, Set<ExecutableElement>> builderMap = new LinkedHashMap<>();
        for (Element element : elements) {
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement enclosingElement = ((TypeElement) executableElement.getEnclosingElement());

            EntryPoint annotation = enclosingElement.getAnnotation(EntryPoint.class);
            if (annotation == null) {
                continue;
            }
            info("process...EntryPoint" + annotation);
            if (builderMap.get(enclosingElement) == null) {
                builderMap.put(enclosingElement, new HashSet<ExecutableElement>());
            }
            builderMap.get(enclosingElement).add(executableElement);
        }

        info("process..." + builderMap);
        for (TypeElement typeElement : builderMap.keySet()) {
            EntryPoint entryAnotation = typeElement.getAnnotation(EntryPoint.class);
            final String fileName = filename(entryAnotation, typeElement);
            final String entryPointValue = entryAnotation.value();
            PackageElement packageOf = utils.getPackageOf(typeElement);
            info("build..." + packageOf);
            TypeSpec.Builder impl = TypeSpec.classBuilder(fileName)
                    .addSuperinterface(TypeName.get(typeElement.asType()))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(FieldSpec.builder(
                            rxFetchClass,
                            "rxFetch",
                            Modifier.PRIVATE, Modifier.FINAL)
                            .build())
                    .addMethod(MethodSpec.constructorBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(rxFetchClass, "rxFetch")
                            .addStatement("this.rxFetch = rxFetch")
                            .build()
                    );
            Set<ExecutableElement> executableElements = builderMap.get(typeElement);
            for (ExecutableElement executableElement : executableElements) {
                TypeMirror returnType = executableElement.getReturnType();
                String erasureType = doubleErasure(returnType);
                TypeMirror TargetJsonClass;
                if (returnType instanceof DeclaredType) {
                    if (!FLOWABLE_TYPE.equals(erasureType)) {
                        error("返回类型必须是如Flowable<?>的形式，当前为" + returnType);
                        continue;
                    }
                    DeclaredType typeVariable = (DeclaredType) returnType;
                    List<? extends TypeMirror> typeArguments = typeVariable.getTypeArguments();
                    TargetJsonClass = typeArguments.get(0);
                    info("DeclaredType..." + typeArguments);
                } else {
                    error("返回类型必须是如Flowable<?>的形式，当前为" + returnType);
                    continue;
                }
                info("method..." + erasureType);

                List<? extends VariableElement> parameters = executableElement.getParameters();
                List<ParameterSpec> parameterSpecs = new ArrayList<>();
                List<VariableElement> pathParams = new ArrayList<>();
                List<VariableElement> queryParams = new ArrayList<>();
                ParameterSpec mapParam = null;
                for (VariableElement parameter : parameters) {
                    PathParam pathParam = parameter.getAnnotation(PathParam.class);
                    QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
                    //info("-parameter annotation:"+pathParam + queryParam);
                    ParameterSpec parameterSpec = ParameterSpec.get(parameter);
                    parameterSpecs.add(parameterSpec);
                    if (pathParam != null) {
                        pathParams.add(parameter);
                    } else if (queryParam != null) {
                        queryParams.add(parameter);
                    } else {
                        //判断是否是map类型
                        boolean assignable = typeUtils.isAssignable(
                                utils.getTypeElement(doubleErasure(parameter.asType())).asType(),
                                utils.getTypeElement("java.util.Map").asType());
                        info("parameter isAssignable:" + assignable + parameter.asType());
                        if (assignable) {
                            mapParam = parameterSpec;
                        }
                    }

                }

                Query annotation = executableElement.getAnnotation(Query.class);
                MethodSpec.Builder builder = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(executableElement.getReturnType()))
                        .addParameters(parameterSpecs);
                final String url = "".equals(annotation.url()) ? entryPointValue + annotation.path() : annotation.url();
                // 支持传入参入合并到请求地址中
                if (pathParams.size() > 0) {
                    builder.addStatement("$T<String, String> urlParam = new $T<>()", Map.class, HashMap.class);
                    for (int i = 0; i < pathParams.size(); i++) {
                        VariableElement variableElement = pathParams.get(i);
                        PathParam ano = variableElement.getAnnotation(PathParam.class);
                        String key;
                        if ("".equals(ano.value())) {
                            key = variableElement.getSimpleName().toString();
                        } else {
                            key = ano.value();
                        }
                        builder.addStatement("urlParam.put($S, $L+\"\")", key, variableElement.getSimpleName().toString());
                    }
                    builder.addStatement("String url = $T.format($S,$L)",
                            ClassName.bestGuess(UrlFormatter_TYPE),
                            url,
                            "urlParam");
                } else {
                    builder.addStatement("String url = $S",
                            "".equals(annotation.url()) ? entryPointValue + annotation.path() : annotation.url());
                }
                String paramsName;
                if (mapParam == null) {
                    builder.addStatement("Map<String, String> __params = new HashMap<>()");
                    paramsName = "__params";
                } else {
                    paramsName = mapParam.name;
                }
                if (queryParams.size() > 0) {
                    for (int i = 0; i < queryParams.size(); i++) {
                        VariableElement variableElement = queryParams.get(i);
                        QueryParam ano = variableElement.getAnnotation(QueryParam.class);
                        String key;
                        if ("".equals(ano.value())) {
                            key = variableElement.getSimpleName().toString();
                        } else {
                            key = ano.value();
                        }
                        builder.addStatement("$L.put($S, $L)", paramsName, key, variableElement.getSimpleName().toString());
                    }
                }
                builder.addStatement("return rxFetch.<$T>$L($L, $L, new $T<$T>(){}.getType())",
                        ClassName.get(TargetJsonClass),
                        annotation.method().getValue(),
                        "url",
                        paramsName,
                        ClassName.bestGuess(TYPETOKEN_TYPE),
                        ClassName.get(TargetJsonClass));

                impl.addMethod(builder.build());
            }
            JavaFile javaFile = JavaFile.builder(packageOf.asType().toString(), impl.build()).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        //brew TinyRitro.java
        ClassName tinyRitroClass = ClassName.get(PAKAGE_NAME, FILE_NAME);
        ClassName tinyRitroBuilderClass = ClassName.get(tinyRitroClass.toString(), "Builder");
        TypeSpec builderSpec = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addField(FieldSpec.builder(okhttpclientClass, "client", Modifier.PRIVATE).build())
                .addMethod(MethodSpec.methodBuilder("client")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addParameter(okhttpclientClass, "client")
                        .returns(tinyRitroBuilderClass)
                        .addStatement("this.client = client")
                        .addStatement("return this")
                        .build()
                )
                .addMethod(MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addStatement("return new TinyRitro(this.client)")
                        .returns(tinyRitroClass)
                        .build()
                ).build();
        TypeSpec.Builder rxFetchSpecBuilder = TypeSpec.classBuilder(FILE_NAME)
                .addJavadoc("auto generated by tinyRitro")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(rxFetchClass, "rxFetch", Modifier.PRIVATE, Modifier.FINAL).build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(okhttpclientClass, "client")
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("this.rxFetch = new $T(client)", rxFetchClass)
                        .build()
                )
                .addType(builderSpec);
        for (TypeElement typeElement : builderMap.keySet()) {
            //将所有entryPoint放入到入口类中，并提供get方法
            EntryPoint entryAnotation = typeElement.getAnnotation(EntryPoint.class);
            if (entryAnotation == null) {
                continue;
            }
            final String fileName = filename(entryAnotation, typeElement);
            PackageElement packageOf = utils.getPackageOf(typeElement);
            ClassName implClassName = ClassName.get(packageOf.asType().toString(), fileName);
            ClassName interfaceClass = ClassName.get(typeElement);
            String instanceName = "m" + interfaceClass.simpleName();
            rxFetchSpecBuilder.addField(FieldSpec.builder(implClassName, instanceName, Modifier.PRIVATE).build());
            rxFetchSpecBuilder.addMethod(MethodSpec.methodBuilder("get" + interfaceClass.simpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(interfaceClass)
                    .beginControlFlow("if ($L == null)", instanceName)
                    .beginControlFlow("synchronized ($T.class)", implClassName)
                    .beginControlFlow("if ($L == null)", instanceName)
                    .addStatement("$L = new $T(rxFetch)", instanceName, implClassName)
                    .endControlFlow()
                    .endControlFlow()
                    .endControlFlow()
                    .addStatement("return $L", instanceName)
                    .build()
            );
        }
        JavaFile javaFile = JavaFile.builder(PAKAGE_NAME, rxFetchSpecBuilder.build()).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }

    private void error(String msg, Object... args) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    /**
     * Uses both {@link Types#erasure} and string manipulation to strip any generic types.
     */
    private String doubleErasure(TypeMirror elementType) {
        String name = typeUtils.erasure(elementType).toString();
        int typeParamStart = name.indexOf('<');
        if (typeParamStart != -1) {
            name = name.substring(0, typeParamStart);
        }
        return name;
    }

    private String filename(EntryPoint entryAnotation, TypeElement typeElement) {
        return "".equals(entryAnotation.name()) ? typeElement.getSimpleName().toString() + "Impl" : entryAnotation.name();

    }
}
