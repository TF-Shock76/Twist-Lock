/**
 * Classe Joueur.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

public class Joueur {
	 
	/**
	 * Couleur du Joueur
	 * @see String
	 */
	private String couleur;

	/**
	 * Nombre de twist lock restant
	 */
	private int    nbTwist;

	/**
	 * Nom du joueur
	 */
	private String nom;

	/**
	 * Score du joueur
	 */
	private int    score;

	/**
	 * true si 'est son tour et false dans le cas contraire
	 */
	private boolean tour;

	/**
	 * Constructeur initialisant toutes les variables
	 * @param couleur couleur du Joueur
	 * @param nom nom du joueur
	 */
	public Joueur(String couleur, String nom)
	{
		this.couleur = couleur;
		this.nbTwist = 20;
		this.nom     = nom;
		this.score   = 0;
		this.tour    = false;
	}

	/**
	 * @return le nom du joueur
	 */
	public String getNom()           { return this.nom;     }

	/**
	 * @return la couleur du joueur
	 */
	public String getCouleur()       { return this.couleur; }

	/**
	 * @return le nombre restant du joueur
	 */
	public int    getNbTwist()       { return this.nbTwist; }

	/**
	 * @return le score du joueur
	 */
	public int    getScore()         { return this.score;   }

	/**
	 * Affecte le nombre de twist lock
	 * @param nb nombre à affecter
	 */
	public void   setNbTwist(int nb) { this.nbTwist = nb;   }

	/**
	 * Affecte le nom du Joueur
	 * @param nom nom à affecter
	 */
	public void   setNom(String nom) { this.nom     = nom;  }

	/**
	 * Affecte le score
	 * @param val nombre à affecter
	 */
	public void   setScore(int val)  { this.score   = val;  }

	public void setTour(boolean b) { this.tour = b; }

	public boolean getTour()       {return this.tour;}

	/**
	 * @return une phrase disant s'il reste des twist et si oui combien
	 */
	public String toString() {
		String s = "";

		s += "Le joueur "   + this.nom;
		s += " de couleur " + this.couleur;
		s += (this.nbTwist == 0) ? " n'a plus de Twist." : " a encore " + this.nbTwist + " Twist.";

		return s;
	}
}
