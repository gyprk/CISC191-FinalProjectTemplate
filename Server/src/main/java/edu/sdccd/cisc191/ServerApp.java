package edu.sdccd.cisc191;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 --ServerApp functions as the go-between for LibraryData and ClientApp, facilitating communication and managing updates to the library.
 --Uses concepts from Module 4 (Encapsulation), Module 5 (I/O Streams), and Module 6 (Networking), Module 11 (Concurrecny), Module 12 (Searching)
 --Encapsulates library data and provides secure access via defined methods.
 --Handles file operations (loading and saving) for persistent data storage.
 --Manages a server that listens for client requests and executes commands.
 */
public class ServerApp {
    private static final String FILE_NAME = "GwanLibrary.txt";
    private final LibraryData library;

    public ServerApp() {
        library = new LibraryData(); //When no GwanLibrary.txt when it will create with default
        try {
            loadLibraryFromFile(); //tries to pull if it detects a GwanLibrary.txt
        } catch (IOException e) {
            System.out.println("GwanLibrary.txt not found. Creating Default Library.");
        }
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) { //creating a local server, in this case port 8080
            System.out.println("Server running on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private void loadLibraryFromFile() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; //if it does not exist begin with default
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            LibraryData loadedLibrary = (LibraryData) in.readObject(); //deserialize library data
            library.getShelves().clear();
            library.getShelves().addAll(loadedLibrary.getShelves());
            library.getShelfNames().clear();
            library.getShelfNames().addAll(loadedLibrary.getShelfNames());
        } catch (ClassNotFoundException e) {
            throw new IOException("Error loading library data.");
        }
    }

    public void saveLibraryToFile() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(library); //serialize and save the library data to a file
        }
    }

    public LibraryData getLibrary() {
        return library; //access library data
    }

    public static void main(String[] args) {
        ServerApp server = new ServerApp();
        server.startServer(8080); //start the server on local port 8080
    }

    /**
     --ClientHandler handles communication between a single client and the server.
     --Executes commands sent by the client, updating the library as needed and sending responses back.
     */
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ServerApp server;

        public ClientHandler(Socket clientSocket, ServerApp server) {
            this.clientSocket = clientSocket;
            this.server = server;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                while (true) {
                    String command = (String) in.readObject(); //read the command from the client
                    switch (command) {
                        case "VIEW_LIBRARY": //sends the current state of the library to the client
                            out.writeObject(server.getLibrary().displayLibrary());
                            break;

                        case "ADD_BOOK": //adds a book to the library at the specific shelf and slot
                            int shelfIndex = in.readInt();
                            int slotIndex = in.readInt();
                            String bookTitle = (String) in.readObject();
                            server.getLibrary().addBook(shelfIndex, slotIndex, bookTitle);
                            server.saveLibraryToFile();
                            break;

                        case "ADD_SHELF": //adds a new shelf to the library
                            String shelfName = (String) in.readObject();
                            server.getLibrary().addShelf(shelfName);
                            server.saveLibraryToFile();
                            break;

                        case "REMOVE_SHELF": //removes a specific shelf from the library
                            int shelfToRemove = in.readInt();
                            server.getLibrary().removeShelf(shelfToRemove);
                            server.saveLibraryToFile();
                            break;

                        case "REMOVE_BOOK": //removes a book from a specific shelf and slot
                            int removeShelf = in.readInt();
                            int removeSlot = in.readInt();
                            server.getLibrary().removeBook(removeShelf, removeSlot);
                            server.saveLibraryToFile();
                            break;

                        case "RENAME_SHELF": //renames a specified shelf
                            int shelfToRename = in.readInt();
                            String newShelfName = (String) in.readObject();
                            server.getLibrary().renameShelf(shelfToRename, newShelfName);
                            server.saveLibraryToFile();
                            break;

                        case "SEARCH_BOOK": //searches for a book by title and returns its location with shelf and slot
                            String searchTitle = (String) in.readObject();
                            out.writeObject(server.getLibrary().searchBook(searchTitle));
                            break;

                        case "SAVE_LIBRARY": //saves the library to the persistent file Gwanlibrary.txt
                            server.saveLibraryToFile();
                            out.writeObject("Library has been saved.");
                            break;

                        case "EXIT": //exits
                            return;

                        default:
                            out.writeObject("Invalid command.");
                    }
                    out.flush(); //ensures all output is sent to the client
                }
            } catch (Exception e) {
                System.err.println("Client disconnected or an error occurred: " + e.getMessage());
            }
        }
    }
}
