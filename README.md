# Twogoods

Twogoods is a Spring Boot 3.5.7 application built with Java 21, designed to provide a modern web system with security, API integration, and scalable data management. It already supports pagination, filtering, and comes with Swagger documentation out of the box. The system also integrates AI-powered price recommendation, which suggests product prices based on existing database records, or calculates average market prices if the product is not yet available in the database.

## Key Features
- Spring Boot Web & JPA → REST API + database access with Hibernate
- Spring Security + JWT → Authentication & authorization using JSON Web Tokens
- Spring Cloud OpenFeign → Declarative HTTP client for service-to-service communication
- Spring Validation & WebSocket → Request validation and real-time communication
- Spring Retry → Automatic retry mechanism for failed requests
- OAuth2 Client → Login with external providers (Google, GitHub, etc.)
- Cloudinary Integration → Media upload and management (images/videos)
- Springdoc OpenAPI → Automatic API documentation (Swagger UI)
- PostgreSQL → Primary relational database
- Dotenv Support → Environment variable management
- Lombok → Reduce boilerplate code
- Pagination & Filtering → Built-in support for paginated queries and dynamic filtering
- Swagger Documentation → Interactive API docs available out of the box
- AI Price Recommendation → Suggests product prices based on DB data or average market values

## Tech Stack
Java 21 • Spring Boot 3.5.7 • Spring Cloud 2025.0.0 • JWT 0.13.0 • PostgreSQL • Cloudinary SDK 1.33.0

## Installation & Setup
Clone the repository with `git clone https://github.com/username/twogoods.git && cd twogoods`. Make sure Java 21 and Maven are installed by running `java -version` and `mvn -v`. Ensure that your `.env` file matches `.env.example`. Start the application with `mvn spring-boot:run`.

## API Documentation
Swagger UI is available at `http://localhost:{yourPort}/swagger-ui/index.html`. The documentation includes all 
endpoints with 
support for pagination, filtering, request validation, and AI-powered price recommendation.
