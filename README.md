# Waveguide Management System API

## Overview
This Spring Boot application provides a comprehensive REST API for a Waveguide Management System. It includes user authentication, waveguide management, and layer manipulation with database persistence, full security implementation, and complete API documentation.

## Technical Stack
- Java 17
- Spring Boot 3.x
- Spring Security with JWT authentication
- Spring Data JPA with PostgreSQL
- Maven for dependency management
- OpenAPI 3.0 documentation

## Features
- User registration and authentication with JWT tokens
- Secure password handling with BCrypt
- Waveguide creation and management
- Layer management within waveguides
- Rate limiting protection
- Comprehensive error handling
- Audit logging for security events
- OpenAPI documentation

## Getting Started

### Prerequisites
- Java 17+
- Maven
- PostgreSQL database

### Configuration
1. Configure your PostgreSQL database in `application.yml`
2. Set appropriate JWT secret key for production environments

### Running the Application
```bash
mvn spring-boot:run
```

### API Documentation
Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Authentication

```
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/logout
```

### Waveguide Management

```
POST /api/v1/waveguides
GET /api/v1/waveguides
GET /api/v1/waveguides/{id}
DELETE /api/v1/waveguides/{id}
```

### Layer Management

```
POST /api/v1/waveguides/{id}/layers
PUT /api/v1/waveguides/{id}/layers/{layerId}
DELETE /api/v1/waveguides/{id}/layers/{layerId}
```

## Security Features
- JWT authentication with token blacklisting
- Password hashing with BCrypt
- Rate limiting to prevent abuse
- SQL injection prevention
- Input validation
- CORS configuration
- Structured error responses

## Testing
Run the test suite using:
```bash
mvn test
```

## License
MIT