package servers;

import java.io.*;
import java.net.*;
import server_protos.*;

class BaseServer {
    private final int serverPort;

    public BaseServer(int port) {
        this.serverPort = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server started on port: " + serverPort);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    InputStream inputStream = clientSocket.getInputStream();
                    OutputStream outputStream = clientSocket.getOutputStream();

                    byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead > 0) {
                        CommunicationMessage.RequestType requestType = CommunicationMessage.RequestType.values()[buffer[0]];
                        handleRequest(requestType, outputStream);
                    }
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
        }
    }

    private void handleRequest(CommunicationMessage.RequestType requestType, OutputStream outputStream) throws IOException {
        switch (requestType) {
            case INITIALIZE:
                System.out.println("Initialization request received.");
                sendResponse(outputStream, CommunicationMessage.ServerResponse.ACCEPTED);
                break;
            case CHECK_CAPACITY:
                System.out.println("Capacity check request received.");
                sendResponse(outputStream, CommunicationMessage.ServerResponse.ACCEPTED);
                break;
            default:
                System.out.println("Unknown request type.");
                sendResponse(outputStream, CommunicationMessage.ServerResponse.DECLINED);
                break;
        }
    }

    private void sendResponse(OutputStream outputStream, CommunicationMessage.ServerResponse response) throws IOException {
        outputStream.write(response.ordinal());
        outputStream.flush();
    }
}

public class Server1 {
    public static void main(String[] args) {
        BaseServer server = new BaseServer(5001);
        server.startServer();
    }
}
