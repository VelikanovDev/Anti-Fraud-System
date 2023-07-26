# Anti-Fraud System

The Anti-Fraud System is a Spring Boot application designed to detect and prevent fraudulent transactions in a financial system. The system employs various strategies to analyze transaction data and identify suspicious activities, stolen cards, and potentially risky IPs. It also provides administrative functionalities to manage user roles and access.

# Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup](#setup)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)

# Introduction
  The Anti-Fraud System is a crucial component of any financial platform, helping to safeguard users from fraudulent activities and maintain a secure environment for transactions. This system employs various checks and validations to detect suspicious transactions and mitigate potential risks.

# Features

- Role-Based Authentication: The Anti-Fraud System implements role-based authentication, allowing administrators to manage user roles and access privileges.

- User Roles and Permissions: The system has different user roles, including Administrator, Merchant, Support, each with specific capabilities and access levels.

- API Endpoints: The application provides well-defined API endpoints for managing users, transactions, and suspicious activities.

- User Registration and Role Assignment: When new users register with the system, the first user is automatically assigned the role of "Administrator," granting them administrative privileges. Subsequent users are automatically assigned the role of "Merchant," allowing them access to specific merchant functionalities. However, by default, these new merchant users are in a "LOCK" state, which means they must be approved and unlocked by the "Administrator" before they can fully access the system and perform any actions.

- Transaction Validation: The system analyzes transaction data to validate the legitimacy of each transaction using the <a href="https://en.wikipedia.org/wiki/Luhn_algorithm">Luhn algorithm</a>.

- IP Monitoring: SuspiciousIPService keeps track of suspicious IP addresses and can block access to prevent potential fraudulent activities.

- Stolen Card Detection: The application includes functionality to detect stolen cards and take appropriate actions to prevent unauthorized transactions.

- Detailed Transaction History: Users with "Support" role can access a detailed transaction history, including transaction status and additional information.

- Unit Testing: Comprehensive unit tests are included to ensure the correctness and reliability of the system.

# Technologies Used
-  Java 17
-  Spring Boot 3.1.2
-  Spring Data JPA for database interactions
-  Spring Security for authentication and authorization
-  PostgreSQL as the relational database
-  Hibernate as the ORM tool
-  Lombok for boilerplate code reduction
-  Maven for build and dependency management
-  JUnit 5 and Mockito for unit testing
-  Jackson for JSON serialization and deserialization

# Setup
1. Clone the repository.
2. Install Java 17 and PostgreSQL on your system. 
3. Create a PostgreSQL database named "antifraud_db."
4. Set the database username and password in the application.properties file. 
5. Build the project using Maven: `mvn clean package`

# Usage
1. Run the application: java -jar target/Anti-Fraud-System-0.0.1-SNAPSHOT.jar 
2. The application will start on port 28852 (you can change this in the application.properties file). 
3. Access the API endpoints using tools like Postman or cURL.

# API Endpoints
The Anti-Fraud System provides several API endpoints for different functionalities. Below are some of the key endpoints:

1. ***/api/auth/user*** - Register a new user with role-based authentication. 
2. ***/api/auth/role*** - Update a user's role with ADMINISTRATOR privileges. 
3. ***/api/auth/access*** - Set the account access for a user. 
4. ***/api/auth/user/{username}*** - Delete a user by username. 
5. ***/api/auth/list*** - Get a list of all users.

Note: Please refer to the source code for additional API endpoints and detailed usage.
