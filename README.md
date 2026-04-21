Development & Operations Overview
Development (Dev)

The Book Social Network (BSN) application was developed as a full-stack web platform enabling users to share, explore, and interact with books.

Backend: Built with Spring Boot, providing RESTful APIs for user management, book handling, and social interactions.
Frontend: Developed using Angular, delivering a responsive and dynamic user interface.
Database: Utilized PostgreSQL for persistent data storage.
Architecture: Designed with a modular structure separating frontend, backend, and database concerns.
Features:
User authentication & profile management
Book creation and sharing
Social interactions (likes, comments)
Operations (Ops / DevOps)

The application was containerized and deployed using modern DevOps practices to ensure scalability, reproducibility, and automation.

Containerization: Each service (frontend, backend, database) was packaged using Docker.
Orchestration: Multi-container setup managed via Docker Compose.
CI/CD Pipeline: Implemented with GitLab CI/CD to automate:
Build process
Docker image creation
Deployment steps
Web Server & Reverse Proxy: Nginx used to serve the frontend and route API requests.
Environment Exposure: Ngrok enabled external access for testing and demonstration.
Database Management: Optional use of pgAdmin for monitoring and administration.
