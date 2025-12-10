# Smart Library Management System (SLMS)

**Student:** M.Y.M. SAJIDH  
**Student ID:** K2530341  
**Version:** 1.0

## Overview

The Smart Library Management System (SLMS) is a comprehensive Java application with a JavaFX GUI and CSV-based persistence. It implements multiple design patterns and supports role-based functionality for librarians and users (students, faculty, guests).

## Features

- **Book Management**: Full CRUD operations for books
- **User Management**: Full CRUD operations for users
- **Borrow/Return System**: Automatic fine calculation based on membership type
- **Reservation Queue**: FIFO reservation system with 48-hour hold window
- **Notifications**: Observer pattern-based notification system
- **Reports**: Most-borrowed books, active borrowers, overdue books
- **Design Patterns**: Observer, Strategy, Builder, Command, State, Decorator

## Technology Stack

- **Java:** 17
- **GUI Framework:** JavaFX 21
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **CSV Library:** OpenCSV 5.9
- **Code Coverage:** JaCoCo

## Project Structure

```
slms/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── k2530341/
│   │               └── slms/
│   │                   ├── app/           # JavaFX application & UI
│   │                   ├── model/         # Domain models
│   │                   ├── patterns/      # Design pattern implementations
│   │                   ├── persistence/   # CSV managers
│   │                   └── service/       # Business logic
│   └── test/
│       └── java/
│           └── com/
│               └── k2530341/
│                   └── slms/              # Unit tests
├── data/                                   # CSV data files
├── pom.xml                                 # Maven configuration
└── README.md                               # This file
```

## Building the Project

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build Commands

```powershell
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Generate code coverage report
mvn test jacoco:report

# Package as JAR
mvn clean package
```

The coverage report will be available at `target/site/jacoco/index.html`.

## Running the Application

### Using Maven

```powershell
mvn javafx:run
```

### Using JAR (after packaging)

```powershell
java -jar target/slms-1.0.jar
```

## Usage

### Role Selection

1. **Librarian**: Access to book/user management, reports, and overdue tracking
2. **User**: Enter User ID (e.g., U001, U002) to access borrowing, reservations, and notifications

### Librarian Functions

- **Manage Books**: Add, edit, delete books
- **Manage Users**: Add, edit, delete users
- **Reports**: View and export:
  - Most borrowed books
  - Most active borrowers
  - Overdue books
- **Overdue Tracking**: Monitor overdue borrows

### User Functions

- **Search Books**: Search by title, author, or category
- **Borrow Books**: Borrow available books (within limits)
- **Return Books**: Return borrowed books (automatic fine calculation)
- **Reservations**: Reserve books and view reservation status
- **Notifications**: View system notifications

## Business Rules

### Membership Types

| Type    | Borrow Duration | Borrow Limit | Fine/Day (LKR) |
|---------|----------------|--------------|----------------|
| STUDENT | 14 days        | 5 books      | 50             |
| FACULTY | 30 days        | 10 books     | 20             |
| GUEST   | 7 days         | 2 books      | 100            |

### Additional Rules

- **Max Unpaid Fine Limit**: LKR 5,000 (blocks new borrows)
- **Reservation Hold Window**: 48 hours after notification
- **Fine Calculation**: Uses Strategy pattern based on membership type

## Design Patterns

### 1. Observer Pattern
- **Subject**: `K2530341_Book`
- **Observer**: `K2530341_User`
- **Usage**: Notification system for book availability

### 2. Strategy Pattern
- **Interface**: `K2530341_FineStrategy`
- **Implementations**: `StudentFineStrategy`, `FacultyFineStrategy`, `GuestFineStrategy`
- **Usage**: Calculate fines based on membership type

### 3. Builder Pattern
- **Class**: `K2530341_BookBuilder`
- **Usage**: Construct books with optional fields

### 4. Command Pattern
- **Interface**: `K2530341_Command`
- **Implementations**: `BorrowCommand`, `ReturnCommand`, `ReserveCommand`, `CancelReservationCommand`
- **Manager**: `K2530341_CommandManager` (supports undo)

### 5. State Pattern
- **Interface**: `K2530341_BookState`
- **States**: `AvailableState`, `BorrowedState`, `ReservedState`
- **Usage**: Manage book availability transitions

### 6. Decorator Pattern
- **Component**: `K2530341_BookComponent`
- **Decorators**: `FeaturedDecorator`, `RecommendedDecorator`
- **Usage**: Add optional display features to books

## CSV Data Format

### books.csv
```
bookId,title,author,category,isbn,availabilityStatus,borrowHistoryCount,optionalTags,edition
```

### users.csv
```
userId,name,email,contactNumber,membershipType,currentBorrowCount
```

### borrows.csv
```
borrowId,bookId,userId,borrowDateISO,dueDateISO,returnDateISO,finePaid
```

### reservations.csv
```
reservationId,bookId,userId,reservationDateISO,notifiedAtISO,status
```

### notifications.csv
```
notificationId,userId,type,message,dateISO,readFlag
```

## Testing

Run all tests with coverage:

```powershell
mvn clean test jacoco:report
```

Test classes cover:
- Borrow/return operations
- Fine calculations for all membership types
- Reservation flow
- All design patterns
- CSV persistence

Target coverage: >70%

## Sample Data

The `data/` folder contains sample CSV files with:
- 10 books
- 10 users (mix of students, faculty, guests)
- Sample borrow records
- Sample reservations
- Sample notifications

## License

Academic project for educational purposes.

## Author

**M.Y.M. SAJIDH**  
Student ID: K2530341

## Acknowledgments

This project was developed with AI assistance for code generation, design patterns implementation, and testing. All architectural decisions and requirements interpretation were made by the author.
