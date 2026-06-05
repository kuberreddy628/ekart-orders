package com.ekart.orders.config;

import com.ekart.orders.event.fulfillment.InventoryOrderOutcomeEvent;
import com.ekart.orders.event.fulfillment.PaymentOutcomeEvent;
import com.ekart.orders.event.fulfillment.ShippingOutcomeEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Fulfillment listeners consume three different JSON shapes from six topics, all without type headers.
 * Use one container factory per payload type (inventory / payment / shipping outcomes).
 */
@Configuration
public class KafkaConsumerConfiguration {

	private static Map<String, Object> baseConsumerProps(Environment env) {
		Map<String, Object> props = new HashMap<>();
		props.put(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				env.getProperty("spring.kafka.consumer.auto-offset-reset", "earliest"));
		return props;
	}

	private static <T> JacksonJsonDeserializer<T> payloadDeserializer(Class<T> type) {
		JacksonJsonDeserializer<T> deserializer = new JacksonJsonDeserializer<>(type, false);
		deserializer.addTrustedPackages("com.ekart");
		return deserializer;
	}

	@Bean
	public ConsumerFactory<String, InventoryOrderOutcomeEvent> inventoryOutcomeConsumerFactory(Environment env) {
		Map<String, Object> props = baseConsumerProps(env);
		DefaultKafkaConsumerFactory<String, InventoryOrderOutcomeEvent> factory =
				new DefaultKafkaConsumerFactory<>(props);
		factory.setValueDeserializer(payloadDeserializer(InventoryOrderOutcomeEvent.class));
		return factory;
	}

	@Bean
	public ConsumerFactory<String, PaymentOutcomeEvent> paymentOutcomeConsumerFactory(Environment env) {
		Map<String, Object> props = baseConsumerProps(env);
		DefaultKafkaConsumerFactory<String, PaymentOutcomeEvent> factory =
				new DefaultKafkaConsumerFactory<>(props);
		factory.setValueDeserializer(payloadDeserializer(PaymentOutcomeEvent.class));
		return factory;
	}

	@Bean
	public ConsumerFactory<String, ShippingOutcomeEvent> shippingOutcomeConsumerFactory(Environment env) {
		Map<String, Object> props = baseConsumerProps(env);
		DefaultKafkaConsumerFactory<String, ShippingOutcomeEvent> factory =
				new DefaultKafkaConsumerFactory<>(props);
		factory.setValueDeserializer(payloadDeserializer(ShippingOutcomeEvent.class));
		return factory;
	}

	@Bean(name = "inventoryOutcomeKafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, InventoryOrderOutcomeEvent>
			inventoryOutcomeKafkaListenerContainerFactory(
					ConsumerFactory<String, InventoryOrderOutcomeEvent> inventoryOutcomeConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, InventoryOrderOutcomeEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(inventoryOutcomeConsumerFactory);
		return factory;
	}

	@Bean(name = "paymentOutcomeKafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, PaymentOutcomeEvent> paymentOutcomeKafkaListenerContainerFactory(
			ConsumerFactory<String, PaymentOutcomeEvent> paymentOutcomeConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, PaymentOutcomeEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(paymentOutcomeConsumerFactory);
		return factory;
	}

	@Bean(name = "shippingOutcomeKafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, ShippingOutcomeEvent>
			shippingOutcomeKafkaListenerContainerFactory(
					ConsumerFactory<String, ShippingOutcomeEvent> shippingOutcomeConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, ShippingOutcomeEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(shippingOutcomeConsumerFactory);
		return factory;
	}
}
