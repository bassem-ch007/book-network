package com.bassem.bsn.common;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact =@Contact(name = "Bassem" ,
                email = "contact@Bassem.com",
                url = "https://bassemcoding.com/courses"),
                description = "Open Api documentation for Book Social Network",
                title = "OpenApi Specification - Bassem",
                version = "1.0",
                license = @License(name = "licence name",url = "htts://someurl"),
                termsOfService = "Terms of service"
        ),
        servers = {@Server (
                description = "Local ENV",url = "http://localhost:8088/api/v1"
        ),
        @Server (description = "PROD ENV",url = "https://bassemcoding.com/courses")},
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(name = "bearerAuth",
description = "JWT auth description",
scheme = "bearer",
type = SecuritySchemeType.HTTP,
bearerFormat = "JWT",
in = SecuritySchemeIn.HEADER )
public class OpenApiConfig {
}
