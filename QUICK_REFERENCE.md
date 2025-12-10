# Smart Library Management System - Quick Reference Guide

**Student:** M.Y.M. SAJIDH | **ID:** K2530341 | **Version:** 1.0

---

## 🚀 Quick Start

### Run the Application
```powershell
# Using Maven
mvn javafx:run

# Using JAR
java -jar target/slms-1.0.jar
```

### Run Tests
```powershell
mvn test
```

### Build Package
```powershell
mvn clean package
```

### View Test Coverage
```powershell
mvn test jacoco:report
start target/site/jacoco/index.html
```

---

## 📂 Key Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven configuration with all dependencies |
| `README.md` | Comprehensive build and usage instructions |
| `CHANGELOG.md` | Version history and feature documentation |
| `PROJECT_COMPLETION_SUMMARY.md` | Complete project status and checklist |
| `FinalDocument_Template.md` | Academic submission document template |
| `UML/K2530341_ClassDiagram.puml` | PlantUML class diagram source |
| `target/slms-1.0.jar` | Executable JAR (4.5 MB with dependencies) |
| `target/site/jacoco/index.html` | Code coverage report |

---

## 🎨 Design Patterns Quick Reference

| Pattern | Interface/Base | Concrete Classes | Usage |
|---------|---------------|------------------|-------|
| **Observer** | K2530341_Subject, K2530341_Observer | Book (Subject), User (Observer) | Real-time notifications |
| **Strategy** | K2530341_FineStrategy | StudentFineStrategy, FacultyFineStrategy, GuestFineStrategy | Fine calculation |
| **Builder** | K2530341_BookBuilder | - | Book construction |
| **Command** | K2530341_Command | BorrowCommand, ReturnCommand, ReserveCommand, CancelReservationCommand | Undo-able operations |
| **State** | K2530341_BookState | AvailableState, BorrowedState, ReservedState | Book status management |
| **Decorator** | K2530341_BookComponent | FeaturedDecorator, RecommendedDecorator | Book prioritization |

---

## 📊 Membership Rules

| Type | Borrow Days | Limit | Fine (LKR/day) |
|------|------------|-------|----------------|
| STUDENT | 14 | 5 | 50 |
| FACULTY | 30 | 10 | 20 |
| GUEST | 7 | 2 | 100 |

**Maximum Unpaid Fine**: LKR 5,000 (blocks new borrows)

---

## 💾 CSV Data Files

| File | Columns | Location |
|------|---------|----------|
| books.csv | 9 | data/books.csv |
| users.csv | 6 | data/users.csv |
| borrows.csv | 7 | data/borrows.csv |
| reservations.csv | 6 | data/reservations.csv |
| notifications.csv | 6 | data/notifications.csv |

All files use atomic writes (temp file → rename) for data integrity.

---

## 🖥️ GUI Structure

### Librarian Dashboard
1. **Manage Books**: Add/Edit/Delete/View books
2. **Manage Users**: Add/Edit/Delete/View users
3. **Reports**: Most borrowed books, Active borrowers, CSV export
4. **Overdue Books**: View all overdue borrows

### User Dashboard
1. **Search Books**: Find and borrow/reserve books
2. **My Borrows**: View active borrows, return books
3. **My Reservations**: Check reservation queue status
4. **Notifications**: View alerts and reminders

---

## 🧪 Test Results

```
Tests run: 23
Failures: 0
Errors: 0
Skipped: 0
Success Rate: 100% ✅
```

**Test Classes**: 6  
**Coverage**: Available at `target/site/jacoco/index.html`

---

## 📦 Package Structure

```
com.k2530341.slms
├── app              # JavaFX GUI (8 panes)
├── model            # Domain models (9 classes)
│   ├── book
│   ├── user
│   ├── reservation
│   └── notification
├── patterns         # Design patterns (17 classes)
│   ├── observer
│   ├── strategy
│   ├── builder
│   ├── command
│   ├── state
│   └── decorator
├── persistence      # CSV managers (5 classes)
└── service          # Business logic (2 classes)
```

---

## 🔑 Sample Test Data

### Users
- U001: Alice Johnson (STUDENT)
- U002: Prof. Robert Smith (FACULTY)
- U005: Guest User One (GUEST)

### Books
- B001: Clean Code
- B002: Design Patterns
- B003: Java Programming
- (7 more books available)

---

## ✅ Project Status

- [x] All 48 classes with K2530341_ prefix
- [x] 6 design patterns implemented
- [x] JavaFX GUI complete (8 panes)
- [x] CSV persistence with atomic writes
- [x] 23 tests, 100% pass rate
- [x] JaCoCo coverage report generated
- [x] Executable JAR built
- [x] Documentation complete
- [x] AI usage acknowledged

---

## 📞 Support

**Student**: M.Y.M. SAJIDH  
**Student ID**: K2530341  
**Project**: Smart Library Management System  
**Date**: December 10, 2025  

---

## 📚 Additional Documentation

For detailed information, refer to:
- **README.md**: Full build and usage guide
- **FinalDocument_Template.md**: Academic documentation with design rationale
- **PROJECT_COMPLETION_SUMMARY.md**: Complete implementation checklist
- **CHANGELOG.md**: Feature list and version history

---

**🎉 Project Complete and Ready for Submission! 🎉**
