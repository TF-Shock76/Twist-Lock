import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Classe Server.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 2.0
 */

public class Server extends Thread{
    /**
     * le numero de port
     */
    private int port;

    /**
     * le nombre de client
     */
    private int nbClient;

    /**
     * le tableau de nom
     * @see String
     */
    private String[] tabNoms;

    /**
     * le datagramSoket
     * @see DatagramSocket
     */
    private DatagramSocket datagramSocket;

    /**
     * la liste des clients
     * @see Client
     */
    private ArrayList<Client> listClients;

    /**
     * savoir si le jeu commence ou non 
     */
    private boolean commence;

    /**
     * un message
     */
    private String msg;

    /**
     * Le tableau de message avec tous les messages 
     * @see String
     */
    private final static String[] TAB_MESSAGES = { "01-la partie va commencer\nMAP=",
                                                   "10-A vous de jouer"             ,
                                                   "20:coup adversaire:"            ,
                                                   "21:coup joué illégal"           ,
                                                   "22:coup adversaire illegal"     ,
                                                   "50-Vous ne pouvez plus jouer"   ,
                                                   "88-Partie Terminée,"            ,
                                                   "91-demande non valide"             };

    /**
     * Partie jeu
     * @see Jeu
     */
    private Jeu jeu;

    /**
     * Constructeur
     * @param port le port du serveur
     * @param nbClient le nombre de client
     */
    public Server(int port, int nbClient){
        this.port     = port;
        this.nbClient = nbClient;
        listClients   = new ArrayList<>();
        
        this.commence =  false;
        this.start();
    }

    /**
     * Méthode pour récupérer un client
     * @param sa adresse 
     * @return le client ayant sa comme address
     */
    private Client getClient(SocketAddress sa){
        // Si l'adresse du client est identique à sa alors on retourne le client
        for (Client c : listClients) 
            if(c.getAddress().equals(sa)) 
                return c;
        return null;
    }

    /**
     * Récupère l'indice du client en fonction de son SocketAddress
     * @param sa adresse
     * @return l'index du client
     */
    private int getClientIndex(SocketAddress sa){
        int i = 0;
        for (Client c : listClients)
            // on vérifie si l'adresse est identique 
            if(c.getAddress().equals(sa)) 
                return i;
            else
                i++;
        return -1;
    }

    /**
     * Méthode lançant les actions à effectuer
     */
    @Override
    public void run() {
        super.run();
        this.tabNoms = new String[nbClient];
        Client tmp;

        //Initialisation du DatagramSocket
        try{
            this.datagramSocket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // On attend un packet
        while (true) {
            // on créé un nouveau Packet
            DatagramPacket packet = new DatagramPacket(new byte[1024],1024);
            System.out.println("En attente...");
            try{
                // réception packet
                this.datagramSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //on a recu un packet

            Client client = getClient(packet.getSocketAddress());
            try{
                this.msg = new String(packet.getData()).trim();
                System.out.println("Message reçu par le serveur : " + this.msg);
            }
            catch(Exception e) {e.printStackTrace();}
            

            // On vérifie si c'est un nouveau client
            if(client == null) {
                // on vérifie si listClients est inférieure à nbClient donc qu'il reste de la place
                if(listClients.size() < nbClient) {
                    Client c = new Client(packet.getSocketAddress(), this);
                    listClients.add(c);
                    c.send("Bonjour equipe : " + msg);
                    tabNoms[listClients.indexOf(c)] = msg;
                } 
                // On vérifie si la longueur de listCLients est égale à 2.
                // Si oui on envoie le message n°91 au nouveau client qui essaie de se 
                // connecter pour lui annoncer qu'il ne peut pas
                else if(listClients.size() == 2) {
                    tmp = new Client(packet.getSocketAddress(), this);
                    tmp.send(getMessage("91"));
                }
                // on vérifie si la longueur de listClients est égale à 2 et
                // que le jeu n'a pas commencé
                if(listClients.size() == 2 && !commence) {
                    this.jeu = new Jeu(this, this.tabNoms); // on initialise donc le jeu
                    commence = true;

                    
                    continue;
                }
            }
            
            // si le jeu commence on execute le tour du joueur
            else if(commence){// && jeu.getJoueurIndex() == getClientIndex(packet.getSocketAddress()))S
                this.jeu.executeTurn(getClientIndex(packet.getSocketAddress()), this.msg);

            }

            this.msg = ""; //On efface le message
                  
            
        }

    }

    /**
     * @return le dernier message envoyé par le client
     */
    public String getLastMessage(){
        return this.msg;
    }



    /**
     * @return la liste des clients
     */
    public ArrayList<Client> getLstCli() {
        return this.listClients;
    }
    
    /**
     * Permet d'envoyer un message
     * @param c 
     * @param msg le message à envoyer
     */
    public void sendMessage(Client c, String msg) {
        try {
            c.send(msg); // le client envoie un message
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param num le numéro du message 
     * @return le message souhaité
     */
    public String getMessage(String num) {
        for(String s : TAB_MESSAGES) {
            if(s.substring(0, 2).equals(num))
                return s;
        }
        return "";
    }

    /**
     * @return le datagramSocket
     */
    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }


    /**
     * Main de Server avec comme port "1500"
     */
    public static void main(String[] args) {
        new Server(1500, 2);
    }
}