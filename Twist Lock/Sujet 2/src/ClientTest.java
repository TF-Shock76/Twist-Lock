import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Classe ClientTest.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class ClientTest
{
    /*
    * le datagramSocket
    */
    private DatagramSocket ds;

    /*
    * Constructeur
    */
    private ClientTest() throws IOException
    {
        ds = new DatagramSocket();
        Scanner sc = new Scanner(System.in);

        String adresse;
        // on demande l'adresse du serveur 
        do{
            System.out.println("Adresse du serveur : ");
            adresse = sc.next();
        }while(adresse.equals(""));

        int port = 0;
        System.out.println("Port : ");
        // on demande au client de saisir un numero de port correct, on répète la situation autant de fois qu'il le faut
        do {
            try {
                port = sc.nextInt();
            } catch (Exception e) {
                System.out.println("saisir un numero de port correct");
            }
        } while (port == 0);

        // on créé un nouveau thread
        new Thread(() -> {
            // on attend en continu de recevoir des messages
            do {
                DatagramPacket msg = new DatagramPacket(new byte[1024], 1024);
                // on reçois un message
                try {
                    ds.receive(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // on convertie le datagramPacket reçu en string
                try{
                     String message = new String(msg.getData(), "UTF-8").trim();
                System.out.println("\n" + message);
                }
                catch(Exception e){ e.printStackTrace(); }
               
            } while (ds != null);

        }).start();

        System.out.println("\nVotre nom d'equipe : ");

        // on attend que le client rentre un nom d'équipe valide
        do{
            String msg = sc.next();
            // on s'en moque de la case
            if(msg.equalsIgnoreCase("q"))
                ds = null;
            else{
                DatagramPacket dataToSend = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(adresse), port);
                ds.send(dataToSend);
            }
        }while(ds != null);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        new ClientTest();
    }
}