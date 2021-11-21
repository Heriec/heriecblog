package com.heriec.blogmaster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket createRestApiGroup1(Environment environment){
        // 添加 head 参数配置 start
        ParameterBuilder token = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        token.name("Authorization").description("token 信息").modelRef(new ModelRef("String"))
                .parameterType("header").required(false).build();
        pars.add(token.build());

         //设置要显示的Swagger环境
        Profiles profiles = Profiles.of("dev", "prod");
         //获取项目的环境
        boolean isDevAndTest = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .enable(isDevAndTest)
                .select().apis(RequestHandlerSelectors.basePackage("com.heriec.blogmaster"))
                .build().groupName("test1")
                // 注意一下 globalOperationParameters 这行配置
                .globalOperationParameters(pars);
    }
    // 配置网站相关信息
    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact=new Contact("heriec","hh.com","2821188630@qq.com");
        return new ApiInfoBuilder()
                .title("个人博客API文档")
                .description("为个人博客开发努力！")
                .termsOfServiceUrl("http://blog.heriec.com")
                .version("v1.0")
                .contact(contact)
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

}
