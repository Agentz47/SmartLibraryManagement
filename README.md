Smart Library Management System (SLMS)

Student: M.Y.M. SAJIDH
Student ID: K2530341
Version: 1.0

About This Project

This is a simple library management system made using Java and JavaFX. It helps manage books, users, borrowing, returning, and reservations. I also used six design patterns, and all data is stored in CSV files.

Main Features

The system can add, edit, and delete books with auto-generated IDs. It can also manage students, faculty, and guest users. Books can be borrowed and returned, and the system calculates fines automatically. If a book is borrowed, users can reserve it and get notified when it becomes available. The system also generates reports such as overdue books and most borrowed books. I implemented design patterns like Observer, Strategy, Builder, Command, State, and Decorator.

Technologies Used

Java 17

JavaFX 21

Maven

JUnit 5

OpenCSV

JaCoCo

Project Structure

The project includes folders for the GUI, models, design patterns, CSV storage, business logic, and tests. A separate data/ folder holds all CSV files.

Building the Project

You need Java 17 and Maven to build it.

mvn clean compile – compile

mvn test – run tests

mvn test jacoco:report – generate coverage

mvn clean package – build JAR

Coverage report will be in target/site/jacoco/index.html.

Running the Application

With Maven:

mvn javafx:run


With JAR:

java -jar target/slms-1.0.jar

Usage

You can select either Librarian or User.
Librarians can manage books, users, overdue records, and reports.
Users can search, borrow, return, reserve books, and check notifications.

Business Rules

There are three membership types: Student, Faculty, and Guest.
Each type has different borrow limits, durations, and daily fines.
The system blocks borrowing if a user has too many unpaid fines.
Fine calculation is done based on the membership type.

Design Patterns

Observer Pattern – for notifications

Strategy Pattern – for fine calculations

Builder Pattern – to create books with optional details

Command Pattern – for borrow, return, and reserve actions

State Pattern – to manage book availability

Decorator Pattern – to add optional tags to books

CSV Data Format

The system uses CSV files for books, users, borrows, reservations, and notifications. Each file contains the necessary fields like IDs, names, dates, and statuses.

Testing

Tests cover borrowing and returning operations, fine calculations, the reservation process, design patterns, and CSV data saving.
Target coverage is above 70%.

Sample Data

The data/ folder includes sample books, users, borrow history, reservations, and notifications.

License

This project is for academic and learning purposes.

Author

M.Y.M. SAJIDH
Student ID: K2530341