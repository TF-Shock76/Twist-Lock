import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

/**
 * Classe Client.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class Client {

    /**
     * l'adresse du client
     * @see SocketAddress
     */
    private SocketAddress address;

    /**
     * le serveur
     * @see Server
     */
    private Server server;

    /**
     * Constructeur
     * @param address
     * @param server
     */
    public Client(SocketAddress address, Server server){
        this.address = address;
        this.server  = server;
    }

    /**
     * Méthode permettant l'envoi d'un message
     * @param message message à envoyer
     */
    void send(String message){
        try{
            byte[] buf = message.getBytes("UTF-8");
            DatagramPacket dataToSend = new DatagramPacket(buf, buf.length, this.address);
            server.getDatagramSocket().send(dataToSend);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant d'obtenir la SocketAdress
     * @return SocketAddress
     */
    public SocketAddress getAddress() {
        return address;
    }
}