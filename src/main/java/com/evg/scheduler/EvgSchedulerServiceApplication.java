package com.evg.scheduler;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class EvgSchedulerServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(EvgSchedulerServiceApplication.class);
	
	@Value("${http.port}")
	private int httpPort;

	public static void main(String[] args) {
		SpringApplication.run(EvgSchedulerServiceApplication.class, args);
	}

	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createStandardConnector());
		return tomcat;
	}

	private Connector createStandardConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setPort(httpPort);
		return connector;
	}
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
	    return (tomcat) -> tomcat.addConnectorCustomizers((connector) -> {
	        if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
	            AbstractHttp11Protocol<?> protocolHandler = (AbstractHttp11Protocol<?>) connector.getProtocolHandler();
	            protocolHandler.setKeepAliveTimeout(600);
	            protocolHandler.setMaxKeepAliveRequests(750);
	            protocolHandler.setUseKeepAliveResponseHeader(true);
	        }
	    });
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate(clientHttpRequestFactory());
	}
	private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Set the connect timeout (time taken to establish a connection)
        factory.setConnectTimeout(2000); // 60 seconds
        // Set the read timeout (time taken to receive data once the connection is established)
        factory.setReadTimeout(2000);    // 60 seconds
        return factory;
    }
}
