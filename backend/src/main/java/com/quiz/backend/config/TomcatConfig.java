package com.quiz.backend.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                var protocol = (Http11NioProtocol) connector.getProtocolHandler();
                protocol.setMaxHttpHeaderSize(65536);
                protocol.setMaxHttpRequestHeaderSize(65536);
                protocol.setMaxHeaderCount(200);
            });
        };
    }
}
