import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static AtomicInteger clientCount = new AtomicInteger(0);
    private static Set<ClientHandler> subscribers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments!\nCommand: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                int clientNumber = clientCount.incrementAndGet();
                System.out.println("New client connected: (Client " + clientNumber+")");

                new ClientHandler(socket, clientNumber).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private int clientNumber;
        private String clientType;
        private PrintWriter out;

        public ClientHandler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
        }

        public void run() {
            try (InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String initialMessage = reader.readLine();
                if (initialMessage == null) {
                    return;
                }

                String[] parts = initialMessage.split(" ");
                if (parts.length < 2) {
                    System.out.println("Invalid client type");
                    socket.close();
                    return;
                }

                clientType = parts[1].toUpperCase();

                if (clientType.equals("SUBSCRIBER")) {
                    subscribers.add(this);
                }

                System.out.println("Client " + clientNumber + " is a " + clientType);

                String text;
                while ((text = reader.readLine()) != null) {
                    if (text.equalsIgnoreCase("terminate")) {
                        System.out.println("Client " + clientNumber + " terminated the connection");
                        break;
                    }

                    if (clientType.equals("PUBLISHER")) {
                        System.out.println("Publisher (Client " + clientNumber + ") : " + text);
                        broadcastToSubscribers("Publisher (Client " + clientNumber + ") : " + text);
                    } else {
                        System.out.println("Subscriber (Client " + clientNumber + ") : " + text);
                    }
                }
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (clientType.equals("SUBSCRIBER")) {
                    subscribers.remove(this);
                }
            }
        }

        private void broadcastToSubscribers(String message) {
            for (ClientHandler subscriber : subscribers) {
                subscriber.out.println(message);
            }
        }
    }
}
