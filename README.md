# ExploreWithMe — Diploma Project

ExploreWithMe is a service that helps users create, publish, discover and participate in events.  
The project is built as a microservice system:

- **main-service** — users, events, categories, participation requests, compilations
- **stats-service** — hit recording and view statistics

## Technologies
- Java, Spring Boot, MVC, JPA/Hibernate
- PostgreSQL
- Docker / Docker Compose
- Lombok
- Postman test collections

The application supports full event lifecycle management, user interaction, moderation tools and statistics collection.  
Development included Git/GitHub workflow, containerization, and testing through Postman collections.

## Additional Feature
As part of the final stage, a new feature was implemented — **event comments**.  
Added:
- REST endpoints for creating, listing and removing comments
- `Comment` entity with DTOs and service layer
- Postman collection `feature.json`

## Pull Request (Stage 3)
PR link: https://github.com/InstaYangire/java-explore-with-me/pull/4