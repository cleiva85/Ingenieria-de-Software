package com.abrazame.service_gestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${donante.service.url}")
    private String donanteServiceUrl;

    @Value("${catalogo.service.url}")
    private String catalogoServiceUrl;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().baseUrl(donanteServiceUrl);
    }

    /** Cliente HTTP dedicado para hablar con service-catalogo (incrementar/decrementar stock) */
    @Bean
    public WebClient catalogoWebClient() {
        return WebClient.builder().baseUrl(catalogoServiceUrl).build();
    }
}
