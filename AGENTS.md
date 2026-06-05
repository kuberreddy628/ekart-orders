# AGENTS.md

## Project Overview
This project is a Spring Boot microservice for order management in an e-commerce system. It integrates with other services (inventory, payment, shipping) using REST (via Feign clients) and Kafka for asynchronous event-driven communication.

## Architecture & Key Components
- **Controller Layer**: `controller/OrdersController.java` exposes REST endpoints for order operations (placing, retrieving, updating, canceling orders).
- **Service Layer**: `service/OrderService.java` (interface) and `serviceImpl/OrderServiceImpl.java` (implementation) contain business logic for order processing, including orchestration of inventory, payment, and shipping.
- **Repository Layer**: `repository/OrdersRepository.java` uses Spring Data JPA for persistence.
- **DTOs**: `dto/` contains request/response objects for API and inter-service communication.
- **Entities**: `entity/` contains JPA entities for database mapping.
- **Kafka Integration**: `config/KafkaConsumerConfiguration.java` configures multiple Kafka consumer factories and listener container factories for different event types (inventory, payment, shipping outcomes). Kafka topics are used for fulfillment event flows.
- **Feign Clients**: `client/` (e.g., `InventoryClient`) for synchronous REST calls to other microservices.

## Event-Driven Patterns
- Kafka topics are used for fulfillment events. Each event type (inventory, payment, shipping) has a dedicated consumer factory and listener container factory.
- No type headers are used in Kafka messages; deserialization is handled by explicit trusted packages and payload type configuration.

## Developer Workflows
- **Build**: Use `mvnw clean package` to build the project.
- **Run**: Use `mvnw spring-boot:run` or run the generated JAR in `target/`.
- **Test**: Standard Spring Boot test conventions apply. Test dependencies are included in `pom.xml`.
- **Kafka**: Ensure Kafka is running and configured via `application.properties` or `application.yml` (see `spring.kafka.bootstrap-servers`).

## Project-Specific Conventions
- **Service Implementation**: All business logic is in `serviceImpl/OrderServiceImpl.java`, which implements the `OrderService` interface.
- **DTO Usage**: All controller and service methods use DTOs for input/output, never entities directly.
- **Logging**: Uses SLF4J (`LoggerFactory`) for logging in controllers and services.
- **Validation**: Uses `@Valid` and `BindingResult` for request validation in controllers.
- **Error Handling**: Returns appropriate HTTP status codes and error messages for validation and entity-not-found scenarios.
- **Kafka Deserialization**: Uses `JacksonJsonDeserializer` with trusted package set to `com.ekart`.

## Integration Points
- **Kafka**: Topics for payment, shipping, and inventory events. See constants in `OrderServiceImpl` and configuration in `KafkaConsumerConfiguration.java`.
- **Feign Clients**: For synchronous calls to inventory and possibly other services.
- **Database**: MySQL (see `pom.xml` and JPA entities).

## Example: Adding a New Fulfillment Event
1. Define the event class in `event/fulfillment/`.
2. Add a consumer factory and listener container factory in `KafkaConsumerConfiguration.java`.
3. Add handling logic in the appropriate service or listener.

## Key Files/Directories
- `controller/OrdersController.java`: REST API entry points
- `service/OrderService.java`, `serviceImpl/OrderServiceImpl.java`: Business logic
- `repository/OrdersRepository.java`: Persistence
- `config/KafkaConsumerConfiguration.java`: Kafka integration
- `dto/`, `entity/`, `event/`: Data structures
- `client/`: Feign clients for inter-service calls

---
For more, see https://agents.md/ for agent authoring best practices.

