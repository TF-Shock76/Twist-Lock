import java.util.*;
import java.util.Scanner;

/**
 * Classe Jeu.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class Jeu {

	/**
	 * Tableau constant avec toutes les couleurs possibles pour Joueur
	 * @see String
	 */
	private final  String[]          tabCoul = {"Rouge", "Vert", "Bleu", "Jaune"};

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
	 * Variable booléenne gérant le mode debug
	 */
	private        boolean           debug;


	/**
	 * Constructeur
	 */
	public Jeu() {
		this.lancer();
	}

	/**
	 * Méthode controlant le déroulement de la partie
	 */
	public void lancer() {
		Scanner   sc = new Scanner(System.in); // Scanner pour récuperer les entrée des utilisateurs
		String    nbJoueurs; // Nombre de joueurs
		char      debug;     // Mode debug [o]/[O] ou non [n]/[N]
		int       vall;      // Une fois vérifié, on met le nombre de joueur en entier
		String[] tabNoms;    // tableau avec les noms des joueurs

		// Permet de choisir si l'on souhaite passer par le mode debug ou non
		do {
			System.out.print("Souhaitez-vous être en mode debug? [O]ui [N]on : ");
			debug = sc.nextLine().charAt(0);

			if(debug == 'O' || debug == 'o')

				this.debug = true;
			if(debug == 'N' || debug == 'n')
				this.debug = false;

		} while (debug != 'N' && debug != 'n' && debug != 'O' && debug != 'o');


		// Choix du nombre de joueur : 2, 3 ou 4
		do {
			System.out.print("Combien de joueurs voulez-vous? (2 à 4) : ");
			nbJoueurs = sc.nextLine();
		} while(!nbJoueurs.equals("2") && !nbJoueurs.equals("3") && !nbJoueurs.equals("4")); // à vérifier pour le nextInt

		vall    = Integer.parseInt(nbJoueurs);
		tabNoms = new String[vall];

		// Demande du nom de chaque joueur
		for(int i = 0; i < vall; i++) {
			System.out.print("Joueur " + this.tabCoul[i] + " : quel est votre nom ? ");
			String nom = sc.nextLine();
			tabNoms[i] = nom;
			System.out.println();
		}

		// Appel de la méthode initJoueurs pour les initialiser
		this.initJoueurs(vall, tabNoms);

		// Si le mode debug est activé, on demande le nombre de TwistLock puis les dimensions du tableau
		if (this.debug) {
			String nbTwist = "";
			int a, b;
			do {
				System.out.print("Combien de TwistLock doivent avoir les joueurs ? : ");
				nbTwist = sc.nextLine();
				a = nbTwist.compareTo("0");
				b = nbTwist.compareTo("99999");
			} while (a < 0 || b > 0);

			for(Joueur j : this.alJoueurs) {
				j.setNbTwist(Integer.parseInt(nbTwist));
			}

			int lig, col;
			do {
				System.out.print("Combien de lignes souhaitez-vous avoir dans votre tableau? : ");
				lig = Integer.parseInt(sc.nextLine());

				System.out.print("Combien de colonnes souhaitez-vous avoir dans votre tableau? : ");
				col = Integer.parseInt(sc.nextLine());
			} while (lig < 0 && lig > 50 && col < 0 && col > 26);
			this.initPlateau(lig, col);

		}
		else
			this.initPlateau();

		int tmp = 0; // utiliser pour verifier les twister des joueur MOI
		boolean plein = false ;
		boolean enCours = true;
		System.out.println(afficherPlateau());
		//Dans la partie, un joueur pose un twist
		while (enCours) { // condition de fin
			for(int i = 0; i < alJoueurs.size(); i++) {
				if(this.alJoueurs.get(i).getNbTwist() > 0) {
					System.out.println("Joueur " + this.alJoueurs.get(i).getCouleur() + " : Choisissez où poser votre twist (ligne en entier, colonne en lettre, coin en entier compris entre 1 et 4). \n Attention, séparez bien par un espace les elements");
					String   pos    = sc.nextLine(); //Récupération de la ligne entrée par l'utilisateur
					String[] tabElt = pos.split(" "); // Tableau découpant l'entrée de l'utilisateur
					int      lig    = Integer.parseInt(tabElt[0]); // Récupération de la ligne
					char     col    = Character.toUpperCase(tabElt[1].charAt(0)); // Récupération de la colonne
					int      coin   = Integer.parseInt(tabElt[2]); // Récupération du coin

					// Si il n'est pas possible de poser un twist à l'emplacement indiqué
					if(!this.poserTwist(this.alJoueurs.get(i), lig, col, coin)) {
						String err = "Une des informations entrees est mauvaise. \n Votre Twist Lock est perdu.";
						this.alJoueurs.get(i).setNbTwist((this.alJoueurs.get(i).getNbTwist()) - 1);

						if(this.alJoueurs.get(i).getNbTwist() > 1) // S'il reste assez de Twist Lock au joueur, on lui en enlève un en plus comme pénalité
						{
							this.alJoueurs.get(i).setNbTwist((this.alJoueurs.get(i).getNbTwist()) - 1);
							err += " De plus, vous prenez une pénalité et vous perdez un de vos Twist Lock! Il vous en reste " + this.alJoueurs.get(i).getNbTwist();
						}

						else
							err += "Vous n'avez plus de Twist Lock.";

						System.out.println(err); // On affiche la phrase d'erreur
					}
					else // Il a bien posé donc on lui en enlève un
						this.alJoueurs.get(i).setNbTwist((this.alJoueurs.get(i).getNbTwist()) - 1);
					System.out.println(afficherPlateau()); // On affiche de nouveau le plateau avec la modification réalisée
					System.out.println("Il vous reste : " + this.alJoueurs.get(i).getNbTwist() + " TwistLock " + this.alJoueurs.get(i).getNom() + "\n\n\n");
				}

			}

			// Vérification du nombre de Twist Lock restants. s'il n'y en a plus du tout, tmp = 0 en sortant du for
			tmp = 0;
			for(int i = 0 ; i < this.alJoueurs.size(); i++)
				tmp += this.alJoueurs.get(i).getNbTwist();

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
				String sRet = "Fin de partie, ";
				sRet       += this.winner().equals("egalite") ? "" : "le gagnant est ";
				sRet       += this.winner();
				System.out.println(sRet);
				System.exit(0);
			}
		}
	}

	/**
	 * Inititialiser les joueurs
	 * @param nbJoueurs nombre de jouer à affecter
	 * @param tabNoms tableau de nom
	 */
	public void initJoueurs(int nbJoueurs, String[] tabNoms) {
		this.alJoueurs = new ArrayList<Joueur>();

		for(int i = 0; i < nbJoueurs; i++)
			this.alJoueurs.add( new Joueur(this.tabCoul[i], tabNoms[i]) );
	}


	/**
	 * Initaliser le plateau aléatoirement
	 */
	public void initPlateau() {
		int a = (int) (Math.random() * 30) + 1;
		int b = (int) (Math.random() * 26) + 1;

		plateau = new Conteneur[a][b]; // Pour les tests mais ensuite remplacer par a et b
		for(int i = 0; i < plateau.length; i++)
			for(int j = 0; j < plateau[i].length; j++)
				plateau[i][j] = new Conteneur();
	}

	/**
	 * Initialiser le tableau en mode debug
	 * @param a nombre de ligne
	 * @param b nombre de colonne
	 */
	public void initPlateau(int a, int b) {
		plateau = new Conteneur[a][b];
		for(int i = 0; i < plateau.length; i++)
			for(int j = 0; j < plateau[i].length; j++)
				plateau[i][j] = new Conteneur();
	}

	/**
	 * Permet de poser un twist et de le positionner sur les conteneurs voisins dans les bons angles
	 * @return true si le twist posé répond aux conditions
	 * @return false si ça ne repond pas aux conditions
	 */
	public static boolean poserTwist(Joueur j, int lig, char col, int coin) {
		 // on peut poser le Twist
		if(lig >= 0 && lig <= plateau.length && ((int)Character.toLowerCase(col)-96)-1 >= 0 && ((int)Character.toLowerCase(col)-96)-1 <= plateau[0].length && coin <= 4 && coin >= 1) {
			int colonne = ((int)Character.toLowerCase(col)-96)-1; // position de la lettre dans l'alphabet. Attention, A = 0 et non 1
			int ligne   = lig - 1;

			// Si le coin est déjà pris
			if(plateau[ligne][colonne].getValCoin(coin - 1) != ' ') {
				System.out.println("COIN DEJA PRIS");
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
	 * @return l'affichage du plateau complet
	 */
	public static String afficherPlateau() {
		String sRet = "       ";
		String sep  = " ";

		// Lignes des lettres
		for(int cpt = 0; cpt < plateau[0].length; cpt++) {
			sRet += (char) ('A' + cpt);
			sRet += "    ";
		}

		// Ligne de séparation avec les valeurs des coins
		for(int i = 0; i < plateau[0].length; i++) {
			sep += plateau[0][i].getValCoin(0) + "----";
		}

		sep  += plateau[0][plateau[0].length - 1].getValCoin(1);


		sRet += "\n    " + sep + "\n  ";

		// Création des lignes
		for(int cptLig = 0; cptLig < plateau.length; cptLig++) {

			// Ligne de séparation
			sep = " ";
			for(int i = 0; i < plateau[0].length; i++) {
				sep += plateau[cptLig][i].getValCoin(0) + "----";
			}

			sep  += plateau[cptLig][plateau[0].length - 1].getValCoin(1);

			if(cptLig != 0)
				sRet += "    " + sep + "\n  ";

			// Permet de tout bien aligner
			sRet += (cptLig + 1 >= 10) ? cptLig + 1 : cptLig + 1 + " ";

			// positionne les séparations puis la valeur du conteneur
			for(int cptCol = 0; cptCol < plateau[cptLig].length; cptCol++) {
				sRet += " | " + plateau[cptLig][cptCol];

			}

			sRet += " | \n";
		}

		sep = " ";
		for(int i = 0; i < plateau[0].length; i++) {
			sep += plateau[plateau.length - 1][i].getValCoin(3) + "----";
		}

		sep  += plateau[plateau.length - 1][plateau[0].length - 1].getValCoin(2);

		sRet += "    " + sep + "\n";

		return sRet;
	}


	/**
	 * @return l'ensemble des joueurs
	 */
	public ArrayList<Joueur> getTabJoueurs() {
		return this.alJoueurs;
	}


	/**
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
		for(int i = 0; i < this.alJoueurs.size();i++) {
            if(scoreTmp < getScore(this.alJoueurs.get(i))) {
                scoreTmp = getScore(this.alJoueurs.get(i));
                id = i;
            }
            else if(scoreTmp == getScore(this.alJoueurs.get(i))) {
                eg ++;
                if(eg == this.alJoueurs.size()) {
                    return "egalite";
                }
            }
        }
		return this.alJoueurs.get(id).getNom();
	}

	/**
	 * Main du Jeu
	 * @param args
	 */
	public static void main(String[] args) {
		Jeu j = new Jeu();
	}
}
