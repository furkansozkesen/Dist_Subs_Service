import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.google.protobuf.*;

public class Client1 {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5002;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("Sunucuya bağlandı: " + SERVER_HOST + ":" + SERVER_PORT);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            System.out.println("Abone ol (A), Aboneliği iptal et (B):");
            Scanner getUserInput = new Scanner(System.in);
            String userInput = getUserInput.nextLine();

            if (userInput.equalsIgnoreCase("A")) {
                // Abone olma talebi oluştur
                SubscriberOuterClass.Subscriber subscriber = SubscriberOuterClass.Subscriber.newBuilder()
                        .setID(2)
                        .setNameSurname("Ali")
                        .setDemand(SubscriberOuterClass.Subscriber.Demand.SUBS)
                        .build();
                subscriber.writeTo(output);
                output.flush();
                System.out.println("Abone olma talebi gönderildi: " + subscriber);
            } else if (userInput.equalsIgnoreCase("B")) {
                // Abonelik iptali talebi oluştur
                SubscriberOuterClass.Subscriber unsubscribeRequest = SubscriberOuterClass.Subscriber.newBuilder()
                        .setID(2)
                        .setDemand(SubscriberOuterClass.Subscriber.Demand.DEL)
                        .build();
                unsubscribeRequest.writeTo(output);
                output.flush();
                System.out.println("Abonelik iptal talebi gönderildi: " + unsubscribeRequest);
            } else {
                System.out.println("Geçersiz giriş. Lütfen 'A' veya 'B' giriniz.");
            }
        } catch (IOException e) {
            System.out.println("Sunucuya bağlanılamadı: " + SERVER_HOST + ":" + SERVER_PORT);
            e.printStackTrace();
        }
    }
}
