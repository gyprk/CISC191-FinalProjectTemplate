package edu.sdccd.cisc191;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 --ClientApp functions as the "client" sided part of the program functioning as the interface for individuals to interact with to modify the Library.
 --It communicates with ServerApp to perform operations like adding, removing, viewing, and managing shelves and books.
 --Pulls concepts from Module 6, Networking, and Module 3, utilizing the interactive terminal console menu.
 */
public class ClientApp {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080); // Establish connection to ServerApp using local port 8080
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);

            while (true) {
                //the menu (module 1 and 3)
                System.out.println("1. View Library");
                System.out.println("2. Add Book on Shelf");
                System.out.println("3. Add Shelf");
                System.out.println("4. Remove Specific Shelf");
                System.out.println("5. Remove Book from Shelf");
                System.out.println("6. Rename Shelf");
                System.out.println("7. Search Book");
                System.out.println("8. Save to File");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();//provides menu options
                scanner.nextLine();


                switch (choice) {
                    case 1: //views the library
                        out.writeObject("VIEW_LIBRARY");
                        System.out.println((String) in.readObject());
                        break;

                    case 2: //adds book
                        out.writeObject("ADD_BOOK");
                        System.out.print("Enter shelf number: ");
                        int shelfIndex = scanner.nextInt() - 1; //to have a 0-based index rather that 0,1,2,3, it goes 1,2,3,4
                        System.out.print("Enter slot number: ");
                        int slotIndex = scanner.nextInt() - 1;
                        scanner.nextLine();
                        System.out.print("Enter book title: ");
                        String bookTitle = scanner.nextLine(); //allows for the input of book title
                        out.writeInt(shelfIndex);
                        out.writeInt(slotIndex);
                        out.writeObject("\"" + bookTitle + "\"");
                        System.out.println("Book added successfully.");
                        break;

                    case 3: //adding a new shelf to the end of list
                        out.writeObject("ADD_SHELF");
                        System.out.print("Enter new shelf name: ");
                        String newShelfName = scanner.nextLine();
                        out.writeObject(newShelfName);
                        System.out.println("Shelf added successfully.");
                        break;

                    case 4: //deletes a shelf
                        out.writeObject("REMOVE_SHELF");
                        System.out.print("Enter shelf number to remove: ");
                        int removeShelf = scanner.nextInt() - 1;
                        out.writeInt(removeShelf);
                        System.out.println("Shelf removed successfully.");
                        break;

                    case 5: //removes book from specific shelf, replacing the text with | Empty |
                        out.writeObject("REMOVE_BOOK");
                        System.out.print("Enter shelf number: ");
                        int removeBookShelf = scanner.nextInt() - 1;
                        System.out.print("Enter slot number: ");
                        int removeSlot = scanner.nextInt() - 1;
                        out.writeInt(removeBookShelf);
                        out.writeInt(removeSlot);
                        System.out.println("Book removed successfully.");
                        break;

                    case 6: //renames shelf
                        out.writeObject("RENAME_SHELF");
                        System.out.print("Enter shelf number: ");
                        int shelfToRename = scanner.nextInt() - 1;
                        scanner.nextLine();
                        System.out.print("Enter new shelf name: ");
                        String renameShelf = scanner.nextLine();
                        out.writeInt(shelfToRename);
                        out.writeObject(renameShelf);
                        System.out.println("Shelf renamed successfully.");
                        break;

                    case 7: //search function
                        out.writeObject("SEARCH_BOOK");
                        System.out.print("Enter book title to search: ");
                        String searchBook = scanner.nextLine();
                        out.writeObject("\"" + searchBook + "\"");
                        System.out.println((String) in.readObject());
                        break;

                    case 8: //updates and saves to the GwanLibrary.txt
                        out.writeObject("SAVE_LIBRARY");
                        System.out.println((String) in.readObject());
                        break;

                    case 9: //exits...
                        out.writeObject("EXIT");
                        System.out.println("Exiting...");
                        return;

                    default: //upon invalid input, will prompt again
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
