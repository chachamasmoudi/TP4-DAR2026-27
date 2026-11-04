import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Étape 2 - Serveur UDP unidirectionnel.
 * Écoute sur le port 1234 et affiche chaque message reçu
 * avec l'adresse (IP:port) de l'expéditeur.
 */
public class UDPServer {

    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(null)) {
            // Liaison explicite avec InetSocketAddress
            socket.bind(new InetSocketAddress(PORT));
            System.out.println("Serveur UDP en écoute sur le port " + PORT + "...");

            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // appel bloquant

                String message = new String(
                        packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                System.out.println("De " + packet.getAddress().getHostAddress()
                        + ":" + packet.getPort() + " -> " + message);
            }
        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}