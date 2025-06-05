# Automobile Stock Management System

A cloud-based system for managing automobile inventory through a web-user interface using Spring Boot, Hibernate ORM, and PostgreSQL.

## Features

- **Inventory Management**: Add, edit, view, and delete vehicles with detailed attributes.
- **Order System**: Create and manage vehicle orders with order status tracking.
- **Maintenance Tracking**: Schedule and track maintenance for vehicles.
- **User Management**: Manage users with different roles (Admin, Manager, Salesperson, Customer).
- **Modern Web Interface**: Responsive UI with filtering and search capabilities.

## Tech Stack

- **Backend**
  - Java 17
  - Spring Boot 3.4.5
  - Spring Data JPA with Hibernate ORM
  - PostgreSQL Database
  - RESTful API Architecture

- **Frontend**
  - HTML5
  - CSS3
  - JavaScript (ES6+)
  - Bootstrap 5

## Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Database Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE automobile_stock;
   ```

2. Configure database username and password in `src/main/resources/application.properties` if needed.

### Running the Application

1. Clone the repository.

2. Navigate to the project directory:
   ```
   cd automobile-stock-management
   ```

3. Build the application:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

5. Access the application in your browser:
   ```
   http://localhost:8080
   ```

## API Endpoints

### Vehicles
- `GET /api/vehicles` - Get all vehicles
- `GET /api/vehicles/{id}` - Get a vehicle by ID
- `GET /api/vehicles/search?searchTerm={term}` - Search vehicles
- `GET /api/vehicles/make/{make}` - Get vehicles by make
- `GET /api/vehicles/model/{model}` - Get vehicles by model
- `GET /api/vehicles/year/{year}` - Get vehicles by year
- `GET /api/vehicles/available` - Get available vehicles
- `GET /api/vehicles/fuel-type/{fuelType}` - Get vehicles by fuel type
- `GET /api/vehicles/transmission-type/{transmissionType}` - Get vehicles by transmission type
- `GET /api/vehicles/price-range?minPrice={min}&maxPrice={max}` - Get vehicles by price range
- `GET /api/vehicles/max-mileage/{maxMileage}` - Get vehicles by maximum mileage
- `POST /api/vehicles` - Create a new vehicle
- `PUT /api/vehicles/{id}` - Update a vehicle
- `DELETE /api/vehicles/{id}` - Delete a vehicle

### Orders
- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get an order by ID
- `GET /api/orders/number/{orderNumber}` - Get an order by order number
- `GET /api/orders/user/{userId}` - Get orders by user ID
- `GET /api/orders/status/{status}` - Get orders by status
- `GET /api/orders/date-range?startDate={start}&endDate={end}` - Get orders by date range
- `POST /api/orders` - Create a new order
- `PUT /api/orders/{id}` - Update an order
- `DELETE /api/orders/{id}` - Delete an order
- `PATCH /api/orders/{id}/status/{status}` - Update order status

### Maintenance
- `GET /api/maintenance` - Get all maintenance records
- `GET /api/maintenance/{id}` - Get a maintenance record by ID
- `GET /api/maintenance/vehicle/{vehicleId}` - Get maintenance records by vehicle ID
- `GET /api/maintenance/status/{status}` - Get maintenance records by status
- `GET /api/maintenance/date-range?startDate={start}&endDate={end}` - Get maintenance records by date range
- `GET /api/maintenance/upcoming?date={date}` - Get upcoming maintenance records
- `POST /api/maintenance` - Create a new maintenance record
- `PUT /api/maintenance/{id}` - Update a maintenance record
- `DELETE /api/maintenance/{id}` - Delete a maintenance record
- `PATCH /api/maintenance/{id}/status/{status}` - Update maintenance status

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get a user by ID
- `GET /api/users/username/{username}` - Get a user by username
- `GET /api/users/email/{email}` - Get a user by email
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/check/username/{username}` - Check if username exists
- `GET /api/users/check/email/{email}` - Check if email exists
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update a user
- `DELETE /api/users/{id}` - Delete a user

## Project Structure

```
automobile-stock-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── stockmanage/
│   │   │           └── automobile/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               │   └── impl/
│   │   │               └── AutomobileStockApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── index.html
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── stockmanage/
│                   └── automobile/
├── pom.xml
└── README.md
``` 