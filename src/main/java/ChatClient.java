import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Étape 3 - Client de chat UDP bidirectionnel.
 * - Le thread principal lit le clavier et envoie les messages.
 * - Un second thread reçoit et affiche en continu les messages du serveur.
 * Les deux threads partagent le même DatagramSocket (donc le même port local),
 * ce qui permet au serveur de renvoyer les messages au bon endroit.
 */
public class ChatClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = scanner.nextLine().trim();

            // Thread de réception
            Thread receiver = new Thread(() -> {
                byte[] buffer = new byte[BUFFER_SIZE];
                try {
                    while (!socket.isClosed()) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(
                                packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                        System.out.println(message);
                    }
                } catch (SocketException e) {
                    // Socket fermé à la sortie du client : arrêt normal
                } catch (Exception e) {
                    System.err.println("Erreur réception : " + e.getMessage());
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            // Message d'arrivée : enregistre le client auprès du serveur
            send(socket, serverAddress, "[" + username + "] a rejoint le chat");

            System.out.println("Connecté. Tapez vos messages (\"quit\" pour quitter).");

            // Boucle d'envoi
            while (true) {
                String text = scanner.nextLine();
                if (text.equalsIgnoreCase("quit")) {
                    send(socket, serverAddress, "[" + username + "] a quitté le chat");
                    break;
                }
                send(socket, serverAddress, "[" + username + "] : " + text);
            }
            System.out.println("Client fermé.");
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void send(DatagramSocket socket, InetAddress address, String message)
            throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(data, data.length, address, SERVER_PORT));
    }
}