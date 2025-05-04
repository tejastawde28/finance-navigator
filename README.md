# Finance Navigator

A comprehensive personal finance management web application that helps users track their financial assets and liabilities, providing insights through interactive dashboards and detailed reports.

## Features

- **User Management**: Create accounts, manage profiles, and secure access to financial data
- **Dashboard**: View financial summaries including total assets, liabilities, and net worth
- **Ledger Entry Management**: Record, categorize, and track financial transactions
- **Allocation System**: Categorize transactions by customizable allocation types
- **Financial Reporting**: Generate date-range reports and export to PDF format

## Technology Stack

- **Backend**: Java with Spring MVC framework
- **ORM**: Hibernate with MySQL database
- **Frontend**: JSP (JavaServer Pages) with CSS and JavaScript
- **Build Tool**: Maven


## Project Structure

The project follows a standard Spring MVC architecture with the following key components:

```txt
finance-navigator/
├── src/main/java/com/financenavigator/
│   ├── config/         # Application configuration
│   ├── controller/     # MVC controllers
│   ├── dao/            # Data access layer
│   ├── model/          # Entity models
│   ├── service/        # Business logic layer
│   └── validator/      # Input validation
├── src/main/resources/ # Properties and static resources
├── src/main/webapp/    # JSP views
└── pom.xml             # Maven configuration
```

## Setup and Installation

### Prerequisites

- JDK 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Database Setup

1. Create a MySQL database:
   ```sql
   CREATE DATABASE finance_navigator;
   ```

2. Set up database credentials as environment variables:
   ```properties
   # For Windows Command Prompt
   set DB_USERNAME=your_username
   set DB_PASSWORD=your_password
   set DB_HOST=localhost
   set DB_PORT=3306
   set DB_NAME=finance_navigator

   # For macOS/Linux
   export DB_USERNAME=your_username
   export DB_PASSWORD=your_password
   export DB_HOST=localhost
   export DB_PORT=3306
   export DB_NAME=finance_navigator
   ```

3. The application is configured to use these environment variables in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/finance_navigator?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   ```

### Building the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/finance-navigator.git
   cd finance-navigator
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

### Running the Application

1. Set environment variables for database configuration (see Database Setup section)

2. Deploy the generated WAR file to a servlet container like Tomcat, or run with embedded Tomcat:
   ```bash
   mvn spring-boot:run
   ```

3. Access the application at:
   ```
   http://localhost:8080/
   ```

### Setting Environment Variables in Development Environments

#### IntelliJ IDEA
1. Edit Run/Debug Configuration for your application
2. In "Environment variables" field, add:
   ```
   DB_USERNAME=your_username;DB_PASSWORD=your_password;DB_HOST=localhost;DB_PORT=3306;DB_NAME=finance_navigator
   ```

#### Eclipse
1. Run > Run Configurations
2. Select your application
3. Go to "Environment" tab
4. Add each variable by clicking "New" button

#### VS Code
1. Create or edit `.vscode/launch.json`
2. Add environment variables:
   ```json
   {
     "version": "0.2.0",
     "configurations": [
       {
         "type": "java",
         "name": "Launch Application",
         "request": "launch",
         "mainClass": "com.csye6220.financenavigator.FinanceNavigatorApplication",
         "env": {
           "DB_USERNAME": "your_username",
           "DB_PASSWORD": "your_password",
           "DB_HOST": "localhost",
           "DB_PORT": "3306",
           "DB_NAME": "finance_navigator"
         }
       }
     ]
   }
   ```

## Usage Guide

### Getting Started

1. **Create an Account**: Click on the "Enroll" link on the login page to create a new portfolio account.
2. **Log In**: Use your Client ID and Passcode to access your dashboard.

### Managing Your Portfolio

1. **Dashboard**: View your financial summary, including assets, liabilities, and net worth.
2. **Add Transactions**: Navigate to the Ledger section to add new financial transactions.
3. **Categorize Entries**: Assign transactions to predefined or custom allocation categories.
4. **Generate Reports**: Use the Reports page to analyze your finances over specific date ranges.
5. **Export to PDF**: Export financial reports to PDF format for offline viewing or sharing.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Framework community
- Hibernate ORM
- iText PDF library
- Bootstrap for UI components