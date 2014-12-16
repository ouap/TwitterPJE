package classification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class classifKeyword {

	/* CLASSIFICATION PAR RAPPORT AUX FICHIER .TXT (MOTS POSITIFS/NEGATIFS) */
	/**
	 * Retourne la classe d'un tweet en fonction du nb de mot positif/négatif
	 *
	 * @param tweet
	 *            Le tweet à classer
	 * @return la classe du tweet
	 */
	public static int getClassePosNeg(String tweet) {
		/* Annotation */
		int positif = motsPositifs(tweet);
		int negatif = motsNegatifs(tweet);

		int result = positif - negatif;
		if (result > 0) {
			return 4;
		} else {
			if (result < 0) {
				return 0;
			} else {
				return 2;
			}
		}
	}

	/**
	 * Calcule le nombre de mots positifs d'un tweet
	 *
	 * @param tweet
	 *            le tweet à tester
	 *
	 * @return le nombre de mots positifs du tweet
	 */
	private static int motsPositifs(String tweet) {
		int positif = 0;
		try {
			String chemin = new java.io.File(".").getCanonicalPath()
					+ "/tweets/positive.txt";
			InputStream ips = new FileInputStream(chemin);
			InputStreamReader ipsr = new InputStreamReader(ips);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			String[] mots = null;

			while ((ligne = br.readLine()) != null) {
				mots = ligne.split(", ");

				for (String mot6 : mots) {

					String mot = mot6.concat(" ");

					/*
					 * A supprimer ??
					 */
					String mot1 = mot6.concat(",");
					String mot2 = mot6.concat(":");
					String mot3 = mot6.concat(".");
					String mot4 = mot6.concat("!");
					String mot5 = mot6.concat("?");

					if (tweet.contains(mot) || tweet.contains(mot1)
							|| tweet.contains(mot2) || tweet.contains(mot3)
							|| tweet.contains(mot4) || tweet.contains(mot5)) {
						// System.out.println(mots[i]);
						positif++;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return positif;
	}

	/**
	 * Calcule le nombre de mots négatifs d'un tweet
	 *
	 * @param tweet
	 *            le tweet à tester
	 * @return le nombre de mots négatifs de ce tweet
	 */
	private static int motsNegatifs(String tweet) {
		int negatif = 0;
		try {
			String chemin = new java.io.File(".").getCanonicalPath()
					+ "/tweets/negative.txt";
			InputStream ips = new FileInputStream(chemin);
			InputStreamReader ipsr = new InputStreamReader(ips);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			String[] mots = null;

			while ((ligne = br.readLine()) != null) {
				mots = ligne.split(", ");
				for (String mot6 : mots) {
					String mot = mot6.concat(" ");

					/*
					 * A supprimer ??
					 */
					String mot1 = mot6.concat(",");
					String mot2 = mot6.concat(":");
					String mot3 = mot6.concat(".");
					String mot4 = mot6.concat("!");
					String mot5 = mot6.concat("?");

					if (tweet.contains(mot) || tweet.contains(mot1)
							|| tweet.contains(mot2) || tweet.contains(mot3)
							|| tweet.contains(mot4) || tweet.contains(mot5)) {
						// System.out.println(mots[i]);
						negatif++;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return negatif;
	}

}
