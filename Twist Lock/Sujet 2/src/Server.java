import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Classe Server.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class Server extends Thread{
    /**
    * le numero de port
    */
    private int port;

    /*
    * le nombre de client
    */
    private int nbClient;
    /*
    * le tableau de nom
    */
    private String[] tabNoms;

    /*
    * le datagramSoket
    */
    private DatagramSocket datagramSocket;

    /*
    * la liste des clients
    */
    private ArrayList<Client> listClients;

    /*
    * savoir si le jeu commence ou non 
    */
    private boolean commence;

    /*
    * un message
    */
    private String msg;

    /*
    * Le tableau de message avec tout les messages 
    */
    private final static String[] TAB_MESSAGES = { "01-la partie va commencer\nMAP=\n",
                                                   "10-A vous de jouer"             ,
                                                   "20-coup adversaire:"            ,
                                                   "21-coup joué illégal"           ,
                                                   "22-coup adversaire illegal"     ,
                                                   "50-Vous ne pouvez plus jouer"   ,
                                                   "88-Partie Terminée,"            ,
                                                   "91-demande non valide"             };

    //Partie jeu
    private Jeu jeu;

    /**
    * Constructeur
    */
    public Server(int port,int nbClient){
        this.port = port;
        this.nbClient=nbClient;
        listClients = new ArrayList<>();
        
        this.commence =  false;
        this.start();
    }

    /**
    * Method pour récupérer un client
    * @param sa adresse 
    */
    private Client getClient(SocketAddress sa){
        // Si l'adresse du client est identique à sa alors on retourne le client
        for (Client c : listClients) if(c.getAddress().equals(sa)) 
            return c;
        return null;
    }

    /**
    * @return l'index du client
    * @param sa adresse
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
    * On lance les thread
    */
    @Override
    public void run() {
        super.run();
        this.tabNoms = new String[nbClient];
        Client tmp;
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
            

            // On vérifie si c'est un nouveau
            if(client == null) { 
                // on vérifie si listClients est inférieur à nbClient
                if(listClients.size() < nbClient) {
                    Client c = new Client(packet.getSocketAddress(),this);
                    listClients.add(c);
                    c.send("Bonjour equipe : " + msg);
                    tabNoms[listClients.indexOf(c)] = msg;
                  // On vérifie si listCLients est égal à 2
                } else if(listClients.size() == 2) {
                    tmp = new Client(packet.getSocketAddress(),this);
                    tmp.send(getMessage("91"));
                }
                // on vérifie si la listClients = à 2 et que le jeu à pas commencer
                if(listClients.size() == 2 && !commence) {
                    this.jeu = new Jeu(this);
                    commence = !commence;
                    continue;
                }
            }
            // si le jeu commence
            else if(commence && jeu.getJoueurIndex() == getClientIndex(packet.getSocketAddress()))
                this.jeu.executeTurn(getClientIndex(packet.getSocketAddress()), this.msg);
           
            this.msg = "";
                  
            
        }

    }

    /**
    *  @return le dernier message envoyé par le client
    */
    public String getLastMessage(){
        return this.msg;
    }

    /**
    * On initalise les joueurs
    * @param jeu on entre un jeu
    */
    public void initJoueurs(Jeu jeu)
    {
        this.jeu = jeu;
        this.jeu.initJoueurs(this.listClients.size(), this.tabNoms);
    }

    /**
    * @return la liste des clients
    */
    public ArrayList<Client> getLstCli() {
        return this.listClients;
    }
    
    /**
    * Permet d'envoyer un message
    */
    public void sendMessage(Client c, String msg) {
        try {
            c.send(msg); // le client envoie un message
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * @return le message souhaiter
    * @param num le numéro du message 
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
    * le main
    */
    public static void main(String[] args) {
        new Server(1500,2);
    }
}