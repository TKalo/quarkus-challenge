# Bankdata Coding Challenge

## Table of Contents
- [Project Overview](#project-overview)
- [Configuration](#configuration)
- [Features](#features)
- [Backend Structure](#backend-structure)
- [Frontend Structure](#frontend-structure)

## Project Overview

This project is a coding challenge proposed by Bankdata. It demonstrates the implementation of a full-stack application that combines a Quarkus-based backend and a React-based frontend. The system allows users to manage bank accounts and perform currency conversions between DKK and USD. The project is containerized using Docker Compose for ease of deployment and setup.

### Key Features:
- Create and manage bank accounts.
- Perform deposits, transfers, and retrieve account balances.
- Convert currency between DKK and USD using a third-party API.

To run the application, use the provided `docker-compose.yml` file. From the root directory of the project, execute:
```bash
docker-compose up
```
This will start the backend, frontend, and database services.

> **Note:**  
> *Currency conversion requires the configuration of the `EXCHANGE_API_KEY` environment variable. See the [Configuration](#configuration) section for details.*

### Technologies Used:
- **Backend:**
  - Java 17
  - Quarkus (for REST API and backend logic)
  - PostgreSQL (for data persistence)
  - OpenAPI & Swagger (for API documentation)
- **Frontend:**
  - React (for building the user interface)
  - Vite (for fast development and builds)
  - TailwindCSS (for styling)
- **Infrastructure:**
  - Docker (for containerization)
  - Docker Compose (for multi-service orchestration)
- **Third-Party API:**
  - Exchange Rate API (for live currency conversion)

## Configuration

### Docker Setup

The project has been set up with Docker Compose for easy execution. The `docker-compose.yml` file in the root of the project contains the configuration for the backend and frontend applications, as well as a PostgreSQL database used by the backend.

- **Backend and Frontend Containers:** The compose file references Dockerfiles located in the `backend` and `frontend` directories.
- **Database Configuration:** A PostgreSQL database is included in the setup to persist application data.

#### Environment Variables

- **Backend Environment Variables:** Must be defined in the compose file as the `.env` file used for local configuration is not copied into the container.
- **Currency Conversion API:** The `EXCHANGE_API_KEY` environment variable must be set in the compose file to enable currency conversion. Obtain a free API key from [Exchange Rate API](https://www.exchangerate-api.com/).

#### Running the Application

To start the application, run the following command from the root directory:

```bash
docker-compose up
```

This will:

1. Build the database container.
2. Build and start the backend container.
3. Build and start the frontend container.

The services will be accessible at:

- **Backend API:** `http://localhost:8080`
- **Frontend Application:** `http://localhost:5173`

### Local Setup

It is possible to run the backend and frontend applications locally with some additional configuration.

#### Tested Installation Dependencies
- Java 17.0.13
- Apache Maven 3.9.9
- Node.js 18.20.0

#### Backend Setup

1. Navigate to the `backend` directory.
2. Create a `.env` file in the root of the backend directory based on the `.env.example` file.
3. Provide connection details to a PostgreSQL database in the `.env` file.
4. Build the backend application:
   ```bash
   mvn clean install
   ```
5. Run the backend tests:
   ```bash
   mvn quarkus:test
   ```
6. Start the backend application:
   ```bash
   mvn quarkus:dev
   ```

#### Frontend Setup

1. Navigate to the `frontend` directory.
2. Install the frontend dependencies:
   ```bash
   npm install
   ```
3. Start the frontend application:
   ```bash
   npm run dev
   ```

The frontend will be accessible at `http://localhost:5173`. The backend must be running locally on `http://localhost:8080` for the frontend to connect to the API.

## Features

This is an overview of the features provided by the system.

### API

The backend provides a REST API with the following endpoints:

| Method | Endpoint                                           | Description                                                      |
| ------ | -------------------------------------------------- | ---------------------------------------------------------------- |
| GET    | /accounts                                          | Get all accounts                                                 |
| POST   | /accounts                                          | Create an account                                                |
| POST   | /accounts/{id}/deposit                             | Deposit money into an account                                    |
| POST   | /accounts/{id}/transfer                            | Transfer money from one account to another                       |
| GET    | /accounts/{id}/balance                             | Get the balance of an account                                    |
| GET    | /currency/{baseCurrency}/{targetCurrency}/{amount} | Convert the amount from the base currency to the target currency |

**Note:** *Only DKK and USD are supported as currencies for the currency conversion.*

### Swagger

OpenAPI and Swagger UI have been configured for the backend application.

- **OpenAPI JSON:** Accessible at `http://localhost:8080/q/openapi` and can be used to generate client code.
- **Swagger UI:** Accessible at `http://localhost:8080/swagger-ui`, providing an interactive interface for testing API endpoints and viewing data models.

### Frontend

The frontend is a single-page application and implements the following API integrations:

- **Currency Conversion:** Supports conversions between DKK and USD.
- **Account Management:**
  - Create new accounts.
  - View a list of all accounts.
  - Deposit money into accounts.
  - Transfer money between accounts.

## Backend Structure

The backend is a Java application using Quarkus.

### Modules

The backend folder structure is feature-based and contains two primary modules:
- **`account`:** Handles account-related operations such as creation, deposits, transfers, and balance retrieval.
- **`currency`:** Handles currency conversion operations.

#### Key Components
- **`Resource` Classes:** Define the REST API endpoints for each module.
- **`Service` Classes:** Contain the business logic and are injected into the respective `Resource` classes.
- **`Repository` Classes:** Implement the persistence layer using Panache and are injected into the `Service` classes.

### Configuration

The application’s configuration is managed in the `application.properties` file located in the `src/main/resources` folder. The file includes the following configuration sections:

1. **CORS Configuration:** Controls cross-origin resource sharing to allow frontend communication.
2. **Database Configuration:** Specifies connection details for the PostgreSQL database.
3. **OpenAPI/Swagger Configuration:** Configures the API documentation generation.
4. **Exchange Rate API Configuration:** Includes the `EXCHANGE_API_KEY` required for currency conversion.

### Tests

Tests have been implemented for the `account` module and are located in the `AccountResourceTest` class.

#### Testing Highlights:
- **PostgreSQL Test Container:** A PostgreSQL container is used to manage persistence during tests. The test container setup is defined in the `PostgreSQLTestResource` class.
- **Integration Tests:** Verify the functionality of the `account` module’s API endpoints and business logic.

> **Note:** Tests for the `currency` module have not been implemented yet. It can be tested by overriding the base URL environment variable, to point to a mock service.

## Frontend Structure

The frontend is a React application using Vite as the build tool and TailwindCSS for styling. It consists of a single page with a row of feature components.

### Components

The frontend has three major components:

1. **Currency Conversion:**
   - Allows users to convert currency between DKK and USD.
   - Includes:
     - An input field for entering the amount to convert.
     - A toggle button to switch between `DKK to USD` and `USD to DKK` conversions.
     - A `Convert` button to perform the conversion and display the result.

2. **Account Creation:**
   - Enables users to create a new account.
   - Includes:
     - Two input fields for entering the first and last name of the account holder.
     - A `Create` button to create the account.

3. **Account List:**
   - Displays a list of all existing accounts in the system.
   - For each account, provides:
     - A `Deposit` button to add funds:
       - Opens a modal with an input field for the deposit amount.
       - Includes a `Deposit` button to confirm and process the deposit.
     - A `Transfer` button to transfer funds to another account:
       - Opens a modal with an input field for the transfer amount.
       - Includes a dropdown menu to select the recipient account.
       - Includes a `Transfer` button to confirm and process the transfer.

> **Note:** The frontend uses page refresh for data updates. This is a due to the very light nature of the application and the absence of a state management library.



