package clients;

import java.io.*;
import java.net.*;
import client_protos.SubscriberOuterClass;

public class ClientHandler {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5050;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connected to the server: " + SERVER_ADDRESS + ":" + SERVER_PORT);
            
            SubscriberOuterClass.Subscriber newSubscriber = SubscriberOuterClass.Subscriber.newBuilder()
                .setSubscriberId(1001)
                .setFullName("Alice Johnson")
                .setJoinDate(System.currentTimeMillis() / 1000L)
                .setLastActiveDate(System.currentTimeMillis() / 1000L)
                .addHobbies("Reading")
                .addHobbies("Gaming")
                .setIsActive(true)
                .setRequestType(SubscriberOuterClass.Subscriber.RequestType.REGISTRATION)
                .build();

            OutputStream outputStream = socket.getOutputStream();
            newSubscriber.writeTo(outputStream);
            outputStream.flush();
            System.out.println("Sent subscription request: " + newSubscriber);

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String response = reader.readLine();
            System.out.println("Server Response: " + response);

        } catch (IOException e) {
            System.err.println("Error communicating with the server: " + e.getMessage());
        }
    }
}
