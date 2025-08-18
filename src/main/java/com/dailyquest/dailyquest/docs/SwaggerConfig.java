package com.dailyquest.dailyquest.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Configuration
@Profile("!prod")
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DailyQuest API 문서") //제목
                        .description("모바일 게임 숙제 관리 서비스 API 문서") // 설명
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // 모든 Operation에 Bearer 요구사항 기본 적용
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    }

    // /api/** 만 문서화, 내부 엔드포인트 제외
    @Bean
    public GroupedOpenApi publicApi(OpenApiCustomizer standardErrorResponses) {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .pathsToExclude("/actuator/**", "/error")
                .addOpenApiCustomizer(standardErrorResponses)
                .build();
    }

    // Swagger 문서에 “공통 에러 응답(400/401/…/500)”을 자동으로 붙여주는 설정
    @Bean
    public OpenApiCustomizer standardErrorResponses() {
        return openApi -> {
            // 표준 에러 스키마 (README/에러코드 문서 포맷과 맞춤)
            Schema<?> apiError = new ObjectSchema()
                    .addProperty("success", new BooleanSchema()._default(false))
                    .addProperty("data", new ObjectSchema().nullable(true))
                    .addProperty("message", new StringSchema().example("잘못된 요청입니다."))
                    .addProperty("code", new StringSchema().example("INVALID_REQUEST"));
            openApi.getComponents().addSchemas("ApiError", apiError);

            // io.swagger.v3.oas.models.responses.ApiResponse 를 풀네임으로 사용(이름 충돌 회피)
            ApiResponse r400 = new ApiResponse().description("잘못된 요청").content(jsonWith("ApiError"));
            ApiResponse r401 = new ApiResponse().description("인증 필요/토큰 오류").content(jsonWith("ApiError"));
            ApiResponse r403 = new ApiResponse().description("권한 없음").content(jsonWith("ApiError"));
            ApiResponse r404 = new ApiResponse().description("리소스 없음").content(jsonWith("ApiError"));
            ApiResponse r409 = new ApiResponse().description("충돌(중복 등)").content(jsonWith("ApiError"));
            ApiResponse r500 = new ApiResponse().description("서버 오류").content(jsonWith("ApiError"));

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(op -> {
                        op.getResponses().addApiResponse("400", r400);
                        op.getResponses().addApiResponse("401", r401);
                        op.getResponses().addApiResponse("403", r403);
                        op.getResponses().addApiResponse("404", r404);
                        op.getResponses().addApiResponse("409", r409);
                        op.getResponses().addApiResponse("500", r500);
                    })
            );
        };
    }

    private io.swagger.v3.oas.models.media.Content jsonWith(String schemaRef) {
        io.swagger.v3.oas.models.media.MediaType mt =
                new io.swagger.v3.oas.models.media.MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/" + schemaRef));
        return new io.swagger.v3.oas.models.media.Content().addMediaType("application/json", mt);
    }
}
