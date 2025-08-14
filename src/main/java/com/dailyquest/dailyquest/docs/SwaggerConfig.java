package com.dailyquest.dailyquest.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration

public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DailyQuest API 문서") //제목
                        .description("모바일 게임 숙제 관리 서비스 API 문서") // 설명
                        .version("1.0.0"));

    }
}
