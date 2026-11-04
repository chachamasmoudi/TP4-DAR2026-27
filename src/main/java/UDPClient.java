import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Étape 2 - Client UDP unidirectionnel.
 * Demande un nom d'utilisateur, puis envoie au serveur chaque ligne saisie
 * sous la forme "[NomUtilisateur] : message". Tapez "quit" pour sortir.
 */
public class UDPClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = scanner.nextLine().trim();

            System.out.println("Connecté. Tapez vos messages (\"quit\" pour quitter).");

            while (true) {
                String text = scanner.nextLine();
                if (text.equalsIgnoreCase("quit")) {
                    break;
                }

                String message = "[" + username + "] : " + text;
                byte[] data = message.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(
                        data, data.length, serverAddress, SERVER_PORT);
                socket.send(packet);
            }
            System.out.println("Client fermé.");
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}