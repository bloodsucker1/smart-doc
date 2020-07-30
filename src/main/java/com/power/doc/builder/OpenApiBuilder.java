/*
 * smart-doc https://github.com/shalousun/smart-doc
 *
 * Copyright (C) 2018-2020 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.builder;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiParam;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.template.SpringBootDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;
import io.swagger.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yu 2019/11/21.
 */
public class OpenApiBuilder {

    private static final String GET_METHOD = "get";
    private static final String POST_METHOD = "post";
    private static final String PUT_METHOD = "put";
    private static final String DELETE_METHOD = "delete";
    private static final String PATCH_METHOD = "patch";
    private static final String TRACE_METHOD = "trace";
    private static final String HEAD_METHOD = "head";
    private static final String OPTIONS_METHOD = "options";


    /**
     * 构建postman json
     *
     * @param config 配置文件
     */
    public static void buildPostmanCollection(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        JavaProjectBuilder javaProjectBuilder = new JavaProjectBuilder();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        openApiCreate(config, configBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config         ApiConfig Object
     * @param projectBuilder QDOX avaProjectBuilder
     */
    public static void buildPostmanCollection(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        openApiCreate(config, configBuilder);
    }


    private static void openApiCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
        IDocBuildTemplate docBuildTemplate = new SpringBootDocBuildTemplate();
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        List<OpenAPI> OpenApiList = new ArrayList<OpenAPI>();
        apiDocList.forEach(
                apiDoc -> {
                    OpenAPI openApiObj = buildOpenAPIObj(apiDoc);
                    openApiObj.info(new Info().title(apiDoc.getName()).description(apiDoc.getDesc()).version("1.0"));
                    String swaggerJson = null;
                    try {
                        swaggerJson = Json.mapper().writeValueAsString(openApiObj);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    System.out.println(swaggerJson);
                    OpenApiList.add(openApiObj);
                }
        );

    }

    private static OpenAPI buildOpenAPIObj(ApiDoc apiDoc) {
        OpenAPI openApiObj = new OpenAPI();
        Paths paths = new Paths();
        apiDoc.getList().forEach(
                apiMethodDoc -> {
                    PathItem pathItemObject = new PathItem();
                    Operation operation = new Operation();
                    operation.operationId(apiMethodDoc.getMethodId());
                    operation.description(apiMethodDoc.getDesc());
                    List<ApiParam> requestParams = apiMethodDoc.getRequestParams();

                    requestParams.forEach(
                            apiParam -> {
                                System.out.println(apiParam.getField());
                                Schema schema = new Schema();
                                schema.setType(apiParam.getType());
                                schema.setExampleSetFlag(true);
                                Parameter parameter = new Parameter();
                                parameter.setName(apiParam.getField());
                                parameter.setRequired(apiParam.isRequired());
                                parameter.setDescription(apiParam.getDesc());
                                parameter.setIn("query");
                                parameter.schema(schema);
                                operation.addParametersItem(parameter);
                            }
                    );
                    setPathItemOperation(pathItemObject, apiMethodDoc.getType().toLowerCase(), operation);
                    RequestBody requestBody = new RequestBody().content(new Content().addMediaType(apiMethodDoc.getContentType(), new MediaType()));
                    operation.requestBody(requestBody);
                    ApiResponses apiResponses = new ApiResponses();
                    ApiResponse apiResponse = new ApiResponse();
                    apiResponse.setDescription(apiMethodDoc.getResponseUsage());
                    operation.responses(apiResponses.addApiResponse("default", apiResponse));
                    String shortUrl = apiMethodDoc.getUrl().replace(apiMethodDoc.getServerUrl(), "");
                    paths.addPathItem(shortUrl, pathItemObject);
                }

        );
        openApiObj.setPaths(paths);
        return openApiObj;
    }

    private static void setPathItemOperation(PathItem pathItemObject, String method, Operation operation) {
        switch (method) {
            case POST_METHOD:
                pathItemObject.post(operation);
                break;
            case GET_METHOD:
                pathItemObject.get(operation);
                break;
            case DELETE_METHOD:
                pathItemObject.delete(operation);
                break;
            case PUT_METHOD:
                pathItemObject.put(operation);
                break;
            case PATCH_METHOD:
                pathItemObject.patch(operation);
                break;
            case TRACE_METHOD:
                pathItemObject.trace(operation);
                break;
            case HEAD_METHOD:
                pathItemObject.head(operation);
                break;
            case OPTIONS_METHOD:
                pathItemObject.options(operation);
                break;
            default:
                // Do nothing here
                break;
        }
    }
}
