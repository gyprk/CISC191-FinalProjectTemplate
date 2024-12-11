package edu.sdccd.cisc191;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 --ServerApp functions as the go between on LibraryData and ClientApp, moving information from the server to the library
 --Uses concepts from module 4 , Encapsulating the values within the shelves as it cant be accessed from outside the class, module 6 (i/o streams) loading and uploading data within the program, and module 6 (networking), as it communicates with an "external" server
 */
public class ServerApp {
    private static final String FILE_NAME = "GwanLibrary.txt"; //GwanLibrary.txt is the "physical" library where information is saved and uploaded.
    private final LibraryData library;

    public ServerApp() {
        library = new LibraryData();
        try {
            loadLibraryFromFile(); //looks for GwanLibrary.txt to pull from. Similar to roster.txt, but if it cannot find it willl be autopopulated and created
        } catch (IOException e) {
            System.out.println("GwanLibrary.txt Not found. Creating Default Library");
        }
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) { //creating server
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
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            LibraryData loadedLibrary = (LibraryData) in.readObject(); //keeps the information "present" so its always readily availible, was quite an issue not having it update (was a real headache to figure out)
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
            out.writeObject(library);
        }
    }

    public LibraryData getLibrary() {
        return library; //allows access to data
    }

    public static void main(String[] args) {
        ServerApp server = new ServerApp();
        server.startServer(8080); //will start on local port 8080
    }

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
                    String command = (String) in.readObject();
                    switch (command) {
                        case "VIEW_LIBRARY": //already commented on each function in ClientApp, quite straightforward commands
                            out.writeObject(server.getLibrary().displayLibrary());
                            break;
                        case "ADD_BOOK":
                            int shelfIndex = in.readInt();
                            int slotIndex = in.readInt();
                            String bookTitle = (String) in.readObject();
                            server.getLibrary().addBook(shelfIndex, slotIndex, bookTitle);
                            server.saveLibraryToFile();
                            break;
                        case "ADD_SHELF":
                            String shelfName = (String) in.readObject();
                            server.getLibrary().addShelf(shelfName);
                            server.saveLibraryToFile();
                            break;
                        case "REMOVE_SHELF":
                            int shelfToRemove = in.readInt();
                            server.getLibrary().removeShelf(shelfToRemove);
                            server.saveLibraryToFile();
                            break;
                        case "REMOVE_BOOK":
                            int removeShelf = in.readInt();
                            int removeSlot = in.readInt();
                            server.getLibrary().removeBook(removeShelf, removeSlot);
                            server.saveLibraryToFile();
                            break;
                        case "RENAME_SHELF":
                            int shelfToRename = in.readInt();
                            String newShelfName = (String) in.readObject();
                            server.getLibrary().renameShelf(shelfToRename, newShelfName);
                            server.saveLibraryToFile();
                            break;
                        case "SAVE_LIBRARY":
                            server.saveLibraryToFile();
                            out.writeObject("Library has been saved.");
                            break;
                        case "EXIT":
                            return;
                        default:
                            out.writeObject("Invalid command.");
                    }
                    out.flush();
                }
            } catch (Exception e) {
                System.err.println("Client disconnected or an error has caused it to crash: " + e.getMessage());
            }
        }
    }
}
