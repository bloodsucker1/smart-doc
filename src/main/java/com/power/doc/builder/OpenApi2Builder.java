package com.power.doc.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.power.common.util.StringUtil;
import com.power.doc.constants.*;
import com.power.doc.handler.QunarAnnotionHandler;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.qunar.ServiceDocModel;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import io.swagger.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * OpenApi2.0支持
 *
 * @author llnn
 * @create 2020-07-29 12:16
 **/
public class OpenApi2Builder {
    /**
     * 构建postman json
     *
     * @param config 配置文件
     */
    public static void buildApiDoc(ApiConfig config) throws JsonProcessingException {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        javaProjectBuilder.addSourceTree(new File("/Users/llnn/Desktop/QDoc/qdoc-example/src/main/java"));
        List<OpenAPI> openApiObjList = buildOpenAipObject(configBuilder);
        for (OpenAPI openApiObj : openApiObjList) {
            String swaggerJson = Json.mapper().writeValueAsString(openApiObj);
            System.out.println(swaggerJson);
        }
    }

    private static List<OpenAPI> buildOpenAipObject(ProjectDocConfigBuilder projectDocConfigBuilder) {
        JavaProjectBuilder javaProjectBuilder = projectDocConfigBuilder.getJavaProjectBuilder();
        List<OpenAPI> openApiObjList = new ArrayList<>();
        Collection<JavaClass> classes = javaProjectBuilder.getClasses();
        for (JavaClass cls : classes) {
            if (!checkController(cls)) {
                continue;
            }
            OpenAPI openApiObj = buildControllerMethod(cls, projectDocConfigBuilder);
            openApiObjList.add(openApiObj);
        }
        return openApiObjList;
    }

    private static boolean checkController(JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String name = annotation.getType().getValue();
            if (SpringMvcAnnotations.CONTROLLER.equals(name) || SpringMvcAnnotations.REST_CONTROLLER.equals(name) || QunarAnnotationConstans.Q_SERVICE_DOC.equals(name)) {
                return true;
            }
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.REST_API.equals(value) || DocTags.Q_Interface_Doc.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static OpenAPI buildControllerMethod(final JavaClass cls, ProjectDocConfigBuilder projectBuilder) {

        String clazName = cls.getCanonicalName();
        System.out.println(clazName);
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        String baseUrl = "";
        OpenAPI openApiObj = new OpenAPI();
        ServiceDocModel serviceDocModel = new ServiceDocModel();

        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getValue();
            if (DocAnnotationConstants.REQUEST_MAPPING.equals(annotationName) || DocGlobalConstants.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                if (annotation.getNamedParameter("value") != null) {
                    baseUrl = StringUtil.removeQuotes(annotation.getNamedParameter("value").toString());
                }
            }
            if (QunarAnnotationConstans.Q_SERVICE_DOC.equals(annotationName)) {
                serviceDocModel = new QunarAnnotionHandler().serviceDocHandler(annotation);
            }
        }
        openApiObj.info(new Info().title(serviceDocModel.getDefine()).description(serviceDocModel.getDesc()).version("1.0"));

        List<JavaMethod> methods = cls.getMethods();
        Paths paths = new Paths();
        for (JavaMethod method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            PathItem pathItemObject = new PathItem();
            String pathString = "";
            Operation operation = new Operation();
            List<JavaAnnotation> methodAnnotations = method.getAnnotations();
            for (JavaAnnotation annotation : methodAnnotations) {
                String annotationName = annotation.getType().getValue();
                if (SpringMvcAnnotations.POST_MAPPING.equals(annotationName)) {
                    pathString = StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter(DocAnnotationConstants.VALUE_PROP)));
                    pathItemObject.post(operation);
                }
                if (SpringMvcAnnotations.GET_MAPPING.equals(annotationName)) {
                    pathString = StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter(DocAnnotationConstants.VALUE_PROP)));
                    pathItemObject.get(operation);
                }
            }


            System.out.println("请求参数类型: " + method.getParameterTypes());
            System.out.println("请求参数:" + method.getParameters());

            List<Parameter> parameters = new ArrayList<Parameter>();


            List<DocletTag> docletTags = method.getTags();
            List<JavaParameter> parameterList = method.getParameters();

            for (JavaParameter parameter : parameterList) {
                String paramName = parameter.getName();
            }
            if (StringUtil.isEmpty(pathString)) {
                paths.addPathItem(pathString, pathItemObject);
            }

            String methodUid = DocUtil.generateId(clazName + method.getName());
            operation.operationId(methodUid);


        }
        openApiObj.setPaths(paths);
        return openApiObj;
    }

}
