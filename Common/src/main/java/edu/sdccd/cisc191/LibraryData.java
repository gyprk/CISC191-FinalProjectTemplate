package edu.sdccd.cisc191;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 --LibraryData serves as the core data structure for the library system, holding and managing shelves and books.
 --Applies concepts from:
 - Module 1: Arrays are used to represent books on each shelf.
 - Module 5: I/O Streams to serialize and deserialize library data.
 - Module 8: Uses linkedlists for shelves to facilitate dynamic addition and removal.
 - Module 9: Searching and Sorting methods for efficient book and shelf management.
 */
public class LibraryData implements Serializable {
    private final List<String[]> shelves; //LinkedList to manage shelf
    private final List<String> shelfNames;

    public LibraryData() {
        shelves = new LinkedList<>();
        shelfNames = new LinkedList<>();
        for (int i = 0; i < 5; i++) { //default library format with 5 shelves
            shelves.add(new String[]{"Empty", "Empty", "Empty", "Empty"});
            shelfNames.add("Shelf " + (i + 1));
        }
    }

    public List<String[]> getShelves() {
        return shelves; //accesses shelf data
    }

    public List<String> getShelfNames() {
        return shelfNames; //accesses for shelf name
    }

    public void addShelf(String shelfName) {
        shelves.add(new String[]{"Empty", "Empty", "Empty", "Empty"}); //creates new shelf
        shelfNames.add(shelfName); //gives name to shelf
    }

    public void removeShelf(int shelfIndex) {
        shelves.remove(shelfIndex); //removes shelf
        shelfNames.remove(shelfIndex); //deletes name shelf
    }

    public void addBook(int shelfIndex, int slotIndex, String bookTitle) {
        shelves.get(shelfIndex)[slotIndex] = bookTitle; //puts book in shelf and slot
    }

    public void removeBook(int shelfIndex, int slotIndex) {
        shelves.get(shelfIndex)[slotIndex] = "Empty"; //replaces book name with empty
    }

    public void renameShelf(int shelfIndex, String newName) {
        shelfNames.set(shelfIndex, newName);
    }

    public String searchBook(String bookTitle) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < shelves.size(); i++) {
            String[] shelf = shelves.get(i);
            for (int j = 0; j < shelf.length; j++) {
                if (bookTitle.equals(shelf[j])) { //checks title name
                    result.append("Found '")
                            .append(bookTitle)
                            .append("' on shelf '")
                            .append(shelfNames.get(i))
                            .append("' at slot ")
                            .append(j + 1)
                            .append(".\n");
                }
            }
        }
        return result.length() > 0 ? result.toString() : "Book not found."; //if it cant find book will prompt
    }

    public String displayLibrary() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shelves.size(); i++) {
            sb.append(i + 1).append(": ").append(shelfNames.get(i)).append(": ");
            for (String slot : shelves.get(i)) {
                sb.append(slot).append(" | ");
            }
            sb.append("\n"); //create line for shelf
        }
        return sb.toString();
    }

    public void sortShelvesAlphabetically() {
        shelfNames.sort(String::compareToIgnoreCase); //sort shelfs (not case sensitive)
    }

    public void sortBooksInShelf(int shelfIndex) {
        String[] books = shelves.get(shelfIndex);
        List<String> bookList = new ArrayList<>();
        for (String book : books) {
            if (!"Empty".equals(book)) {
                bookList.add(book);
            }
        }
        bookList.sort(String::compareToIgnoreCase);
        for (int i = 0; i < books.length; i++) {
            books[i] = i < bookList.size() ? bookList.get(i) : "Empty"; //update shelf with sorted books
        }
    }
}
