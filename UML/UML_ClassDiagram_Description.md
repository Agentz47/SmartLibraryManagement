# UML Class Diagram Description - K2530341 SLMS

## Student Information
- **Student Name:** M.Y.M. SAJIDH
- **Student ID:** K2530341

## Class Diagram Overview

This document describes the UML class diagram for the Smart Library Management System (SLMS). All class names are prefixed with `K2530341_` as per academic requirements.

## Core Model Classes

### K2530341_Book
- **Attributes:**
  - bookId: String
  - title: String
  - author: String
  - category: String
  - isbn: String
  - availabilityStatus: K2530341_AvailabilityStatus
  - borrowHistoryCount: int
  - optionalTags: String
  - edition: String
  - currentState: K2530341_BookState
  - observers: List<K2530341_Observer>

- **Methods:**
  - performBorrow(): void
  - performReturn(): void
  - performReserve(): void
  - attach(observer): void
  - detach(observer): void
  - notifyObservers(event, message): void
  - incrementBorrowHistory(): void
  - [Getters and Setters]

- **Relationships:**
  - Implements K2530341_Subject (Observer pattern)
  - Has K2530341_BookState (State pattern)
  - Created by K2530341_BookBuilder (Builder pattern)

### K2530341_AvailabilityStatus (Enum)
- AVAILABLE
- BORROWED
- RESERVED

### K2530341_User
- **Attributes:**
  - userId: String
  - name: String
  - email: String
  - contactNumber: String
  - membershipType: K2530341_MembershipType
  - currentBorrowCount: int
  - unpaidFines: double
  - notifications: List<String>

- **Methods:**
  - update(event, message): void (Observer pattern)
  - incrementBorrowCount(): void
  - decrementBorrowCount(): void
  - addFine(amount): void
  - payFine(amount): void
  - [Getters and Setters]

- **Relationships:**
  - Implements K2530341_Observer (Observer pattern)
  - Has K2530341_MembershipType

### K2530341_MembershipType (Enum)
- STUDENT(14, 5, 50.0)
- FACULTY(30, 10, 20.0)
- GUEST(7, 2, 100.0)

- **Attributes:**
  - borrowDurationDays: int
  - borrowLimit: int
  - finePerDay: double

### K2530341_Borrow
- **Attributes:**
  - borrowId: String
  - bookId: String
  - userId: String
  - borrowDate: LocalDate
  - dueDate: LocalDate
  - returnDate: LocalDate
  - finePaid: double

- **Methods:**
  - isOverdue(): boolean
  - getOverdueDays(): long
  - [Getters and Setters]

### K2530341_Reservation
- **Attributes:**
  - reservationId: String
  - bookId: String
  - userId: String
  - reservationDate: LocalDateTime
  - notifiedAt: LocalDateTime
  - status: K2530341_ReservationStatus

- **Methods:**
  - isExpired(): boolean
  - [Getters and Setters]

### K2530341_ReservationStatus (Enum)
- PENDING
- NOTIFIED
- CANCELLED
- EXPIRED

### K2530341_Notification
- **Attributes:**
  - notificationId: String
  - userId: String
  - type: K2530341_NotificationType
  - message: String
  - date: LocalDate
  - readFlag: boolean

- **Methods:**
  - [Getters and Setters]

### K2530341_NotificationType (Enum)
- DUE_REMINDER
- OVERDUE_ALERT
- RESERVATION_READY
- FINE_ALERT

## Design Pattern Classes

### Observer Pattern

#### K2530341_Subject (Interface)
- attach(observer): void
- detach(observer): void
- notifyObservers(event, message): void

#### K2530341_Observer (Interface)
- update(event, message): void

**Implementation:**
- K2530341_Book implements Subject
- K2530341_User implements Observer

### Strategy Pattern

#### K2530341_FineStrategy (Interface)
- calculateFine(overdueDays): double
- getFinePerDay(): double

**Implementations:**
- K2530341_StudentFineStrategy
- K2530341_FacultyFineStrategy
- K2530341_GuestFineStrategy

### Builder Pattern

#### K2530341_BookBuilder
- **Attributes:**
  - bookId: String
  - title: String
  - author: String
  - category: String
  - isbn: String
  - availabilityStatus: K2530341_AvailabilityStatus
  - borrowHistoryCount: int
  - optionalTags: String
  - edition: String

- **Methods:**
  - setBookId(id): K2530341_BookBuilder
  - setTitle(title): K2530341_BookBuilder
  - setAuthor(author): K2530341_BookBuilder
  - [other setters]: K2530341_BookBuilder
  - build(): K2530341_Book

### Command Pattern

#### K2530341_Command (Interface)
- execute(): boolean
- undo(): void
- getDescription(): String

**Implementations:**
- K2530341_BorrowCommand
- K2530341_ReturnCommand
- K2530341_ReserveCommand
- K2530341_CancelReservationCommand

#### K2530341_CommandManager
- **Attributes:**
  - commandHistory: Stack<K2530341_Command>
  - maxHistorySize: int

- **Methods:**
  - executeCommand(command): boolean
  - undoLastCommand(): boolean
  - getHistoryDescription(): String
  - clearHistory(): void

### State Pattern

#### K2530341_BookState (Interface)
- borrow(book): void
- returnBook(book): void
- reserve(book): void
- getStateName(): String

**Implementations:**
- K2530341_AvailableState
- K2530341_BorrowedState
- K2530341_ReservedState

### Decorator Pattern

#### K2530341_BookComponent (Interface)
- getDescription(): String
- getPriority(): int

**Implementations:**
- K2530341_BasicBook (Concrete Component)
- K2530341_BookDecorator (Abstract Decorator)
  - K2530341_FeaturedDecorator
  - K2530341_RecommendedDecorator

## Service Layer

### K2530341_LibraryService
- **Attributes:**
  - MAX_UNPAID_LIMIT: double (constant = 5000.0)
  - books: Map<String, K2530341_Book>
  - users: Map<String, K2530341_User>
  - borrows: Map<String, K2530341_Borrow>
  - reservations: Map<String, K2530341_Reservation>
  - notifications: Map<String, K2530341_Notification>

- **Methods:**
  - initialize(): void
  - saveAllData(): void
  - addBook(book): void
  - updateBook(book): void
  - deleteBook(bookId): void
  - searchBooks(query): List<K2530341_Book>
  - addUser(user): void
  - updateUser(user): void
  - deleteUser(userId): void
  - borrowBook(bookId, userId): String
  - returnBook(borrowId): boolean
  - reserveBook(bookId, userId): String
  - cancelReservation(reservationId): boolean
  - createNotification(userId, type, message): void
  - [Additional helper methods]

### K2530341_ReportService
- **Attributes:**
  - libraryService: K2530341_LibraryService

- **Methods:**
  - getMostBorrowedBooks(topN): List<K2530341_Book>
  - getActiveBorrowers(topN): List<K2530341_User>
  - getOverdueReport(): List<Map<String, Object>>
  - exportMostBorrowedBooks(filename, topN): void
  - exportActiveBorrowers(filename, topN): void
  - exportOverdueReport(filename): void

## Persistence Layer

### CSV Managers
- K2530341_BookCSVManager
- K2530341_UserCSVManager
- K2530341_BorrowCSVManager
- K2530341_ReservationCSVManager
- K2530341_NotificationCSVManager

Each manager has:
- loadXXX(): List<Entity>
- saveXXX(entities): void

## Application Layer

### K2530341_SLMSApplication
- Main JavaFX application class
- Coordinates all GUI panes

### GUI Panes
- K2530341_BookManagementPane
- K2530341_UserManagementPane
- K2530341_ReportsPane
- K2530341_OverduePane
- K2530341_SearchBooksPane
- K2530341_MyBorrowsPane
- K2530341_MyReservationsPane
- K2530341_NotificationsPane

## Key Relationships

1. **Composition:**
   - LibraryService contains Books, Users, Borrows, Reservations, Notifications
   - Book has BookState
   - CommandManager has Commands

2. **Aggregation:**
   - ReportService uses LibraryService
   - GUI panes use Services

3. **Implementation:**
   - Book implements Subject
   - User implements Observer
   - Various classes implement pattern interfaces

4. **Dependency:**
   - Services depend on CSV managers
   - Commands depend on LibraryService
   - Fine strategies used by LibraryService

## Notes for UML Diagram Creation

When creating the visual UML class diagram:

1. Group classes by package/responsibility
2. Show all interfaces and their implementations
3. Indicate pattern relationships with stereotypes (<<Observer>>, <<Strategy>>, etc.)
4. Use proper UML notation for:
   - Composition (filled diamond)
   - Aggregation (hollow diamond)
   - Implementation (dashed arrow with hollow triangle)
   - Association (simple line)
5. Include multiplicity where relevant
6. Show key attributes and methods (can abbreviate for space)
7. Use notes to explain design pattern usage
8. Ensure all class names have K2530341_ prefix
9. Include Student ID K2530341 prominently on diagram

## Recommended UML Tool
- PlantUML
- Draw.io
- Lucidchart
- StarUML
- Visual Paradigm
