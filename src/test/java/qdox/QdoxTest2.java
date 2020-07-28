package qdox;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.SpringMvcAnnotations;
import com.power.doc.model.ApiMethodDoc;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * qdox 测试
 *
 * @author llnn
 * @create 2020-07-28 15:02
 **/
public class QdoxTest2 {
    @Test
    public void getMeClass() throws IOException {
        JavaProjectBuilder builder = new JavaProjectBuilder();
//        builder.addSourceTree(new File("/Users/llnn/project_java/smart-doc-demo/src/main/java"));
        builder.addSourceTree(new File("/Users/llnn/Desktop/QDoc/qdoc-example/src/main/java"));

        //目录下的所有class
        System.out.println("====目录下的所有class====");
        Collection<JavaClass> classes = builder.getClasses();
        System.out.println(classes + "\n");

        /**
         * 1.如何识别是controller
         *      - smart-doc,识别springboot的注解方式
         *
         */
        for (JavaClass cls : classes) {
            if (!checkController(cls)) {
                continue;
            }
            /**
             * 根据spring注解,SpringMvcAnnotations来进行匹配,解析处理
             * requestParams，buildReturnApiParams，createDocRenderHeaders
             * 1. 如何判断是get还是post请求
             * 2. 获取get/post请求参数
             * 3. 获取响应返回值
             * 4. Header怎么设置
             */
            List<ApiMethodDoc> apiMethodDocs = buildControllerMethod(cls, builder);
        }

    }

    private boolean checkController(JavaClass cls) {
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String name = annotation.getType().getValue();
            if (SpringMvcAnnotations.CONTROLLER.equals(name) || SpringMvcAnnotations.REST_CONTROLLER.equals(name)) {
                return true;
            }
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.REST_API.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<ApiMethodDoc> buildControllerMethod(final JavaClass cls, JavaProjectBuilder projectBuilder) {

        String clazName = cls.getCanonicalName();
        System.out.println(clazName);
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        String baseUrl = "";
        for (JavaAnnotation annotation : classAnnotations) {
            String annotationName = annotation.getType().getValue();
            System.out.println(annotationName + " 是否是Controller: " + SpringMvcAnnotations.REST_CONTROLLER.equals(annotationName));
            if (annotationName.equals("QServiceDoc")) {
                System.out.println("QServiceDoc define: " + StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("define"))));
                System.out.println("QServiceDoc desc: " + StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("desc"))));
                System.out.println("QServiceDoc scene: " + StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("scene"))));
                System.out.println("QServiceDoc notice: " + StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter("notice"))));
            }
        }
        List<JavaMethod> methods = cls.getMethods();
        List<ApiMethodDoc> methodDocList = new ArrayList<>(methods.size());
        for (JavaMethod method : methods) {
            List<JavaAnnotation> methodAnnotations = method.getAnnotations();

            for (JavaAnnotation annotation : methodAnnotations) {
                String annotationName = annotation.getType().getValue();
                if (SpringMvcAnnotations.POST_MAPPING.equals(annotationName)) {
                    System.out.println("POST 请求");
                    System.out.println("URL: " + StringUtil.trimBlank(String.valueOf(annotation.getNamedParameter(DocAnnotationConstants.VALUE_PROP))));
                }
                System.out.println(annotationName);
            }
            List<JavaParameter> parameterList = method.getParameters();
            System.out.println("请求参数类型: " + method.getParameterTypes());
            System.out.println("请求参数:" + method.getParameters());
            for (JavaParameter parameter : parameterList) {
                String paramName = parameter.getName();
            }
        }
//        int methodOrder = 0;
//        for (JavaMethod method : methods) {
//            if (method.isPrivate()) {
//                continue;
//            }
//            if (StringUtil.isEmpty(method.getComment()) ) {
//                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
//            }
//            methodOrder++;
//            ApiMethodDoc apiMethodDoc = new ApiMethodDoc();
//            apiMethodDoc.setOrder(methodOrder);
//            apiMethodDoc.setDesc(method.getComment());
//            apiMethodDoc.setName(method.getName());
//            String methodUid = DocUtil.generateId(clazName + method.getName());
//            apiMethodDoc.setMethodId(methodUid);
//            String apiNoteValue = DocUtil.getNormalTagComments(method, DocTags.API_NOTE, cls.getName());
//            if (StringUtil.isEmpty(apiNoteValue)) {
//                apiNoteValue = method.getComment();
//            }
//            Map<String, String> authorMap = DocUtil.getParamsComments(method, DocTags.AUTHOR, cls.getName());
//            String authorValue = String.join(", ", new ArrayList<>(authorMap.keySet()));
//            apiMethodDoc.setDetail(apiNoteValue);
//            //handle request mapping
//            RequestMapping requestMapping = new SpringMVCRequestMappingHandler()
//                    .handle(projectBuilder.getServerUrl(), baseUrl, method, constantsMap);
//            //handle headers
//            List<ApiReqHeader> apiReqHeaders = new SpringMVCRequestHeaderHandler().handle(method);
//            apiMethodDoc.setRequestHeaders(apiReqHeaders);
//            if (Objects.nonNull(requestMapping)) {
//                if (null != method.getTagByName(IGNORE)) {
//                    continue;
//                }
//                apiMethodDoc.setType(requestMapping.getMethodType());
//                apiMethodDoc.setUrl(requestMapping.getUrl());
//                apiMethodDoc.setPath(requestMapping.getShortUrl());
//                apiMethodDoc.setDeprecated(requestMapping.isDeprecated());
//                // build request params
//                List<ApiParam> requestParams = requestParams(method, DocTags.PARAM, projectBuilder);
//                apiMethodDoc.setRequestParams(requestParams);
//                // build request json
//                ApiRequestExample requestExample = buildReqJson(method, apiMethodDoc, requestMapping.getMethodType(),
//                        projectBuilder);
//                String requestJson = requestExample.getExampleBody();
//                // set request example detail
//                apiMethodDoc.setRequestExample(requestExample);
//                apiMethodDoc.setRequestUsage(requestJson == null ? requestExample.getUrl() : requestJson);
//                // build response usage
//                apiMethodDoc.setResponseUsage(JsonBuildHelper.buildReturnJson(method, projectBuilder));
//                // build response params
//                List<ApiParam> responseParams = buildReturnApiParams(method, projectBuilder);
//                apiMethodDoc.setResponseParams(responseParams);
//                List<ApiReqHeader> allApiReqHeaders;
//                if (this.headers != null) {
//                    allApiReqHeaders = Stream.of(this.headers, apiReqHeaders)
//                            .flatMap(Collection::stream).distinct().collect(Collectors.toList());
//                } else {
//                    allApiReqHeaders = apiReqHeaders;
//                }
//                //reduce create in template
//                apiMethodDoc.setHeaders(this.createDocRenderHeaders(allApiReqHeaders, apiConfig.isAdoc()));
//                apiMethodDoc.setRequestHeaders(allApiReqHeaders);
//                methodDocList.add(apiMethodDoc);
//            }
//        }
        return methodDocList;
    }

}
