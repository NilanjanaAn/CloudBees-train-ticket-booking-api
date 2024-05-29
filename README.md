# CloudBees-train-ticket-booking-api 🚅
A Java REST API for Train Ticket Booking - CloudBees 2nd Technical Assessment

Submitted By: *Nilanjana Thakur* 😊

## Table of Contents
- [Problem Statement](#problem-statement)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)

## Problem Statement

1. I want to board a train from London to France. The train ticket will cost $20.
2. Create API where you can submit a purchase for a ticket.
3. Details included in the receipt are: From, To, User , price paid.
4. User should include first and last name, email address
5. The user is allocated a seat in the train. Assume the train has only sections, section A and section B.
6. An API that shows the details of the receipt for the user
7. An API that lets you view the users and seat they are allocated by the requested section
8. An API to remove a user from the train
9. An API to modify a user's seat

## Features

- **Ticket management**: Allows users to purchase, retrieve receipt, modify, and delete tickets.
- **Seat management**: Automatically allocates seats and allows modification to requested seat (if available).
- **Seat chart**: Retrieves the list of the users and seat they are allocated in the entire train or by the requested section.
- **Exception handling**: Provides meaningful error responses for invalid inputs or actions.


## Technologies Used

- Java 17
- Spring Boot 3.0.5
- Spring Data JPA (for database operations)
- H2 Database (in-memory database for development and testing)
- Swagger (for API documentation and UI)
- JUnit 5 and Mockito (for testing)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/NilanjanaAn/CloudBees-train-ticket-booking-api.git
   ```

2. Navigate to the project directory:
    ```bash
    cd CloudBees-train-ticket-booking-api\trainTicketBookingAPI
    ```

3. Build the project:
    ```bash
    mvn clean install
    ```

4. Run the application:
    ```bash
    mvn spring-boot:run
    ```

The application will start running on http://localhost:8080.

Optionally navigate to http://localhost:8080/swagger-ui/index.html to access the Swagger UI to test the API endpoints.


## API Endpoints


- 🎫**GET /api/ticket/receipt/{pnr}**: Retrieve a ticket's receipt by its PNR (Passenger Name Record) number.
- 💺**GET /api/ticket/seatchart**: Retrieve the list of the users and seat they are allocated.
- 💺**GET /api/ticket/seatchart/{section}**: Retrieve the list of the users and seat they are allocated by the requested section.
- 💵**POST /api/ticket/purchase**: Purchase a new ticket from London to France for $20 by entering the details of the user.
- 🗑️**DELETE /api/ticket/remove/{pnr}**: Remove a user's ticket by its PNR number.
- 📝**PUT /api/ticket/modify/{pnr}**: Modify the seat allocated to a ticket by its PNR number, if the requested seat is present and unoccupied.

## Testing
The application includes unit tests to ensure the correctness of the implemented functionality. The tests can be run using the following command:

    mvn test

    