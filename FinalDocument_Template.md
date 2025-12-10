# Smart Library Management System - Final Documentation

**Student Name:** M.Y.M. SAJIDH  
**Student ID:** K2530341  
**Date:** December 10, 2025  
**Version:** 1.0

---

## Executive Summary

This document presents the Smart Library Management System (SLMS), a comprehensive Java-based application implementing a feature-rich library management solution with a graphical user interface and CSV-based data persistence. The system demonstrates the practical application of object-oriented design principles and six key design patterns: Observer, Strategy, Builder, Command, State, and Decorator.

The SLMS supports role-based functionality for librarians and library users (students, faculty, and guests), providing complete book and user management, an intelligent borrowing system with automated fine calculation, a FIFO reservation queue, real-time notifications, and comprehensive reporting capabilities.

### Key Features
- Full CRUD operations for books and users
- Membership-based borrowing rules and fine calculation
- FIFO reservation system with 48-hour hold window
- Observer-based notification system
- Comprehensive reporting and CSV export
- Command pattern with undo support
- State-based book availability management

### Technology Stack
- Java 17
- JavaFX 21
- Maven
- JUnit 5
- OpenCSV
- JaCoCo for code coverage

---

## 1. Introduction

### 1.1 Project Overview

The Smart Library Management System (SLMS) is designed to automate and streamline library operations for educational institutions. The system addresses the following key requirements:

1. **Multi-role Support**: Separate interfaces for librarians (administrative) and users (borrowers)
2. **Automated Business Logic**: Membership-based rules for borrowing, fines, and limits
3. **Intelligent Reservations**: FIFO queue with automatic notifications
4. **Data Persistence**: CSV-based storage with atomic write operations
5. **Design Patterns**: Implementation of six industry-standard patterns
6. **Comprehensive Testing**: >70% code coverage with JUnit 5

### 1.2 Business Requirements

#### User Roles

1. **Librarian**
   - Manage book catalog (CRUD operations)
   - Manage user accounts (CRUD operations)
   - View and export reports
   - Monitor overdue books

2. **User (Student/Faculty/Guest)**
   - Search and browse books
   - Borrow and return books
   - Reserve books
   - View notifications
   - View borrowing history

#### Membership Rules

| Membership Type | Borrow Duration | Borrow Limit | Fine Rate (LKR/day) |
|----------------|-----------------|--------------|---------------------|
| STUDENT        | 14 days         | 5 books      | 50                  |
| FACULTY        | 30 days         | 10 books     | 20                  |
| GUEST          | 7 days          | 2 books      | 100                 |

#### Business Constraints

- Maximum unpaid fine limit: LKR 5,000 (blocks new borrows)
- Reservation hold window: 48 hours after notification
- Reservation queue: FIFO per book
- Date arithmetic uses `java.time` API

---

## 2. System Architecture

### 2.1 Package Structure

```
com.k2530341.slms
├── app                 # JavaFX application and GUI panes
├── model               # Domain models
│   ├── book           # Book-related models
│   ├── user           # User-related models
│   ├── reservation    # Reservation models
│   └── notification   # Notification models
├── patterns            # Design pattern implementations
│   ├── observer       # Observer pattern
│   ├── strategy       # Strategy pattern
│   ├── builder        # Builder pattern
│   ├── command        # Command pattern
│   ├── state          # State pattern
│   └── decorator      # Decorator pattern
├── persistence         # CSV persistence managers
├── service            # Business logic services
└── util               # Utility classes
```

### 2.2 Layer Architecture

The system follows a layered architecture:

1. **Presentation Layer** (JavaFX GUI)
   - Role selection screen
   - Librarian dashboard
   - User dashboard
   - Specialized panes for each function

2. **Service Layer** (Business Logic)
   - `K2530341_LibraryService`: Core business operations
   - `K2530341_ReportService`: Reporting and analytics
   - `K2530341_CommandManager`: Command execution and undo

3. **Domain Layer** (Models)
   - Core entities: Book, User, Borrow, Reservation, Notification
   - Design pattern implementations

4. **Persistence Layer** (CSV Managers)
   - Atomic write operations
   - Load/save for all entities

---

## 3. Design Patterns Implementation

This section documents the implementation and rationale for each of the six required design patterns.

### 3.1 Observer Pattern

**Purpose**: Enable automatic notification of users when books become available.

**Implementation**:
- **Subject**: `K2530341_Book` (implements `K2530341_Subject`)
- **Observer**: `K2530341_User` (implements `K2530341_Observer`)

**Key Classes**:
```
K2530341_Subject (interface)
  - attach(observer): void
  - detach(observer): void
  - notifyObservers(event, message): void

K2530341_Observer (interface)
  - update(event, message): void
```

**Usage Example**:
When a user reserves a book, they are attached as an observer. When the book is returned, the library service notifies all observers (reservers) that the book is available.

**Files**:
- `src/main/java/com/k2530341/slms/patterns/observer/K2530341_Subject.java`
- `src/main/java/com/k2530341/slms/patterns/observer/K2530341_Observer.java`
- `src/main/java/com/k2530341/slms/model/book/K2530341_Book.java` (Subject)
- `src/main/java/com/k2530341/slms/model/user/K2530341_User.java` (Observer)

**Rationale**: The Observer pattern decouples the notification system from the borrowing logic. Books don't need to know the details of how users receive notifications; they simply notify all registered observers. This makes it easy to add new notification channels (email, SMS, etc.) without modifying the Book class.

### 3.2 Strategy Pattern

**Purpose**: Calculate fines based on membership type without conditional logic.

**Implementation**:
- **Interface**: `K2530341_FineStrategy`
- **Concrete Strategies**:
  - `K2530341_StudentFineStrategy` (50 LKR/day)
  - `K2530341_FacultyFineStrategy` (20 LKR/day)
  - `K2530341_GuestFineStrategy` (100 LKR/day)

**Key Methods**:
```
K2530341_FineStrategy
  - calculateFine(overdueDays): double
  - getFinePerDay(): double
```

**Usage Example**:
When a book is returned, the `LibraryService` selects the appropriate fine strategy based on the user's membership type and delegates fine calculation to that strategy.

**Files**:
- `src/main/java/com/k2530341/slms/patterns/strategy/K2530341_FineStrategy.java`
- `src/main/java/com/k2530341/slms/patterns/strategy/K2530341_StudentFineStrategy.java`
- `src/main/java/com/k2530341/slms/patterns/strategy/K2530341_FacultyFineStrategy.java`
- `src/main/java/com/k2530341/slms/patterns/strategy/K2530341_GuestFineStrategy.java`

**Rationale**: The Strategy pattern eliminates the need for large if-else or switch statements based on membership type. Each strategy encapsulates its own calculation logic, making it easy to add new membership types or modify fine rules without changing the LibraryService. This adheres to the Open/Closed Principle.

### 3.3 Builder Pattern

**Purpose**: Construct Book objects with many optional fields in a readable, flexible way.

**Implementation**:
- **Builder**: `K2530341_BookBuilder`
- **Product**: `K2530341_Book`

**Key Methods**:
```
K2530341_BookBuilder
  - setBookId(id): K2530341_BookBuilder
  - setTitle(title): K2530341_BookBuilder
  - setOptionalTags(tags): K2530341_BookBuilder
  - setEdition(edition): K2530341_BookBuilder
  - build(): K2530341_Book
```

**Usage Example**:
```java
K2530341_Book book = new K2530341_BookBuilder()
    .setBookId("B001")
    .setTitle("Clean Code")
    .setAuthor("Robert Martin")
    .setOptionalTags("java;bestseller")
    .setEdition("1st")
    .build();
```

**Files**:
- `src/main/java/com/k2530341/slms/patterns/builder/K2530341_BookBuilder.java`

**Rationale**: Books have many attributes, some optional (tags, edition, reviews). A constructor with many parameters would be difficult to use and read. The Builder pattern provides a fluent interface for constructing books, making code more readable and maintainable. Optional fields have sensible defaults.

### 3.4 Command Pattern

**Purpose**: Encapsulate library operations as objects, enabling undo functionality and operation logging.

**Implementation**:
- **Command Interface**: `K2530341_Command`
- **Concrete Commands**:
  - `K2530341_BorrowCommand`
  - `K2530341_ReturnCommand`
  - `K2530341_ReserveCommand`
  - `K2530341_CancelReservationCommand`
- **Invoker**: `K2530341_CommandManager`

**Key Methods**:
```
K2530341_Command
  - execute(): boolean
  - undo(): void
  - getDescription(): String

K2530341_CommandManager
  - executeCommand(command): boolean
  - undoLastCommand(): boolean
```

**Usage Example**:
```java
K2530341_Command borrowCmd = new K2530341_BorrowCommand(libraryService, bookId, userId);
commandManager.executeCommand(borrowCmd);
// Later, if needed:
commandManager.undoLastCommand();
```

**Files**:
- `src/main/java/com/k2530341/slms/patterns/command/K2530341_Command.java`
- `src/main/java/com/k2530341/slms/patterns/command/K2530341_BorrowCommand.java`
- `src/main/java/com/k2530341/slms/patterns/command/K2530341_ReturnCommand.java`
- `src/main/java/com/k2530341/slms/patterns/command/K2530341_ReserveCommand.java`
- `src/main/java/com/k2530341/slms/patterns/command/K2530341_CancelReservationCommand.java`
- `src/main/java/com/k2530341/slms/patterns/command/K2530341_CommandManager.java`

**Rationale**: The Command pattern separates the request for an operation from its execution. This enables several benefits: (1) Commands can be logged for auditing, (2) Commands can be undone (e.g., accidental borrow), (3) Commands can be queued or executed later, (4) GUI actions are decoupled from business logic. The CommandManager maintains a history stack for undo operations.

### 3.5 State Pattern

**Purpose**: Manage book availability state transitions cleanly without complex conditionals.

**Implementation**:
- **State Interface**: `K2530341_BookState`
- **Concrete States**:
  - `K2530341_AvailableState`
  - `K2530341_BorrowedState`
  - `K2530341_ReservedState`
- **Context**: `K2530341_Book`

**State Transition Diagram**:
```
AVAILABLE --borrow--> BORROWED
BORROWED --return--> AVAILABLE or RESERVED (if queue exists)
AVAILABLE --reserve--> RESERVED
RESERVED --borrow--> BORROWED (by reserver)
RESERVED --expire--> AVAILABLE
```

**Files**:
- `src/main/java/com/k2530341/slms/patterns/state/K2530341_BookState.java`
- `src/main/java/com/k2530341/slms/patterns/state/K2530341_AvailableState.java`
- `src/main/java/com/k2530341/slms/patterns/state/K2530341_BorrowedState.java`
- `src/main/java/com/k2530341/slms/patterns/state/K2530341_ReservedState.java`

**Rationale**: Books have complex state-dependent behavior. Instead of large if-else blocks checking the current status, each state encapsulates its own behavior for borrow, return, and reserve operations. This makes adding new states or modifying state behavior straightforward and maintains the Single Responsibility Principle.

### 3.6 Decorator Pattern

**Purpose**: Add optional display features (Featured, Recommended) to books without modifying the Book class.

**Implementation**:
- **Component Interface**: `K2530341_BookComponent`
- **Concrete Component**: `K2530341_BasicBook`
- **Decorator Base**: `K2530341_BookDecorator`
- **Concrete Decorators**:
  - `K2530341_FeaturedDecorator` (adds ⭐ and +10 priority)
  - `K2530341_RecommendedDecorator` (adds 👍 and +5 priority)

**Usage Example**:
```java
K2530341_BookComponent book = new K2530341_BasicBook(actualBook);
K2530341_BookComponent featured = new K2530341_FeaturedDecorator(book);
K2530341_BookComponent recommended = new K2530341_RecommendedDecorator(featured);
// Stacked decorators: both featured and recommended
```

**Files**:
- `src/main/java/com/k2530341/slms/patterns/decorator/K2530341_BookComponent.java`
- `src/main/java/com/k2530341/slms/patterns/decorator/K2530341_BasicBook.java`
- `src/main/java/com/k2530341/slms/patterns/decorator/K2530341_BookDecorator.java`
- `src/main/java/com/k2530341/slms/patterns/decorator/K2530341_FeaturedDecorator.java`
- `src/main/java/com/k2530341/slms/patterns/decorator/K2530341_RecommendedDecorator.java`

**Rationale**: The Decorator pattern allows adding responsibilities to objects dynamically without subclassing. Books can be displayed as Featured, Recommended, or both, without creating a combinatorial explosion of Book subclasses. Decorators can be stacked, and their effects (priority) are cumulative. This follows the Open/Closed Principle.

---

## 4. UML Diagrams

### 4.1 Class Diagram

**Location**: `UML/K2530341_ClassDiagram.png` (generated from PlantUML)

The class diagram shows all classes with the `K2530341_` prefix, illustrating:
- Core domain models (Book, User, Borrow, Reservation, Notification)
- All design pattern interfaces and implementations
- Service layer (LibraryService, ReportService)
- Persistence layer (CSV managers)
- Relationships: composition, aggregation, implementation, dependency

**Key Relationships**:
- Book implements Subject (Observer pattern)
- User implements Observer
- LibraryService contains Books, Users, Borrows, etc. (composition)
- Commands depend on LibraryService
- Strategy implementations realize FineStrategy interface

### 4.2 Sequence Diagram: Borrow Book Flow

[A sequence diagram would be inserted here showing the interaction between User, GUI, CommandManager, BorrowCommand, LibraryService, Book, User, and CSV managers during a book borrowing operation]

**Flow**:
1. User selects book and clicks "Borrow"
2. GUI creates BorrowCommand
3. GUI calls CommandManager.executeCommand()
4. CommandManager calls BorrowCommand.execute()
5. BorrowCommand calls LibraryService.borrowBook()
6. LibraryService validates (availability, limits, fines)
7. LibraryService creates Borrow record
8. LibraryService updates Book state (performBorrow)
9. Book transitions to BorrowedState
10. LibraryService updates User borrow count
11. LibraryService saves data to CSV
12. Success/failure returned to GUI

---

## 5. CSV Data Schemas

All data is persisted to CSV files with atomic write operations (write to .tmp, then rename).

### 5.1 books.csv

**Header**:
```
bookId,title,author,category,isbn,availabilityStatus,borrowHistoryCount,optionalTags,edition
```

**Example**:
```
B001,Clean Code,Robert Martin,Programming,978-0132350884,AVAILABLE,15,bestseller;java,1st
```

**Managed by**: `K2530341_BookCSVManager`

### 5.2 users.csv

**Header**:
```
userId,name,email,contactNumber,membershipType,currentBorrowCount
```

**Example**:
```
U001,Alice Johnson,alice@university.edu,+94771234567,STUDENT,2
```

**Managed by**: `K2530341_UserCSVManager`

### 5.3 borrows.csv

**Header**:
```
borrowId,bookId,userId,borrowDateISO,dueDateISO,returnDateISO,finePaid
```

**Example**:
```
BR001,B001,U001,2025-11-20,2025-12-04,2025-12-03,0.0
```

**Notes**:
- ISO format: `yyyy-MM-dd`
- `returnDateISO` empty if not returned
- `finePaid` calculated on return

**Managed by**: `K2530341_BorrowCSVManager`

### 5.4 reservations.csv

**Header**:
```
reservationId,bookId,userId,reservationDateISO,notifiedAtISO,status
```

**Example**:
```
RES001,B005,U004,2025-12-01T10:00:00,2025-12-02T09:00:00,NOTIFIED
```

**Notes**:
- ISO format: `yyyy-MM-dd'T'HH:mm:ss`
- Status: PENDING, NOTIFIED, CANCELLED, EXPIRED

**Managed by**: `K2530341_ReservationCSVManager`

### 5.5 notifications.csv

**Header**:
```
notificationId,userId,type,message,dateISO,readFlag
```

**Example**:
```
N001,U001,DUE_REMINDER,Your book 'Clean Code' is due in 3 days.,2025-12-01,true
```

**Notes**:
- Type: DUE_REMINDER, OVERDUE_ALERT, RESERVATION_READY, OTHER

**Managed by**: `K2530341_NotificationCSVManager`

---

## 6. Testing & Quality Assurance

### 6.1 Test Coverage

**Target**: >70% code coverage  
**Tool**: JaCoCo Maven Plugin

**Test Classes**:
1. `K2530341_LibraryServiceTest`: Core business logic
2. `K2530341_ObserverPatternTest`: Observer attach/detach/notify
3. `K2530341_StrategyPatternTest`: Fine calculation for all membership types
4. `K2530341_StatePatternTest`: Book state transitions
5. `K2530341_BuilderPatternTest`: Book construction
6. `K2530341_DecoratorPatternTest`: Decorator stacking and priority

### 6.2 Test Scenarios

#### Borrow/Return Tests
- ✓ Successful borrow when book available
- ✓ Borrow fails when book unavailable
- ✓ Borrow fails when user at limit
- ✓ Borrow fails when unpaid fines >= 5000 LKR
- ✓ Return without fine
- ✓ Return with fine (Student: 50 LKR/day)
- ✓ Return with fine (Faculty: 20 LKR/day)
- ✓ Return with fine (Guest: 100 LKR/day)

#### Reservation Tests
- ✓ Reserve book when borrowed
- ✓ Automatic notification when book returned
- ✓ Book transitions to RESERVED for first in queue
- ✓ 48-hour expiry check

#### Pattern Tests
- ✓ Observer attach/detach/notify
- ✓ Strategy fine calculation correctness
- ✓ State transitions (Available ↔ Borrowed ↔ Reserved)
- ✓ Builder with required fields only
- ✓ Builder with all fields
- ✓ Decorator priority accumulation

### 6.3 Running Tests

```powershell
# Run all tests
mvn test

# Run with coverage report
mvn clean test jacoco:report

# View coverage report
start target/site/jacoco/index.html
```

---

## 7. User Interface

### 7.1 Role Selection Screen

**Purpose**: Choose between Librarian and User roles.

**Features**:
- Displays student name and ID (M.Y.M. SAJIDH, K2530341)
- Two role buttons: Librarian and User

### 7.2 Librarian Dashboard

**Tabs**:
1. **Manage Books**: Add, edit, delete, view books in table
2. **Manage Users**: Add, edit, delete, view users in table
3. **Reports**: Generate and export most-borrowed books, active borrowers
4. **Overdue Books**: List of overdue borrows with borrower and days overdue

### 7.3 User Dashboard

**Tabs**:
1. **Search Books**: Search by title/author/category, borrow, reserve
2. **My Borrows**: View active borrows, return books
3. **My Reservations**: View reservation status
4. **Notifications**: View all notifications (due reminders, overdue alerts, reservation ready)

### 7.4 Screenshots

[Screenshots would be inserted here showing each main screen]

---

## 8. System Operation

### 8.1 Build and Run

```powershell
# Clone or extract project
cd SmartLibraryManagement

# Build project
mvn clean package

# Run application
mvn javafx:run

# Or run JAR
java -jar target/slms-1.0.jar
```

### 8.2 Sample Users (for testing)

| User ID | Name              | Type    | Password/Login         |
|---------|-------------------|---------|------------------------|
| U001    | Alice Johnson     | STUDENT | Just enter U001        |
| U002    | Prof. Robert Smith| FACULTY | Just enter U002        |
| U005    | Guest User One    | GUEST   | Just enter U005        |

### 8.3 Typical Workflows

**Librarian Workflow**:
1. Log in as Librarian
2. Add new books via "Manage Books" tab
3. Add new users via "Manage Users" tab
4. View reports to analyze library usage
5. Monitor overdue books

**User Workflow**:
1. Log in with User ID (e.g., U001)
2. Search for books
3. Borrow available books
4. Check "My Borrows" to see due dates
5. Return books before due date to avoid fines
6. Reserve books if needed
7. Check notifications for alerts

---

## 9. Acknowledgement of AI Contribution

### 9.1 AI Usage Declaration

This project was developed with substantial assistance from GitHub Copilot and Claude AI (Anthropic) for code generation, implementation, and testing. The AI assisted with:

**Code Generation**:
- Implementation of all Java classes based on requirements
- Generation of JavaFX GUI components
- Creation of CSV persistence managers
- Implementation of all six design patterns
- Generation of comprehensive unit tests

**Design and Architecture**:
- Package structure recommendations
- Design pattern selection and implementation strategies
- UML diagram structure and PlantUML code

**Documentation**:
- README.md content
- This final documentation structure
- Code comments and Javadoc
- CHANGELOG.md

**Testing**:
- JUnit test case generation
- Test coverage strategies
- Sample data creation

### 9.2 Student Contribution

While AI tools were used extensively for implementation, the following aspects were managed by the student (M.Y.M. SAJIDH, K2530341):

**Requirements Analysis**:
- Understanding and interpreting project requirements
- Defining business rules and constraints
- Specifying membership types and fine rates

**Architectural Decisions**:
- Selection of JavaFX over Swing
- Choice of CSV for persistence
- Decision to implement all required patterns
- Validation of AI-generated code for correctness

**Quality Assurance**:
- Verification that all requirements are met
- Testing the complete system
- Ensuring code adheres to academic standards
- Final review and approval of all deliverables

**Academic Integrity**:
The student acknowledges that AI tools were used as coding assistants, similar to using IDEs with autocomplete or referring to documentation. All architectural decisions, requirements interpretation, and final validation were performed by the student. The use of AI is disclosed transparently as required by academic integrity policies.

---

## 10. Conclusion

The Smart Library Management System successfully implements a comprehensive library management solution demonstrating:

1. **Object-Oriented Design**: All six required design patterns implemented correctly and documented
2. **Business Logic**: Complete implementation of membership rules, fine calculation, and reservation system
3. **Persistence**: Robust CSV-based storage with atomic writes
4. **User Interface**: Intuitive JavaFX GUI with role-based dashboards
5. **Testing**: Comprehensive unit tests with >70% coverage
6. **Documentation**: Complete UML diagrams, README, and this final document

### Key Achievements

- ✅ All functional requirements implemented
- ✅ All 6 design patterns implemented and documented
- ✅ CSV persistence with atomic writes
- ✅ JavaFX GUI with all required screens
- ✅ Comprehensive testing (>70% coverage)
- ✅ UML diagrams with K2530341_ prefix
- ✅ Complete documentation
- ✅ AI usage transparently acknowledged

### Future Enhancements

Potential improvements for future versions:
- Database backend (MySQL, PostgreSQL) for production use
- Email/SMS notifications via external services
- Book cover images and rich media
- Advanced search with filters
- User profile photos
- Book reviews and ratings
- Analytics dashboard with charts
- Multi-library support
- RESTful API for mobile apps

---

## 11. References

### Design Patterns
- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.

### Java & JavaFX
- Oracle. (2023). *Java Platform, Standard Edition Documentation*. Retrieved from https://docs.oracle.com/en/java/
- OpenJFX. (2023). *JavaFX Documentation*. Retrieved from https://openjfx.io/

### Testing
- JUnit Team. (2023). *JUnit 5 User Guide*. Retrieved from https://junit.org/junit5/docs/current/user-guide/

### Tools
- Apache Maven. (2023). *Maven Documentation*. Retrieved from https://maven.apache.org/guides/
- OpenCSV. (2023). *OpenCSV Documentation*. Retrieved from http://opencsv.sourceforge.net/

---

**END OF DOCUMENT**

Prepared by: M.Y.M. SAJIDH  
Student ID: K2530341  
Date: December 10, 2025
