package com.firefly.domain.people.core.integration;

import com.firefly.domain.people.core.integration.properties.CustomerMgmtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ClientFactory {

    private final CustomerMgmtProperties customerMgmtProperties;

    @Autowired
    public ClientFactory(
            CustomerMgmtProperties customerMgmtProperties) {
        this.customerMgmtProperties = customerMgmtProperties;
    }

    /**
     * Creates and returns a Customers service client.
     *
     * @return A configured Customers service client
     */
    @Bean
    public com.firefly.core.customer.sdk.invoker.ApiClient createCustomersClient() {
        com.firefly.core.customer.sdk.invoker.ApiClient apiClient = new com.firefly.core.customer.sdk.invoker.ApiClient();
        apiClient.setBasePath(customerMgmtProperties.getBasePath());
        return apiClient;
    }


}
