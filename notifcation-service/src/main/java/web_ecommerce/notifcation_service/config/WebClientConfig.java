package web_ecommerce.notifcation_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${novu.api.base-url}")
    private String baseUrl;

    @Value("${novu.api.key}")
    private String apiKey;

    @Bean
    public WebClient novuWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl) // e.g., https://api.novu.co
                .defaultHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
