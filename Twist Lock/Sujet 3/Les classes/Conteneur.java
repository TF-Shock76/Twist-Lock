/**
 * Classe Conteneur.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class  Conteneur {

	/**
	 * Ensemble des 4 coins du conteneur
	 */
	private char[] ensCoins;

	/**
	 * Valeur du conteneur comprise entre 5 et 54
	 */
	private int    valeur;

	/**
	 * Tableau constant comprenant la première lettre de chaque couleur de joueur
	 */
	private final char[] ENS_JOUEUR= {'R','V','B','J'};


	/**
	 * Constructeur initialisant le tableau de coin ainsi que la valeur aléatoirement entre 5 et 54
	 */
	public Conteneur() {
		this.ensCoins  = new char[4];

		for(int i = 0; i < this.ensCoins.length; i++)
			this.ensCoins[i] = ' ';

		this.valeur = (int) (Math.random() * 50) + 5;
	}

	public Conteneur(int val) {
		this.ensCoins  = new char[4];

		for(int i = 0; i < this.ensCoins.length; i++)
			this.ensCoins[i] = ' ';

		this.valeur = val;
	}


	/**
	 * Permet de placer la première lettre du joueur dans le voin voulu
	 * @param couleur première lettre de la couleur
	 * @param coin numéro du coin
	 */
	public void setJoueurCoin(char couleur, int coin) {
		this.ensCoins[coin] = couleur;
	}

	/**
	 * @return la valeur du coin (char) en fonction du coin placé en paramètre
	 * @param coin numéro du coin
	 */
	public char getValCoin(int coin) {
		return this.ensCoins[coin];
	}


	/**
	 * return la valeur du conteneur
	 */
	public int getVal() {
		return this.valeur;
	}


	/**
	 * @return la première lettre de la couleur du joueur qui possède ce Conteneur en fonction  des coins
	 * retourne 'N' si il n'y a pas de possesseur : aucun Twist Lock ou nombre égal par rapport aux joueurs
	 */
	public char getPossesseur() {
		int   tmp         = 0;
		char  tmp2        = 'N';
		int[] scoreJoueur = new int[4];

		// Initialisation du tableau des score à 0
		for(int x = 0; x < scoreJoueur.length; x++) {
			scoreJoueur[x] = 0;
		}

		// Vérification des coins et incrémentation du tableau de score
		for(int i = 0; i < this.ensCoins.length; i++) {
			if(this.ensCoins[i] == 'R') { scoreJoueur[0]++; }
			if(this.ensCoins[i] == 'V') { scoreJoueur[1]++; }
			if(this.ensCoins[i] == 'B') { scoreJoueur[2]++; }
			if(this.ensCoins[i] == 'J') { scoreJoueur[3]++; }
		}


		// Recherche du possesseur
		for(int y = 0; y < scoreJoueur.length; y++){
            if(tmp < scoreJoueur[y]) {
                tmp = scoreJoueur[y];
                tmp2 = this.ENS_JOUEUR[y];
            }
			else if(tmp == 2 && scoreJoueur[y] == 2)
            	{ return 'N'; }

			else if(tmp == 1 && scoreJoueur[y] == 1)
				{ return 'N'; }

			else if (scoreJoueur[0] == 1 && scoreJoueur[1] == 1 &&
			         scoreJoueur[2] == 1)
					 { return 'N'; }

            else if (scoreJoueur[0] == 1 && scoreJoueur[1] == 1 &&
			         scoreJoueur[2] == 1 && scoreJoueur[3] == 1)
					 { return 'N'; }
        }
		return tmp2;
	}

	/**
	 * @return si les coins du conteneur sont tous pris
	 */
	public boolean estPlein() {
		for(char c: ensCoins) {
			if(c == ' ') { return false; }
		}
		return true;
	}

	/**
	 * @return la valeur du conteneur
	 */
	public String toString() {
		return (valeur >= 10) ? "" + valeur : " " + valeur;
	}

}
