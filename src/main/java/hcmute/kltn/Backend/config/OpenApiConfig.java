package hcmute.kltn.Backend.config;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(
		  name = "Bearer Authentication",
		  type = SecuritySchemeType.HTTP,
		  bearerFormat = "JWT",
		  scheme = "bearer"
		)
public class OpenApiConfig {
	@Bean
	public GroupedOpenApi customOpenApi() {
		return GroupedOpenApi.builder().group("api").packagesToScan("hcmute.kltn.Backend.controller")
				.addOpenApiCustomiser(openApiCustomiser()).build();
	}

	private OpenApiCustomiser openApiCustomiser() {
		return openApi -> {
			openApi.getInfo().setTitle("Travelover API Manager");
			openApi.getInfo().setDescription("Your API Description");
			openApi.getInfo().setVersion("v1");
		};
	}
}
