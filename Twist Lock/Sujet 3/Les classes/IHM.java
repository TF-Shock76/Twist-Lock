/**
 * Classe IHM.java
 * @author Equipe 1 : Théo Crauffon, Thibault Fouchet, Alexi Debonne, Olivier Jonquais, Benoit Klimczak
 * @version 1.0
 */

 public class IHM {


 	public IHM(){
 	}

 	/**
	 * @return l'affichage du plateau complet
	 */
	public String afficherPlateau(Conteneur[][] plateau) {
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

/*
	public String setSop(String message){
		String comm = " ";

		comm = message;

		
	}
	public String getSop()
	{
		return null;
	}
	 */
	
}
