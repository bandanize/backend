# Bandanize Backend

Backend service for the Bandanize platform, built with Spring Boot and Java 17.

## Overview

This application provides the REST API for managing:
- **Users & Authentication**: JWT-based secure access.
- **Projects**: Collaborative workspaces for bands.
- **Songs & Setlists**: Metadata, BPM, Key, and original artist tracking.
- **Tablatures**: Detailed instrument tabs (Guitar, Bass, etc.) with editor support.
- **Real-time Presence**: Tracking active users in a project via heartbeat mechanism.
- **File Management**: Uploading audio, images, and other attachments.

## Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.4.4
- **Database**: PostgreSQL (JPA/Hibernate)
- **Security**: Spring Security + JWT
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL running on port 5432 (or configure in `application.properties`)

### Running Locally

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`.

### API Documentation

Swagger UI is available at:
`http://localhost:8080/swagger-ui/index.html`

## Project Structure

- `src/main/java/com/bandanize/backend`
    - `controllers`: REST endpoints.
    - `services`: Business logic.
    - `models`: JPA Entities.
    - `repositories`: Database access.
    - `config`: Security and App configuration.
