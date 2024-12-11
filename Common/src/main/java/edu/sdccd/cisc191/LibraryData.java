package edu.sdccd.cisc191;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 --LibraryData functions as the brains of the entire program, handling and containing all the information that passes from ClientApp to ServerApp
 --Uses concepts from Module 1, in having an array to store information, Module 5's, I/O streams, and Module 8's data structuring
 */
public class LibraryData implements Serializable { // Implements Serializable for file storage
    private final List<String[]> shelves; // List of shelves, each represented by an array of books
    private final List<String> shelfNames; // Names of each shelf

    public LibraryData() {
        shelves = new ArrayList<>(); // Initialize shelves list
        shelfNames = new ArrayList<>(); // Initialize shelf names list
        for (int i = 0; i < 5; i++) {
            shelves.add(new String[]{"Empty", "Empty", "Empty", "Empty"}); // Add default shelves
            shelfNames.add("Shelf " + (i + 1)); // Assign default names
        }
    }

    public List<String[]> getShelves() {
        return shelves; // Accessor for shelf data
    }

    public List<String> getShelfNames() {
        return shelfNames; // Accessor for shelf names
    }

    public void addShelf(String shelfName) {
        shelves.add(new String[]{"Empty", "Empty", "Empty", "Empty"}); //the default state of an empty library, generates when GwanLibrary.txt is not detected.
        shelfNames.add(shelfName); //adding name of shelf
    }

    public void removeShelf(int shelfIndex) {
        shelves.remove(shelfIndex); //remove shelf function
        shelfNames.remove(shelfIndex);
    }

    public void addBook(int shelfIndex, int slotIndex, String bookTitle) {
        shelves.get(shelfIndex)[slotIndex] = bookTitle; //add book logic
    }

    public void removeBook(int shelfIndex, int slotIndex) {
        shelves.get(shelfIndex)[slotIndex] = "Empty"; //upon deletion defaults book to "Empty"
    }

    public void renameShelf(int shelfIndex, String newName) {
        shelfNames.set(shelfIndex, newName);
    }

    public String displayLibrary() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shelves.size(); i++) {
            sb.append(i + 1).append(": ").append(shelfNames.get(i)).append(": ");
            for (String slot : shelves.get(i)) {
                sb.append(slot).append(" | ");
            }
            sb.append("\n"); //when creating a new shelf, it will create a newline
        }
        return sb.toString();
    }
}
