package com.firefly.domain.people.core.integration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties.
 * Maps the properties defined in application.yaml under api-configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "api-configuration.common-platform.customer-mgmt")
@Getter
@Setter
public class CustomerMgmtProperties {

    private String basePath;

}