# Achievix 🎯

Welcome to **Achievix**, a full-stack web application designed to help you set, track, and achieve your personal goals and tasks!

## 🎥 Project Demo
Check out the demo video showcasing Achievix's core features:

<video src="assets/demo.mp4" controls width="600"></video>

## 🛠️ Tech Stack
### Backend
- **Java Spring Boot**: Core framework for REST API development.
- **Spring Data JPA**: Simplified data access for entity management.
- **MySQL**: Relational database for storing goals and tasks.
- **Kafka**: Event-driven communication via message broker.
- **SendGrid**: Email delivery for notifications.
- **Docker**: Containerization for easy deployment.
- **Testing**: JUnit, Mockito, and Testcontainers for unit and integration testing.

### Frontend
- **Vue.js**: Progressive JavaScript framework for a responsive UI.
- **Tailwind CSS**: Utility-first CSS framework for sleek styling.
- **Chart.js**: JavaScript library for interactive data visualizations.
- **Axios**: Handles API requests to the backend.
- **Vue Router**: Enables client-side routing.
- **Testing**: Vitest for unit testing, Cypress for end-to-end testing.

### Deployment
- **Docker**: Containerized backend and frontend for portability.
- **Docker Compose**: Orchestrates multi-container applications.

## 🚀 Getting Started
Follow these steps to set up and run Achievix locally using Docker.

### Prerequisites
- **Docker** and **Docker Compose** installed on your machine.
- **SendGrid API Key** for email notifications (sign up at [SendGrid](https://sendgrid.com)).

### Setup Instructions
1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/achievix.git
   cd achievix
   ```

2. **Configure Environment Variables**
   - Create a `.env` file by copying the example:
     ```bash
     cp .env.example .env
     ```
   - Edit `.env` with your credentials.
   

3. **Create Application Configuration Files**
   - Copy example configuration files for the backend:
     ```bash
     cp backend/Achievix/src/main/resources/application.yml.example backend/Achievix/src/main/resources/application.yml
     cp backend/Achievix/src/main/resources/application-test.yml.example backend/Achievix/src/main/resources/application-test.yml
     ```
   - Fill in the fields in `application.yml` and `application-test.yml`.

4. **Obtain a SendGrid API Key**
   - Sign up at [SendGrid](https://sendgrid.com).
   - Follow SendGrid’s documentation to create an API key.
   - Use a verified sender email in your `.env` file.

5. **Run the Application with Docker**
   ```bash
   docker-compose up --build
   ```
   - **Frontend**: Accessible at `http://localhost`
   - **Backend API**: Accessible at `http://localhost:8080/api`

6. **Access the App**
   - Open `http://localhost` in your browser.
   - Create an account, set goals, assign tasks, and track your progress!

## 📋 Project Structure
- `backend/Achievix/`: Java Spring Boot backend with REST API and database integration.
- `frontend/Achievix/`: Vue.js frontend with components and views.
- `docker-compose.yml`: Docker configuration for containerized deployment.

## 💻 Features
- **User Authentication**: Secure JWT-based login system.
- **Goal Management**: Set goals with deadlines and monitor progress.
- **Task Assignment**: Create and link tasks to specific goals.
- **Progress Tracking**: Visual progress bars and completion status.
- **Dashboard Analytics**: Interactive charts for goal and task completion trends.
- **Email Notifications**: Event-driven alerts via SendGrid.
- **Responsive Design**: Optimized for desktop and mobile devices.

## 🧪 Running Tests
### Backend Tests
```bash
cd backend/Achievix
mvn test
```
- Uses **JUnit**, **Mockito**, and **Testcontainers** for unit and integration testing.

### Frontend Tests
```bash
cd frontend/Achievix
npm run test
```
- Uses **Vitest** for unit testing and **Cypress** for end-to-end testing.