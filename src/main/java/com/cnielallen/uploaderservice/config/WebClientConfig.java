package com.cnielallen.uploaderservice.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() throws SSLException {
        var sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        var httpClient = HttpClient
                .create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        var connector = new ReactorClientHttpConnector(httpClient);
        return WebClient.builder().clientConnector(connector)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("Response status: {} ", response.statusCode());
            return Mono.just(response);
        });
    }
}
