# Changelog

All notable changes to the Smart Library Management System will be documented in this file.

## [1.0.0] - 2025-12-10

### Added
- Complete Smart Library Management System implementation
- JavaFX GUI with role-based dashboards (Librarian and User)
- CSV-based persistence with atomic write operations
- Six design patterns:
  - Observer pattern for notification system
  - Strategy pattern for fine calculation
  - Builder pattern for book construction
  - Command pattern for operations with undo support
  - State pattern for book availability management
  - Decorator pattern for book display features
- Full CRUD operations for books and users
- Borrow/return system with automatic fine calculation
- FIFO reservation queue with 48-hour hold window
- Comprehensive reporting system:
  - Most borrowed books
  - Most active borrowers
  - Overdue books with borrower details
- Export reports to CSV
- Comprehensive unit test suite (>70% coverage)
- Sample data for testing and demonstration
- Complete documentation (README, CHANGELOG)

### Features
- **Book Management**: Add, edit, delete, search books
- **User Management**: Add, edit, delete users with different membership types
- **Borrowing System**: 
  - Membership-based borrow limits (Student: 5, Faculty: 10, Guest: 2)
  - Membership-based borrow durations (Student: 14d, Faculty: 30d, Guest: 7d)
  - Membership-based fine rates (Student: 50 LKR/day, Faculty: 20 LKR/day, Guest: 100 LKR/day)
- **Reservation System**: 
  - FIFO queue per book
  - Automatic notification when book becomes available
  - 48-hour hold window
- **Notification System**: 
  - Due reminders
  - Overdue alerts
  - Reservation ready notifications
- **Reports**: 
  - Most borrowed books (configurable top N)
  - Most active borrowers (configurable top N)
  - Overdue books with details

### Technical Details
- Java 17
- JavaFX 21 for GUI
- Maven for build management
- JUnit 5 for testing
- JaCoCo for code coverage
- OpenCSV for CSV operations
- All classes prefixed with K2530341_ as per academic requirements

### Testing
- LibraryService tests (borrow, return, reservation, search)
- Design pattern tests (all 6 patterns)
- Fine calculation tests (all membership types)
- State transition tests
- Builder pattern tests
- Observer/notification tests

### Author
M.Y.M. SAJIDH (K2530341)
