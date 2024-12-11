package edu.sdccd.cisc191;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 --ClientApp functions as the "client" sided part of the program functioning as the interface for individuals to interact with to modify the Library.
 --It communicates with ServerApp to perform operations like adding, removing, viewing, and managing shelves and books.
 --Pulls concepts from Module 6, Networking, and Module 3, utilizing the Interactive terminal console menu
 */
public class ClientApp {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080); //command to connect to local server, in this case uses localport 8080
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);

            while (true) {
                //displays all the functions availible, shows prompt upon finishing command
                System.out.println("1. View Library");
                System.out.println("2. Add Book on Shelf");
                System.out.println("3. Add Shelf");
                System.out.println("4. Remove Specific Shelf");
                System.out.println("5. Remove Book from Shelf");
                System.out.println("6. Rename Shelf");
                System.out.println("7. Save to File");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                //module 3's Interactive terminal menu, displays all the options and their function
                switch (choice) {
                    case 1: //to view the library, all the books and shelfs
                        out.writeObject("VIEW_LIBRARY");
                        System.out.println((String) in.readObject());
                        break;
                    case 2: //function to "add book", kept it straightforward so the prompt will function as a 1 - 1
                        out.writeObject("ADD_BOOK");
                        System.out.print("Enter shelf number: ");
                        int shelfIndex = scanner.nextInt() - 1; //Was having issues with it counting from 0-4 instead of 1-5
                        System.out.print("Enter slot number: ");
                        int slotIndex = scanner.nextInt() - 1;
                        scanner.nextLine();
                        System.out.print("Enter book title: ");
                        String bookTitle = scanner.nextLine(); //title of the book to add, allows for spaces, special characters, and numbers without error (thus far)
                        out.writeInt(shelfIndex);
                        out.writeInt(slotIndex);
                        out.writeObject(bookTitle);
                        System.out.println("Book added successfully.");
                        break;
                    case 3: //lets one add a shelf
                        out.writeObject("ADD_SHELF");
                        System.out.print("Enter new shelf name: ");
                        String newShelfName = scanner.nextLine();
                        out.writeObject(newShelfName);
                        System.out.println("Shelf added successfully.");
                        break;
                    case 4: //removes a shelf
                        out.writeObject("REMOVE_SHELF");
                        System.out.print("Enter shelf number to remove: ");
                        int removeShelf = scanner.nextInt() - 1; //similar to the book would often run from a 0-4 instead of 1-5, wanted to not confuse
                        out.writeInt(removeShelf);
                        System.out.println("Shelf removed successfully.");
                        break;
                    case 5: //deletes book, the format is if 1 | 2 | 3 | 4 |, and you decide to delete book 2 it will output 1 | Empty | 3 | 4
                        out.writeObject("REMOVE_BOOK");
                        System.out.print("Enter shelf number: ");
                        int removeBookShelf = scanner.nextInt() - 1;
                        System.out.print("Enter slot number: ");
                        int removeSlot = scanner.nextInt() - 1;
                        out.writeInt(removeBookShelf);
                        out.writeInt(removeSlot);
                        System.out.println("Book removed successfully.");
                        break;
                    case 6: //change the name of shelf
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
                    case 7: //Saves the library so when you pull GwanLibrary.txt again it will pull up the previous changes with no issue
                        out.writeObject("SAVE_LIBRARY");
                        System.out.println((String) in.readObject()); //sometimes had issues being unable to tell if it saved, so addded text to notify when saved (believe it or not it was a real issue, as sometimes when running the command it wouldn't save unless I opened and closed to check.)
                        break;
                    case 8: //To quit the program
                        out.writeObject("EXIT");
                        System.out.println("Exiting...");
                        return;
                    default: // Handle invalid input
                        System.out.println("Invalid choice. Please try again from the alloted options.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
