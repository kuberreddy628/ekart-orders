package com.ekart.orders.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka JSON payloads without {@code @class} metadata so other services can deserialize into their own POJOs.
 * <p>
 * Uses only {@link Environment} (no {@code KafkaProperties}) so the project compiles whether Kafka meta is pulled
 * from {@code spring-boot-starter-kafka} / Boot 3 vs 4 layout.
 * <p>
 * Spring Kafka 4 deprecates {@code JsonSerializer}; {@link JacksonJsonSerializer} is the supported replacement.
 * <p>
 * Bean type must be {@code ProducerFactory<Object, Object>} so {@link org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration}'s
 * {@code kafkaTemplate} method can inject it (Boot skips its default factory when any {@code ProducerFactory} exists).
 */
@Configuration
public class KafkaProducerSerializationConfig {

	@Bean
	public ProducerFactory<Object, Object> kafkaProducerFactory(Environment env) {
		Map<String, Object> props = new HashMap<>();
		props.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));

		String clientId = env.getProperty("spring.kafka.producer.client-id");
		if (clientId != null && !clientId.isBlank()) {
			props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		}
		String acks = env.getProperty("spring.kafka.producer.acks");
		if (acks != null && !acks.isBlank()) {
			props.put(ProducerConfig.ACKS_CONFIG, acks);
		}

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		DefaultKafkaProducerFactory<Object, Object> factory = new DefaultKafkaProducerFactory<>(props);
		JacksonJsonSerializer<Object> serializer = new JacksonJsonSerializer<>();
		serializer.setAddTypeInfo(false);
		factory.setValueSerializer(serializer);
		return factory;
	}
}
