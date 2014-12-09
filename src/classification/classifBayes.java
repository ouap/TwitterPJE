package classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class classifBayes {

	/**
	 * Fonction qui permet de mettre a � la puissance b
	 * 
	 * @param a
	 * @param b
	 * @return a puissance b
	 */
	public static float myPow(float a, float b) {
		float res = 1;
		for (int i = 0; i < b; i++) {
			res *= a;
		}
		return res;
	}

	/**
	 * Calcule les trois .csv negatifs, positifs et neutre et renvoie le tableau
	 * contenant le nombre de mots positifs, negatifs et neutres et le nombre de
	 * tweets positifs, negatifs et neutres.
	 * 
	 * @param fichier
	 * @return
	 * @throws IOException
	 */
	public static float[] nbTweetsMood(String fichier) throws IOException {
		float res[] = new float[6];
		float pos = 0; // nombre de tweets positifs
		float nbMotsPos = 0; // nombre de mots positifs en tout
		float neg = 0; // nombre de tweets negatifs
		float nbMotsNeg = 0; // nombre de mots negatifs en tout
		float neutre = 0; // nombre de tweets neutres
		float nbMotsNeutre = 0; // nombre de mots neutres en tout
		FileWriter fwPos = new FileWriter("positifs.csv", false);
		FileWriter fwNeg = new FileWriter("negatifs.csv", false);
		FileWriter fwNeutres = new FileWriter("neutres.csv", false);

		try {
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			BufferedWriter outputPos = new BufferedWriter(fwPos);
			BufferedWriter outputNeg = new BufferedWriter(fwNeg);
			BufferedWriter outputNeutres = new BufferedWriter(fwNeutres);
			while ((ligne = br.readLine()) != null) {
				String tab[] = ligne.split(";");
				String sp[] = tab[2].split(" ");
				if (Integer.parseInt(tab[5]) == 4) {
					outputPos.write(ligne + "\n");
					outputPos.flush();
					pos++;
					nbMotsPos += sp.length;
				}
				if (Integer.parseInt(tab[5]) == 2) {
					outputNeutres.write(ligne + "\n");
					outputNeutres.flush();
					neutre++;
					nbMotsNeutre += sp.length;
				}
				if (Integer.parseInt(tab[5]) == 0) {
					outputNeg.write(ligne + "\n");
					outputNeg.flush();
					neg++;
					nbMotsNeg += sp.length;
				}
			}
			br.close();
			outputPos.close();
			outputNeutres.close();
			outputNeg.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		res[0] = neg;
		res[1] = nbMotsNeg;
		res[2] = neutre;
		res[3] = nbMotsNeutre;
		res[4] = pos;
		res[5] = nbMotsPos;
		return res;
	}

	/**
	 * Calcule le tableau de tous les mots presents dans une base
	 * d'apprentissage sans repetition (negatifs, positifs ou neutre)
	 * 
	 * @param fichier
	 *            le fichier de base
	 * @param fichier2
	 *            la base d'apprentissage (negatifs, positifs ou neutre)
	 * @return mots
	 * @throws IOException
	 */
	public static String[] tabMotsMood(String fichier, String fichier2)
			throws IOException {
		int tab = 0;
		if (fichier2.equals("positifs.csv")) {
			tab = Math.round(nbTweetsMood(fichier)[5]);
		} else {
			if (fichier2.equals("negatifs.csv")) {
				tab = Math.round(nbTweetsMood(fichier)[1]);
			} else {
				if (fichier2.equals("neutres.csv")) {
					tab = Math.round(nbTweetsMood(fichier)[3]);
				}
			}
		}
		String res[] = new String[tab];
		int k = 0;
		for (int i = 0; i < tab; i++) {
			res[i] = null;
		}
		try {
			InputStream ips = new FileInputStream(fichier2);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine()) != null) {
				String sp1[] = ligne.split(";");
				String sp[] = sp1[2].split(" ");
				for (String element : sp) {
					int alreadyPresent = 0;
					for (int j = 0; j < k; j++) {
						if (element.equals(res[j])) {
							alreadyPresent = 1;
						}
					}
					if (alreadyPresent == 0) {
						res[k] = element;
						k++;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		int cpt = 0;

		while (((cpt + 1) < res.length) && (res[cpt + 1] != null)) {
			cpt++;
		}

		String[] mots = new String[cpt];
		for (int i = 0; i < cpt; i++) {
			mots[i] = res[i];
		}
		return mots;
	}

	/**
	 * Pour un tableau deja calcule avec les mots presents dans les tweets, on
	 * calcule pour chacun d'entre eux le nombre d'occurrences dans la base
	 * d'apprentissage
	 * 
	 * @param mots
	 *            le tableau avec les mots presents
	 * @param fichier
	 *            la base d'apprentissage (soit negatifs, positifs ou neutres)
	 * @return
	 */
	public static float[] nbMots(String[] mots, String fichier) {
		float nbMots[] = new float[mots.length];
		for (int i = 0; i < mots.length; i++) {
			nbMots[i] = 0;
		}
		try {
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine()) != null) {
				String sp1[] = ligne.split(";");
				String sp[] = sp1[2].split(" ");
				for (String element : sp) {
					for (int j = 0; j < mots.length; j++) {
						if (element.equals(mots[j])) {
							nbMots[j]++;
						}
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return nbMots;
	}

	public static float nbMotsTotalMood(float[] nbMots) {
		float nbTot = 0;
		for (float nbMot : nbMots) {
			nbTot += nbMot;
		}
		return nbTot;
	}

	public static String[] tweetToTab(String tweet) {
		String[] tab = tweet.split(" ");
		if (tweet.length() == 0) {
			return tab;
		}
		int cpt = 1;
		int tmp;
		int pos = 1;
		for (int i = 1; i < tab.length; i++) {
			tmp = 0;
			for (int j = 0; j < i; j++) {
				if (tab[i].equals(tab[j])) {
					tmp = 1;
				}
			}
			if (tmp == 0) {
				cpt++;
			}
		}
		String motsTweet[] = new String[cpt];
		motsTweet[0] = tab[0];
		for (int k = 1; k < tab.length; k++) {
			tmp = 0;
			for (int l = 0; l < pos; l++) {
				if (motsTweet[l].equals(tab[k])) {
					tmp = 1;
				}
			}
			if (tmp == 0) {
				motsTweet[pos] = tab[k];
				pos++;
			}
		}
		return motsTweet;
	}

	public static float[] nbMotstweet(String tweet) {
		float cpt;
		String[] motsTweet = tweetToTab(tweet);
		float[] nbMots = new float[motsTweet.length];
		String[] sp = tweet.split(" ");
		for (int i = 0; i < motsTweet.length; i++) {
			cpt = 0;
			for (String element : sp) {
				if (element.equals(motsTweet[i])) {
					cpt += 1.0;
				}
			}
			nbMots[i] = cpt;
		}
		return nbMots;
	}

	public static float probaTweetMood(String fichier, String fichierMood,
			String tweet, int classif) throws IOException {
		float nbTweetsMood[] = nbTweetsMood(fichier);
		float nbTweets = nbTweetsMood[0] + nbTweetsMood[2] + nbTweetsMood[4];
		String[] motsMood = tabMotsMood(fichier, fichierMood);
		float nbMotsTot[] = nbMots(motsMood, fichier);
		float nbMotsMood[] = nbMots(motsMood, fichierMood);
		float nbMotsTotMood = nbMotsTotalMood(nbMotsMood);
		String[] motsTweet = tweetToTab(tweet);
		float[] nbMotsTweet = nbMotstweet(tweet);
		float probaMood = 0;
		if (fichierMood.equals("positifs.csv")) {
			probaMood = nbTweetsMood[4] / nbTweets;
		} else {
			if (fichierMood.equals("negatifs.csv")) {
				probaMood = nbTweetsMood[0] / nbTweets;
			} else {
				if (fichierMood.equals("neutres.csv")) {
					probaMood = nbTweetsMood[2] / nbTweets;
				}
			}
		}
		float proba = 0;
		for (int i = 0; i < motsTweet.length; i++) {
			if (motsTweet[i].length() > 3) {
				for (int j = 0; j < motsMood.length; j++) {
					float probaMot = 0;
					if (motsMood[j].equals(motsTweet[i])) {
						if (motsMood[j].length() > 3) {
							if (proba == 0) {
								proba = 1;
							}
							if (classif == 0) {
								/* Presence */
								probaMot = (nbMotsMood[j] + 1)
										/ (nbMotsTot[j] + nbMotsTotMood);
							} else {
								/* Frequence */
								probaMot = myPow(
										(nbMotsMood[j] / nbMotsTot[j]),
										nbMotsTweet[i]);

							}
						}
					}
					if (probaMot != 0) {
						proba *= probaMot;
					}
				}
			}
		}
		proba *= probaMood;
		return proba;
	}

	/**
	 * Classification de bayes, classif sera mis � 0 pour la classification par
	 * pr�sence, � 1 pour la classification par fr�quence
	 * 
	 * @param fichier
	 * @param tweet
	 * @param classif
	 * @return
	 * @throws IOException
	 */

	public static int classifierBayes(String fichier, String tweet, int classif)
			throws IOException {
		float probaPos = probaTweetMood(fichier, "positifs.csv", tweet, classif);
		System.out.println("positifs : " + probaPos);
		float probaNeg = probaTweetMood(fichier, "negatifs.csv", tweet, classif);
		System.out.println("Negatifs : " + probaNeg);
		float probaNeutre = probaTweetMood(fichier, "neutres.csv", tweet,
				classif);
		System.out.println("Neutres : " + probaNeutre);
		if (probaPos > probaNeg && probaPos > probaNeutre) {
			return 4;
		} else {
			if (probaNeg > probaNeutre && probaNeg > probaPos) {
				return 0;
			} else
				return 2;
		}
	}
}
