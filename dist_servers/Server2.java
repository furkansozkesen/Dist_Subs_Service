import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.protobuf.*;

public class Server2 {
    private static final int SERVER_PORT = 5002;
    private static final int OTHER_SERVER_PORT_1 = 5001;
    private static final int OTHER_SERVER_PORT_2 = 5003;
    private static final String ADMIN_HOST = "localhost";
    private static final int ADMIN_PORT = 5000;

    private static final Map<Integer, String> subscriberBackup = new ConcurrentHashMap<>();
    private static final int faultToleranceLevel = 2;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server2 running on port: " + SERVER_PORT);

            // Thread: Client'tan gelen abonelik bilgilerini al
            new Thread(Server2::acceptClients).start();

            // Thread: Diğer serverlardan gelen abonelik bilgilerini al
            new Thread(Server2::receiveUpdatesFromOtherServers).start();

            // Thread: Admin'e kapasite bilgisi gönder
            new Thread(Server2::sendCapacityPeriodically).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            } catch (IOException e) {
                System.err.println("Hata: Client bağlantısı kabul edilemedi.");
                e.printStackTrace();
            }
        }
    }

    private static void receiveUpdatesFromOtherServers() {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT + 1000)) { // Dinleme portu
                while (true) {
                    try (Socket socket = serverSocket.accept();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",", 2);
                            if (parts.length == 2) {
                                int id = Integer.parseInt(parts[0]);
                                String name = parts[1];
                                subscriberBackup.put(id, name);
                                System.out.println("Subscriber received from another server: " + id + " - " + name);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error receiving updates from other servers: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {

            SubscriberOuterClass.Subscriber subscriber = SubscriberOuterClass.Subscriber.parseFrom(input);

            if (subscriber.getDemand() == SubscriberOuterClass.Subscriber.Demand.SUBS) {
                subscriberBackup.put(subscriber.getID(), subscriber.getNameSurname());
                System.out.println("Subscriber added: " + subscriber);
                sendToOtherServers(subscriber);

            } else if (subscriber.getDemand() == SubscriberOuterClass.Subscriber.Demand.DEL) {
                subscriberBackup.remove(subscriber.getID());
                System.out.println("Subscriber removed: " + subscriber);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendToOtherServers(SubscriberOuterClass.Subscriber subscriber) {
        String message = subscriber.getID() + "," + subscriber.getNameSurname();

        if (faultToleranceLevel >= 1) {
            sendStringToServer(OTHER_SERVER_PORT_1 + 1000, message);
        }
        if (faultToleranceLevel == 2) {
            sendStringToServer(OTHER_SERVER_PORT_2 + 1000, message);
        }
    }

    private static void sendStringToServer(int port, String message) {
        try (Socket socket = new Socket("localhost", port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            writer.println(message);

        } catch (IOException e) {
            System.err.println("Failed to send string to server on port: " + port);
            e.printStackTrace();
        }
    }

    private static void sendCapacityPeriodically() {
        while (true) {
            try {
                sendCapacityToAdmin();
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                System.err.println("Hata: sendCapacityPeriodically kesildi.");
                e.printStackTrace();
            }
        }
    }

    private static void sendCapacityToAdmin() {
        try (Socket socket = new Socket(ADMIN_HOST, 6002);
             OutputStream output = socket.getOutputStream()) {

            CapacityOuterClass.Capacity capacity = CapacityOuterClass.Capacity.newBuilder()
                    .setSubscriberCount(subscriberBackup.size())
                    .build();

            capacity.writeTo(output);
            output.flush();

            System.out.println("Capacity sent to admin: " + subscriberBackup.size());

        } catch (IOException e) {
            System.err.println("Failed to send capacity to admin.");
            e.printStackTrace();
        }
    }
}
