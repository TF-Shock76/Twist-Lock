import java.util.*;


/**
 * Classe Jeu.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 3.0
 */

public class Jeu{

	/**
	 * Objet permettant l'affichage du plateau
	 * @see IHM
	 */
	private IHM ihm;

	/**
	 * Tableau constant avec toutes les couleurs possibles pour Joueur
	 * @see String
	 */
	private final  String[]          tabCoul = {"Rouge", "Vert"};

	/**
	 * Il y aura forcément 2 joueurs
	 * @see String
	 */
	private final int NB_JOUEURS = 2;
	
	/**
	 * ArrayList comprenant tous les Joueurs
	 * @see Joueur
	 */
	private static ArrayList<Joueur> alJoueurs;

	/**
	 * Tableau à deux dimensions comprenant tous les conteneurs
	 * @see Conteneur
	 */
	private static Conteneur[][]     plateau;

	/**
	 * le serveur
	 * @see Server
	 */
	private Server server;

	/**
	 * Position
	 * @see String
	 */
	private String pos;


	/**
	 * Constructeur
	 */
	public Jeu(Server server, String[] tabNoms) {
		this.server = server;
		this.pos    = "";
		this.ihm    = new IHM();

		this.lancer(tabNoms);
	}

	/**
	 * Permet d'affecter au plateau du jeu la valeur de tab
	 */
	public void setPlateau(Conteneur[][] tab) {
		Jeu.plateau = tab;
	}

	/**
	 * Méthode controlant le déroulement de la partie
	 */
	public void lancer(String[] tabNoms) {
		// Appel de la méthode initJoueurs pour les initialiser
		this.initJoueurs(tabNoms);
		
		this.initPlateau();


		String s = server.getMessage("01") + tabToMap();
		for(Client c : server.getLstCli())
			server.sendMessage(c, s);

		s = server.getMessage("10") + " (" + Jeu.alJoueurs.get(0).getCouleur().toUpperCase()
				+ ") :\nenvoie au serveur chaine\n ";
		server.sendMessage(server.getLstCli().get(0), s);





		Jeu.alJoueurs.get(0).setTour(true);
	}



	/**
	 *Execution d'un tour à chaque réception d'un message de commande valide du client
	 * @param client index du client
	 * @param msg msg du client
	 */
	public void executeTurn(int client, String msg){
		int tmp = 0; // utiliser pour verifier les twister des joueur MOI
		boolean plein = false ;
		boolean enCours = true;
		Joueur thePlayer = Jeu.alJoueurs.get(client);
		Joueur autreJoueur = Jeu.alJoueurs.get((client + 1) % 2);


		



		if(thePlayer.getNbTwist() > 0) {
			thePlayer.setTour(false);
            autreJoueur.setTour(true);

			String s = msg;

			server.sendMessage(server.getLstCli().get((client + 1) % 2), server.getMessage("20") + msg);


			this.pos = s;

			//Reception du message				
			int      lig    = Character.getNumericValue(pos.charAt(0)); // Récupération de la ligne
			char     col    = Character.toUpperCase(    pos.charAt(1)); // Récupération de la colonne
			int      coin   = Character.getNumericValue(pos.charAt(2)); // Récupération du coin

			// Si il n'est pas possible de poser un twist à l'emplacement indiqué
			if((lig > Jeu.plateau.length + 1 || lig < 1 || col < 'A' || col > 'A' + Jeu.plateau[0].length - 1 || coin <= 0 || coin > 4) || !this.poserTwist(thePlayer, lig, col, coin)) {
				server.sendMessage(server.getLstCli().get(client)          , server.getMessage("21"));
				server.sendMessage(server.getLstCli().get((client + 1) % 2), server.getMessage("22"));

				if(thePlayer.getNbTwist() > 1)
					thePlayer.setNbTwist((thePlayer.getNbTwist()) - 1);

				thePlayer.setNbTwist((thePlayer.getNbTwist()) - 1);
			}
			else // Il a bien posé donc on lui en enlève un
				thePlayer.setNbTwist((thePlayer.getNbTwist()) - 1);


						
			s = ihm.afficherPlateau(plateau);
			for(Client c : server.getLstCli())
				server.sendMessage(c, s);


			if(enCours && autreJoueur.getNbTwist() > 0) {
				s = server.getMessage("10") + " (" + autreJoueur.getCouleur().toUpperCase() + ") :\nenvoie au serveur chaine\n";
				server.sendMessage(server.getLstCli().get((client + 1) % 2), s);

			


			} else {
                server.sendMessage(server.getLstCli().get((client + 1) % 2), server.getMessage("50"));
                s = server.getMessage("10") + " (" + Jeu.alJoueurs.get(0).getCouleur().toUpperCase()
						+ ") :\nenvoie au serveur chaine\n ";
                server.sendMessage(server.getLstCli().get(client), s);
            }
        } else {
            server.sendMessage(server.getLstCli().get(client), server.getMessage("50"));
            String s = server.getMessage("10") + " (" + Jeu.alJoueurs.get(0).getCouleur().toUpperCase()
					+ ") :\nenvoie au serveur chaine\n ";
			server.sendMessage(server.getLstCli().get((client + 1) % 2), s);
        }
		thePlayer.setTour(false);


		// Vérification du nombre de Twist Lock restants. S'il n'y en a plus du tout, tmp = 0 en sortant du for
		tmp = 0;
		for(int i = 0 ; i < Jeu.alJoueurs.size(); i++)
			tmp += Jeu.alJoueurs.get(i).getNbTwist();

		// Vérification de si le plateau est plein ou pas
		for(int lig = 0; lig < plateau.length; lig++) {
			for(int col = 0; col < plateau[lig].length; col++) {
				if(!plateau[lig][col].estPlein()) {
					plein = false;
					break;
				}
				else {
					plein = true;
				}
			}
		}

		// Si la partie est finie : soit il n'y a plus de Twist Lock ou que le plateau est plein niveau Twist Lock
		if(tmp == 0 || plein) { 
			enCours     = false;
			String scoreFinal = this.getScore(thePlayer) + " - " + this.getScore(autreJoueur);
			for(int i = 0; i < Jeu.alJoueurs.size(); i++)
				server.sendMessage(server.getLstCli().get(i), server.getMessage("88"));
			//Vérification du gagnant puis envoi du score
			if(this.winner().equals("egalite")) {
				server.sendMessage(server.getLstCli().get(client), "Egalite " + scoreFinal);
				server.sendMessage(server.getLstCli().get((client + 1) % 2), "Egalite " + scoreFinal);
			}
			else if(this.winner().equals(thePlayer.getNom())) {
				server.sendMessage(server.getLstCli().get(client), "Vous avez gagne " + scoreFinal);
				server.sendMessage(server.getLstCli().get((client + 1) % 2), "Vous avez perdu " + scoreFinal);
			}
			else if(this.winner().equals(autreJoueur.getNom())) {
				server.sendMessage(server.getLstCli().get(client), "Vous avez perdu " + scoreFinal);
				server.sendMessage(server.getLstCli().get((client + 1) % 2), "Vous avez gagne " + scoreFinal);
			}
			


			System.exit(0);
		}
	}

	/**
	 * Inititialiser les joueurs
	 * @param nbJoueurs nombre de jouer à affecter
	 * @param tabNoms tableau de nom
	 */
	public void initJoueurs(String[] tabNoms) {
		Jeu.alJoueurs = new ArrayList<Joueur>();

		for(int i = 0; i < NB_JOUEURS; i++) {
			Jeu.alJoueurs.add(new Joueur(this.tabCoul[i], tabNoms[i]));
		}
	}

	
	/**
	 * Méthode permettant d'affecter la position si c'est le tour du joueur
	 * @param numJoueurs numéro du joueur
	 * @param pos position du twist lock
	 */
	public boolean setPosition(int numJoueur, String pos) {
		if(Jeu.alJoueurs.get(numJoueur).getTour()) {
			this.pos = pos;
			return true;
		}
		else
			return false;
	}


	/**
	 * Méthode permettant d'initaliser le plateau aléatoirement
	 */
	public void initPlateau() {
		int a = (int) (Math.random() * 5) + 4;
		int b = (int) (Math.random() * 5) + 4;

		plateau = new Conteneur[a][b]; // Pour les tests mais ensuite remplacer par a et b
		for(int i = 0; i < plateau.length; i++)
			for(int j = 0; j < plateau[i].length; j++)
				plateau[i][j] = new Conteneur();
	}


	/**
	 * Permet de poser un twist et de le positionner sur les conteneurs voisins dans les bons angles
	 * @return true si le twist posé répond aux conditions
	 * @return false si ça ne repond pas aux conditions
	 */
	public boolean poserTwist(Joueur j, int lig, char col, int coin) {
		 // on peut poser le Twist
		if(lig >= 0 && lig <= plateau.length && ((int)Character.toLowerCase(col)-96)-1 >= 0 && ((int)Character.toLowerCase(col)-96)-1 <= plateau[0].length && coin <= 4 && coin >= 1) {
			int colonne = ((int)Character.toLowerCase(col)-96)-1; // position de la lettre dans l'alphabet. Attention, A = 0 et non 1
			int ligne   = lig - 1;

			// Si le coin est déjà pris
			if(plateau[ligne][colonne].getValCoin(coin - 1) != ' ') {
				return false;
			}

			plateau[ligne][colonne].setJoueurCoin(j.getCouleur().charAt(0), coin - 1);

			// Les différentes conditions selon le coin choisi par l'utilisateur
			switch (coin) {
				case 1:
					if(ligne == 0 && colonne != 0) {
						plateau[ligne][colonne - 1].setJoueurCoin(j.getCouleur().charAt(0), coin);
					}

					if (ligne != 0 && colonne != 0) {
						plateau[ligne    ][colonne - 1].setJoueurCoin(j.getCouleur().charAt(0), coin    );
						plateau[ligne - 1][colonne - 1].setJoueurCoin(j.getCouleur().charAt(0), coin + 1);
						plateau[ligne - 1][colonne    ].setJoueurCoin(j.getCouleur().charAt(0), coin + 2);
					}

					if (ligne != 0 && colonne == 0) {
						plateau[ligne - 1][colonne].setJoueurCoin(j.getCouleur().charAt(0), coin + 2);
					}
					break;

				case 2:
					if (ligne == 0 && colonne != plateau[0].length - 1) {
						plateau[ligne][colonne + 1].setJoueurCoin(j.getCouleur().charAt(0), coin - 2);
					}

					if (ligne != 0 && colonne != plateau[0].length - 1) {
						plateau[ligne    ][colonne + 1].setJoueurCoin(j.getCouleur().charAt(0), coin - 2);
						plateau[ligne - 1][colonne    ].setJoueurCoin(j.getCouleur().charAt(0), coin    );
						plateau[ligne - 1][colonne + 1].setJoueurCoin(j.getCouleur().charAt(0), coin + 1);
					}

					if (ligne != 0 && colonne == plateau[0].length - 1) {
						plateau[ligne - 1][colonne].setJoueurCoin(j.getCouleur().charAt(0), coin);
					}
					break;

				case 3:
					if (ligne == plateau.length - 1 && colonne != plateau[0].length - 1) {
						plateau[ligne][colonne + 1].setJoueurCoin(j.getCouleur().charAt(0), coin);
					}

					if (ligne != plateau.length - 1 && colonne != plateau[0].length - 1) {
						plateau[ligne    ][colonne + 1].setJoueurCoin(j.getCouleur().charAt(0), coin    );
						plateau[ligne + 1][colonne    ].setJoueurCoin(j.getCouleur().charAt(0), coin - 2);
						plateau[ligne + 1][colonne + 1].setJoueurCoin(j.getCouleur().charAt(0), coin - 3);
					}

					if (ligne != plateau.length - 1 && colonne == plateau[0].length - 1) {
						plateau[ligne + 1][colonne].setJoueurCoin(j.getCouleur().charAt(0), coin - 2);
					}
					break;

				case 4:
					if (ligne == plateau.length - 1 && colonne != 0) {
						plateau[ligne][colonne - 1].setJoueurCoin(j.getCouleur().charAt(0), coin - 2);
					}

					if (ligne != plateau.length - 1 && colonne != 0) {
						plateau[ligne    ][colonne - 1].setJoueurCoin(j.getCouleur().charAt(0), coin - 2);
						plateau[ligne + 1][colonne    ].setJoueurCoin(j.getCouleur().charAt(0), coin - 4);
						plateau[ligne + 1][colonne - 1].setJoueurCoin(j.getCouleur().charAt(0), coin - 3);
					}

					if (ligne != plateau.length - 1 && colonne == 0) {
						plateau[ligne + 1][colonne].setJoueurCoin(j.getCouleur().charAt(0), coin - 4);
					}
			}
			return true;
		}

		else
			return false;
	}

	


	/**
	 * @return l'ensemble des joueurs
	 */
	public ArrayList<Joueur> getTabJoueurs() {
		return Jeu.alJoueurs;
	}


	public Conteneur[][] getPlateau() {
		return Jeu.plateau;
	}


	/**
	 * @param joueur joueur dont on veut obtenir le score
	 * @return le score du joueur
	 */
	public int getScore(Joueur joueur) {
        int val = 0;
        char coulJoueur = joueur.getCouleur().charAt(0);

        for(int i = 0; i < Jeu.plateau.length; i++)
            for(int j = 0; j < Jeu.plateau[0].length; j++)
                if(Jeu.plateau[i][j].getPossesseur() == coulJoueur)
                    val += Jeu.plateau[i][j].getVal();

        joueur.setScore(val);
        return val;
	}


	/**
	 * @return le nom du joueur qui gagne ou égalité si tel est le cas
	 */
	public String winner() {
		int scoreTmp = 0;
		int id       = 0;
		int eg       = 0;
		for(int i = 0; i < Jeu.alJoueurs.size(); i++) {
            if(scoreTmp < getScore(Jeu.alJoueurs.get(i))) {
                scoreTmp = getScore(Jeu.alJoueurs.get(i));
                id = i;
            }
            else if(scoreTmp == getScore(Jeu.alJoueurs.get(i))) {
                eg ++;
                if(eg == Jeu.alJoueurs.size()) {
                    return "egalite";
                }
            }
        }
		return Jeu.alJoueurs.get(id).getNom();
	}

	/**
	 * @return le nom du joueur qui gagne ou égalité si tel est le cas
	 */
	public int getJoueurIndex() {
        for(int i = 0; i < alJoueurs.size(); i++) {
            if(alJoueurs.get(i).getTour())
                return i;
        }
        return -1;
    }

	/**
	 * Permet de transposer le tableau plateau avec toutes les valeurs des conteneurs
	 * en un String avec ':' comme séparateur de colonne et '|' en tant que séparateur de ligne
	 * @return la map bien comme il le faut
	 */
	public String tabToMap() {
		String sRet = "";
		for(int i = 0; i < Jeu.plateau.length; i++) {
			for(int j = 0; j < Jeu.plateau[0].length; j++) {
				sRet += Jeu.plateau[i][j].getVal();
				if(j < Jeu.plateau[0].length - 1)
					sRet += ":";
			}
			sRet += "|";
		}
		return sRet;
	}

	
}