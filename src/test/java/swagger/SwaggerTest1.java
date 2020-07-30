package swagger;

import io.swagger.util.Json;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.Test;

import java.io.IOException;

/**
 * @author wangyafei.wang
 * @create 2020-07-30 11:40
 **/
public class SwaggerTest1 {
    @Test
    public void testParseClassBuildObject() throws IOException {
        Components components = new Components();
        components.addResponses("invalidJWT", new ApiResponse().description("when JWT token invalid/expired"));
        OpenAPI oas = new OpenAPI()
                .info(new Info().description("info").title("title").version("1.0"))
                .components(components);
        Reader reader = new Reader(oas);

        OpenAPI openAPI = reader.read(SimpleResponsesResource.class);
        String swaggerJson = Json.mapper().writeValueAsString(openAPI);
        System.out.println(swaggerJson);
    }

    @Test
    public void testBuildOpenAPIObject() throws IOException {
        OpenAPI swagger = new OpenAPI();
        PathItem expectedPath = new PathItem();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("xxx");

        ApiResponses apiResponses = new ApiResponses().addApiResponse("200", apiResponse);
        Operation operation = new Operation();
        operation.summary("summary").description("description");
        operation.responses(apiResponses);
        expectedPath.setGet(operation);
        swagger.setPaths(new Paths().addPathItem("/health", expectedPath));
        swagger.setInfo(new Info());
        swagger.getInfo().setVersion("1.0");
        swagger.getInfo().setTitle("info title");
        String swaggerJson = Json.mapper().writeValueAsString(swagger);
        System.out.println(swaggerJson);
    }
}
