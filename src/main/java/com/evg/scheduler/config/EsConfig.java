package com.evg.scheduler.config;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;


@Configuration
public class EsConfig extends AbstractElasticsearchConfiguration {
	
	@Value("${elasticsearch.url}")
	String url;
	
	@Override
	@Bean
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(url) // Define the Elasticsearch host and port
                .withConnectTimeout(5000) // Connection timeout in milliseconds
                .withSocketTimeout(30000) // Socket timeout in milliseconds
                .build();
        return RestClients.create(clientConfiguration).rest();
	}
	
	@Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(elasticsearchClient()); // You might need to configure this with appropriate settings.
    }

}
