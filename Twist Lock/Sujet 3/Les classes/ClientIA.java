import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe ClientIA.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class ClientIA
{
    /**
     * le datagramSocket
     * @see DatagramSocket
     */
    private DatagramSocket    ds;

    /**
     * ensemble de toutes les possibilités de déplacement
     * @see String
     */
    private ArrayList<String> alPossibilite;

    /**
     * plateau généré qui permet d'avoir une copie du plateau du jeu
     * @see Conteneur
     */
    private Conteneur[][]     plateau;

    /**
     * DatagramPacket qui permet d'envoyer les informations voulues
     * @see DatagramPacket
     */
    private DatagramPacket    dataToSend;

    /**
     * Adresse du serveur
     * @see String
     */
    private String            adresse;

    /**
     * port du serveur
     */
    private int               port;


    /*
     * Constructeur
     */
    private ClientIA() throws IOException
    {
        this.alPossibilite = new ArrayList<String>();
        ds = new DatagramSocket();
        final Scanner sc = new Scanner(System.in);

        // on demande l'adresse du serveur
        do{
            System.out.println("Adresse du serveur : ");
            this.adresse = sc.next();
        }while(this.adresse.equals(""));

        // on demande au client de saisir un numero de port correct, on répète la situation autant de fois qu'il le faut
        System.out.println("Port : ");
        do {
            try {
                this.port = sc.nextInt();
            } catch (final Exception e) {
                System.out.println("saisir un numero de port correct");
            }
        } while (this.port == 0);

        // on créé un nouveau thread
        new Thread(() -> {
            // on attend en continu de recevoir des messages
            do {
                final DatagramPacket msg = new DatagramPacket(new byte[1024], 1024);
                // on reçois un message
                try {
                    ds.receive(msg);
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                // on converti le datagramPacket reçu en string
                try{
                    String message = new String(msg.getData(), "UTF-8").trim();

                    // Si le message commence par "01" donc la map, on appelle la métode setPlateau(message)
                    if (message.substring(0, 2).equals("01")) {
                        this.setPlateau(message);
                    }

                    // Si le message commence par "20" donc coup adverse, on appelle la métode coupAdverse(message)
                    if (message.substring(0, 2).equals("20")) {
                        coupAdverse(message);
                    }

                    // Si le message commence par "10" donc coup de ce client, on appelle la métode jouer()
                    // jouer() retourne la valeur du déplacement du joueur pour l'afficher
                    if (message.substring(0, 2).equals("10")) {
                        message += jouer();
                    }

                    //Le message reçu est affiché par le client
                    System.out.println("\n" + message);
                }
                catch(final Exception e){ e.printStackTrace(); }
            } while (ds != null);

        }).start();

        // Demande le nom de l'équipe et l'envoie au serveur
        System.out.println("\nVotre nom d'equipe : ");

        String msg;
        msg = sc.next();
        this.dataToSend = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(this.adresse), this.port);
        ds.send(dataToSend);
    }

    /**
     * Méthode appelée lorsque le client joue un coup
     * @return le déplacement du joueur
     */
    public String jouer(){
        String msg = getPossAlea(); //Récupération d'un coup
        poserTwist(msg); // pose un twist aux coordonnées du message
        envoyer(msg); // envoie les coordonnées
        return msg;
    }

    /**
     * Appel de la méthode poserTwist(msg) lorsque le joueur adverse joue
     * @param msg
     */
    public void coupAdverse(String msg){
        poserTwist(msg);
    }


    /**
     * Permet d'envoyer un message
     * @param msg
     */
    public void envoyer(String msg){
        try {
            this.dataToSend = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(this.adresse), this.port);
            ds.send(dataToSend);
        } catch (Exception e) {
            System.out.println("CA MARCHE PLUS!!!!!!!!");
        }
    }

    /**
     * Récupère la map suite à la reception du message commencant par "01"
     * et initialise grâce à ce code this.plateau avec les valeurs récupérées
     * @param code
     */
    public void setPlateau(String code) {
        String[] tabInfo; // Découpe le message reçu : tabInfo[0] = le message, tabInfo[1] = la map
        String[] tabLig;  // Découpe les lignes
        String[] tabCol;  // Découpe les colonnes

        // Effectue les découpages
        tabInfo = code.split("=");
        tabLig  = tabInfo[1].split("\\|");
        tabCol  = tabLig[0].split(":");

        // Initialise le plateau avec la taille des tableaux
        this.plateau = new Conteneur[tabLig.length][tabCol.length];


        // Remplit le plateau avec les valeurs récupérées
        for(int i = 0; i < tabLig.length; i++) {
            for(int j = 0; j < tabCol.length; j++) {
				this.plateau[i][j] = new Conteneur(Integer.parseInt(tabCol[j]));
			}
        }
    }

    /**
     * Appelée quand le client joue ou qu'il reçoit le message commençant par "20"
     * Méthode permettant le placement d'un twist sur le plateau
     * @param message
     */
    public void poserTwist(String message) {
        int lig, col, coin;
        if(message.substring(0,2).equals("20")) {
            String[] tabInfo = message.split(":");
            lig  = Character.getNumericValue(tabInfo[2].charAt(0)-1);
            col  = ((int)Character.toLowerCase(tabInfo[2].charAt(1))-96)-1;
            coin = Character.getNumericValue(tabInfo[2].charAt(2));
        }
        else {
            lig  = Character.getNumericValue(message.charAt(0)-1);
            col  = ((int)Character.toLowerCase(message.charAt(1))-96)-1;
            coin = Character.getNumericValue(message.charAt(2));
        }

        this.plateau[lig][col].setJoueurCoin('X', coin - 1);

        // Les différentes conditions selon le coin choisi par l'utilisateur
        switch (coin) {
            case 1:
                if(lig == 0 && col != 0) {
                    plateau[lig][col - 1].setJoueurCoin('X', coin);
                }

                if (lig != 0 && col != 0) {
                    plateau[lig    ][col - 1].setJoueurCoin('X', coin    );
                    plateau[lig - 1][col - 1].setJoueurCoin('X', coin + 1);
                    plateau[lig - 1][col    ].setJoueurCoin('X', coin + 2);
                }

                if (lig != 0 && col == 0) {
                    plateau[lig - 1][col].setJoueurCoin('X', coin + 2);
                }
                break;

            case 2:
                if (lig == 0 && col != plateau[0].length - 1) {
                    plateau[lig][col + 1].setJoueurCoin('X', coin - 2);
                }

                if (lig != 0 && col != plateau[0].length - 1) {
                    plateau[lig    ][col + 1].setJoueurCoin('X', coin - 2);
                    plateau[lig - 1][col    ].setJoueurCoin('X', coin    );
                    plateau[lig - 1][col + 1].setJoueurCoin('X', coin + 1);
                }

                if (lig != 0 && col == plateau[0].length - 1) {
                    plateau[lig - 1][col].setJoueurCoin('X', coin);
                }
                break;

            case 3:
                if (lig == plateau.length - 1 && col != plateau[0].length - 1) {
                    plateau[lig][col + 1].setJoueurCoin('X', coin);
                }

                if (lig != plateau.length - 1 && col != plateau[0].length - 1) {
                    plateau[lig    ][col + 1].setJoueurCoin('X', coin    );
                    plateau[lig + 1][col    ].setJoueurCoin('X', coin - 2);
                    plateau[lig + 1][col + 1].setJoueurCoin('X', coin - 3);
                }

                if (lig != plateau.length - 1 && col == plateau[0].length - 1) {
                    plateau[lig + 1][col].setJoueurCoin('X', coin - 2);
                }
                break;

            case 4:
                if (lig == plateau.length - 1 && col != 0) {
                    plateau[lig][col - 1].setJoueurCoin('X', coin - 2);
                }

                if (lig != plateau.length - 1 && col != 0) {
                    plateau[lig    ][col - 1].setJoueurCoin('X', coin - 2);
                    plateau[lig + 1][col    ].setJoueurCoin('X', coin - 4);
                    plateau[lig + 1][col - 1].setJoueurCoin('X', coin - 3);
                }

                if (lig != plateau.length - 1 && col == 0) {
                    plateau[lig + 1][col].setJoueurCoin('X', coin - 4);
                }
        }
    }


    /**
     * Méthode qui initialise l'Arraylist de possibilitées
     */
	public void getPossibilite() {
		char c;
		this.alPossibilite.clear();
        for(int i = 0; i < plateau.length; i++) // Ligne
            for(int j = 0; j < plateau[0].length; j++) // Colonne
                for(int k = 0; k < 4; k++) { // Coins
                    if(plateau[i][j].getValCoin(k) == ' ') {
                        c = (char) (j + 97); // Converti la colonne en lettre
                        c = Character.toUpperCase(c); // La met en majuscule

                        // Ajoute dans this.alPossibilite les possibilités sous la forme par exemple 1A1 ou 6C3 (ligne colonne coin)
                        this.alPossibilite.add("" + (i+1) + c + (k+1));
                    }
                }
    }

    /**
     * Méthode qui récupère aléatoirement deux positions différentes possibles, les compares, et retourne la meilleure
     * @return une chaine correspondant a une position
     */
    public String getPossAlea() {
        getPossibilite();
        String poss1   , poss2;
        int    valPoss1, valPoss2;
        int    lig     , col,      coin;

        poss1 = this.alPossibilite.get((int)(Math.random()*this.alPossibilite.size())); //prend une possibilitée de jeu
        do {
            poss2 = this.alPossibilite.get((int)(Math.random()*this.alPossibilite.size())); //prend une seconde possibilitée
        } while (poss1.equals(poss2)); //vérifie que les deux positions ne sont pas identiques


        lig  = Character.getNumericValue(poss1.charAt(0)); //traduit la possibilitée en position compréhensible par le programme
        col  = ((int)Character.toLowerCase(poss1.charAt(1))-96)-1;
        coin = Character.getNumericValue(poss1.charAt(2));

        valPoss1 = this.plateau[lig-1][col].getValCoin(coin - 1); //prend la valeur de la position 1


        lig  = Character.getNumericValue(poss2.charAt(0)); //traduit la possibilitée en position compréhensible par le programme
        col  = ((int)Character.toLowerCase(poss2.charAt(1))-96)-1;
        coin = Character.getNumericValue(poss2.charAt(2));

        valPoss2 = this.plateau[lig-1][col].getValCoin(coin - 1); //prend la valeur de la position 2


        return (valPoss1 > valPoss2) ? poss1 : poss2; //retourne la position avec la valeur la plus élevée
    }

    /**
     * Main du client
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new ClientIA();
    }
}
