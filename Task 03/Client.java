import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Invalid number of arguments!\nCommand: java Client <server-ip> <port> <PUBLISHER|SUBSCRIBER> <TOPIC>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String clientType = args[2].toUpperCase();
        String topic = args[3].toUpperCase();

        if (!clientType.equals("PUBLISHER") && !clientType.equals("SUBSCRIBER")) {
            System.out.println("Invalid client type. Use PUBLISHER or SUBSCRIBER.");
            return;
        }

        try (Socket socket = new Socket(hostname, port);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            writer.println("Client " + clientType + " " + topic);

            if (clientType.equals("PUBLISHER")) {
                String text;
                do {
                    System.out.print("Enter message: ");
                    text = consoleReader.readLine();
                    writer.println(text);
                } while (!text.equalsIgnoreCase("terminate"));
            } else {
                Thread readerThread = new Thread(() -> {
                    try {
                        String serverMessage;
                        while ((serverMessage = reader.readLine()) != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException ex) {
                        System.out.println("Client exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                readerThread.start();

                while (true) {
                    String text = consoleReader.readLine();
                    if (text.equalsIgnoreCase("terminate")) {
                        writer.println(text);
                        break;
                    }
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
