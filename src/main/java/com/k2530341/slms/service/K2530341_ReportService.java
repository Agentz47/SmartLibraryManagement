package com.k2530341.slms.service;

import com.k2530341.slms.model.book.K2530341_Book;
import com.k2530341.slms.model.user.K2530341_User;
import com.k2530341.slms.model.K2530341_Borrow;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Report service for generating library reports.
 * Author: M.Y.M. SAJIDH (K2530341)
 */
public class K2530341_ReportService {
    private final K2530341_LibraryService libraryService;
    
    public K2530341_ReportService(K2530341_LibraryService libraryService) {
        this.libraryService = libraryService;
    }
    
    /**
     * Get most borrowed books.
     * @param topN Number of top books to retrieve
     * @return List of books sorted by borrow count
     */
    public List<K2530341_Book> getMostBorrowedBooks(int topN) {
        return libraryService.getAllBooks().stream()
            .sorted(Comparator.comparingInt(K2530341_Book::getBorrowHistoryCount).reversed())
            .limit(topN)
            .collect(Collectors.toList());
    }
    
    /**
     * Get most active borrowers.
     * @param topN Number of top borrowers to retrieve
     * @return List of users sorted by current borrow count
     */
    public List<K2530341_User> getActiveBorrowers(int topN) {
        return libraryService.getAllUsers().stream()
            .sorted(Comparator.comparingInt(K2530341_User::getCurrentBorrowCount).reversed())
            .limit(topN)
            .collect(Collectors.toList());
    }
    
    /**
     * Get overdue books with borrower details.
     * @return List of overdue borrow records
     */
    public List<Map<String, Object>> getOverdueReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        
        for (K2530341_Borrow borrow : libraryService.getOverdueBorrows()) {
            K2530341_Book book = libraryService.getBook(borrow.getBookId());
            K2530341_User user = libraryService.getUser(borrow.getUserId());
            
            if (book != null && user != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("borrowId", borrow.getBorrowId());
                record.put("bookTitle", book.getTitle());
                record.put("userName", user.getName());
                record.put("dueDate", borrow.getDueDate());
                record.put("overdueDays", borrow.getOverdueDays());
                report.add(record);
            }
        }
        
        return report;
    }
    
    /**
     * Export most borrowed books to CSV.
     * @param filename Output filename
     * @param topN Number of top books
     */
    public void exportMostBorrowedBooks(String filename, int topN) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            // Header
            String[] header = {"Book ID", "Title", "Author", "Borrow Count"};
            writer.writeNext(header);
            
            // Data
            List<K2530341_Book> books = getMostBorrowedBooks(topN);
            for (K2530341_Book book : books) {
                String[] row = {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    String.valueOf(book.getBorrowHistoryCount())
                };
                writer.writeNext(row);
            }
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }
    
    /**
     * Export active borrowers to CSV.
     * @param filename Output filename
     * @param topN Number of top borrowers
     */
    public void exportActiveBorrowers(String filename, int topN) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            // Header
            String[] header = {"User ID", "Name", "Membership Type", "Current Borrows"};
            writer.writeNext(header);
            
            // Data
            List<K2530341_User> users = getActiveBorrowers(topN);
            for (K2530341_User user : users) {
                String[] row = {
                    user.getUserId(),
                    user.getName(),
                    user.getMembershipType().toString(),
                    String.valueOf(user.getCurrentBorrowCount())
                };
                writer.writeNext(row);
            }
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }
    
    /**
     * Export overdue books to CSV.
     * @param filename Output filename
     */
    public void exportOverdueReport(String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            // Header
            String[] header = {"Borrow ID", "Book Title", "Borrower", "Due Date", "Overdue Days"};
            writer.writeNext(header);
            
            // Data
            List<Map<String, Object>> report = getOverdueReport();
            for (Map<String, Object> record : report) {
                String[] row = {
                    record.get("borrowId").toString(),
                    record.get("bookTitle").toString(),
                    record.get("userName").toString(),
                    record.get("dueDate").toString(),
                    record.get("overdueDays").toString()
                };
                writer.writeNext(row);
            }
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }
}
