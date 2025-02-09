package com.example.demo.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI UMCswaggerAPI() {
        Info info = new Info()
                .title("UMC MiniProject API")
                .description("UMC MiniProject API 명세서")
                .version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        // Kakao 로그인 요청에 사용할 헤더 파라미터 설정
        Parameter kakaoAuthHeader = new Parameter()
                .in("header")
                .name("X-Kakao-Authorization")
                .description("카카오 로그인 ID 토큰")
                .required(true)
                .schema(new StringSchema().example("your-kakao-id-token"));

        // Google 로그인 요청에 사용할 헤더 파라미터 설정
        Parameter googleAuthHeader = new Parameter()
                .in("header")
                .name("X-Google-Authorization")
                .description("구글 로그인 ID 토큰")
                .required(true)
                .schema(new StringSchema().example("your-google-id-token"));

        // /join/Kakao 엔드포인트에 대한 Operation 설정
        Operation kakaoJoinOperation = new Operation()
                .description("카카오 ID 토큰을 이용한 회원가입 및 로그인")
                .addParametersItem(kakaoAuthHeader)  // 헤더 추가
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("로그인 성공 - JWT 토큰 발행"))
                        .addApiResponse("400", new ApiResponse().description("잘못된 요청"))
                        .addApiResponse("401", new ApiResponse().description("인증 실패")));

        // /join/Google 엔드포인트에 대한 Operation 설정
        Operation googleJoinOperation = new Operation()
                .description("구글 ID 토큰을 이용한 회원가입 및 로그인")
                .addParametersItem(googleAuthHeader)  // 헤더 추가
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("로그인 성공 - JWT 토큰 발행"))
                        .addApiResponse("400", new ApiResponse().description("잘못된 요청"))
                        .addApiResponse("401", new ApiResponse().description("인증 실패")));

        // /join/Kakao 엔드포인트에 POST 메서드 추가
        PathItem kakaoJoinPathItem = new PathItem().post(kakaoJoinOperation);

        // /join/Google 엔드포인트에 POST 메서드 추가
        PathItem googleJoinPathItem = new PathItem().post(googleJoinOperation);

        // Login 요청에 사용될 스키마 정의 (예: email, password)
        Schema<Object> loginRequestSchema = new Schema<>()
                .type("object")
                .addProperties("email", new Schema<>().type("string").example("example@example.com"))
                .addProperties("password", new Schema<>().type("string").example("password"));

        // /login 엔드포인트의 POST 메서드에 대한 Operation 생성
        Operation loginOperation = new Operation()
                .description("이메일과 비밀번호를 통해 로그인 후 JWT 토큰을 반환")
                .requestBody(new RequestBody()
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(loginRequestSchema)))
                        .required(true))
                .responses(new ApiResponses()
                        .addApiResponse("200", new ApiResponse().description("로그인 성공 - JWT 토큰 반환"))
                        .addApiResponse("401", new ApiResponse().description("인증 실패")));

        // /login 엔드포인트에 POST 메서드 추가
        PathItem loginPathItem = new PathItem().post(loginOperation);

        // Paths 객체에 경로 추가
        Paths paths = new Paths();
        paths.addPathItem("/join/Kakao", kakaoJoinPathItem);
        paths.addPathItem("/join/Google", googleJoinPathItem);
        paths.addPathItem("/login", loginPathItem);

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components)
                .paths(paths);
    }
}
