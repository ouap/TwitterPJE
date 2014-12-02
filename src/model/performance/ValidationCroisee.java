package model.performance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationCroisee {
	List<List<String>> sousEnsembles;
	Map<String, Integer> reference;

	/**
	 * Initialisation de la liste des sous-ensemble et de la map référence
	 * 
	 * @param k
	 *            le nombre de sous-ensembles
	 */
	void initArray(int k) {
		sousEnsembles = new ArrayList<List<String>>(k);
		reference = new HashMap<String, Integer>();
		for (int i = 0; i < k; i++) {
			sousEnsembles.add(new ArrayList<String>());
		}
	}

	/**
	 * Création des sous-ensembles à partir de la base d'apprentissage
	 * 
	 * @param k
	 *            le nombre de sous ensemble
	 * @throws IOException
	 */
	void creerSousEnsembles(int k) throws IOException {
		int pos, neg, neutre;
		pos = neg = neutre = 0;

		initArray(k);

		String fichier = new java.io.File(".").getCanonicalPath()
				+ "/tweets/search.csv";
		InputStream ips = new FileInputStream(fichier);
		InputStreamReader ipsr = new InputStreamReader(ips);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(ipsr);
		String read;
		String[] ligne;

		while ((read = br.readLine()) != null) {
			ligne = read.split(";");

			switch (Integer.parseInt(ligne[5])) {
			case 0:

				if (neg >= k)
					neg = 0;
				reference.put(ligne[2], Integer.parseInt(ligne[5]));
				sousEnsembles.get(neg).add(ligne[2]);
				neg++;
				break;
			case 2:
				if (neutre >= k)
					neutre = 0;
				reference.put(ligne[2], Integer.parseInt(ligne[5]));
				sousEnsembles.get(neutre).add(ligne[2]);
				neutre++;
				break;
			case 4:
				if (pos >= k)
					pos = 0;
				reference.put(ligne[2], Integer.parseInt(ligne[5]));
				sousEnsembles.get(pos).add(ligne[2]);
				pos++;
				break;
			}

		}

	}

	int calculerTxErreur(int algo) {

		switch (algo) {
		case 1:

			break;

		case 2:

			break;
		case 3:

			break;
		}
		return 0;

	}

	public static void main(String[] args) throws IOException {
		new ValidationCroisee().creerSousEnsembles(10);
	}
}
