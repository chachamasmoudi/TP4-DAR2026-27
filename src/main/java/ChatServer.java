import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Étape 3 - Serveur de chat UDP avec diffusion.
 * - Écoute sur le port 1234.
 * - Mémorise l'adresse de chaque client qui envoie un message.
 * - Retransmet chaque message à tous les autres clients (pas à l'expéditeur).
 */
public class ChatServer {

    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    // Liste des clients "connectés" (sans doublons, ordre d'arrivée conservé)
    private static final Set<SocketAddress> clients = new LinkedHashSet<>();

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.bind(new InetSocketAddress(PORT));
            System.out.println("Serveur de chat UDP en écoute sur le port " + PORT + "...");

            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                SocketAddress sender = packet.getSocketAddress();
                String message = new String(
                        packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                // 1. Enregistrer l'expéditeur s'il est nouveau
                if (clients.add(sender)) {
                    System.out.println("Nouveau client enregistré : " + sender
                            + " (total : " + clients.size() + ")");
                }

                System.out.println("De " + sender + " -> " + message);

                // 2. Diffuser à tous les autres clients
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                for (SocketAddress client : clients) {
                    if (!client.equals(sender)) {
                        socket.send(new DatagramPacket(data, data.length, client));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}