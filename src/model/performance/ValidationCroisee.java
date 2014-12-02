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

import model.Model;
import model.TweetInfos;

public class ValidationCroisee {
	List<List<TweetInfos>> sousEnsembles;
	Map<String, Integer> reference;

	/**
	 * Initialisation de la liste des sous-ensemble et de la map référence
	 *
	 * @param k
	 *            le nombre de sous-ensembles
	 */
	void initArray(int k) {
		sousEnsembles = new ArrayList<List<TweetInfos>>(k);
		reference = new HashMap<String, Integer>();
		for (int i = 0; i < k; i++) {
			sousEnsembles.add(new ArrayList<TweetInfos>());
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
				+ "/tweets/base.csv";
		InputStream ips = new FileInputStream(fichier);
		InputStreamReader ipsr = new InputStreamReader(ips);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(ipsr);
		String read;
		String[] ligne;

		while ((read = br.readLine()) != null) {
			ligne = read.split(";");
			TweetInfos tweet = new TweetInfos(Long.parseLong(ligne[0]),
					ligne[1], ligne[2], ligne[3], ligne[4],
					Integer.parseInt(ligne[5]));
			switch (Integer.parseInt(ligne[5])) {
			case 0:

				if (neg >= k)
					neg = 0;
				reference.put(ligne[2], Integer.parseInt(ligne[5]));
				sousEnsembles.get(neg).add(tweet);
				neg++;
				break;
			case 2:
				if (neutre >= k)
					neutre = 0;
				reference.put(ligne[2], Integer.parseInt(ligne[5]));
				sousEnsembles.get(neutre).add(tweet);
				neutre++;
				break;
			case 4:
				if (pos >= k)
					pos = 0;
				reference.put(ligne[2], Integer.parseInt(ligne[5]));
				sousEnsembles.get(pos).add(tweet);
				pos++;
				break;
			}

		}
		for (List<TweetInfos> sous : sousEnsembles) {
			System.out.println("Taille " + sous.size());
		}

	}

	/**
	 * @param k
	 * @return
	 */
	int calculerTxErreur(int k) {
		List<TweetInfos> courrant;
		List<TweetInfos> apprentissage;
		int classeknn, classePosNeg, classeBayes;
		int errKnn = 0;
		int errBayes = 0;
		int errPosNeg = 0;
		try {
			creerSousEnsembles(k);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < k; i++) {
			courrant = sousEnsembles.get(i);
			apprentissage = concatLists(i);
			for (TweetInfos string : courrant) {
				try {
					classeknn = Model.knn(string.getTweet(), 30, apprentissage);
					classePosNeg = Model.getClassePosNeg(string.getTweet());
					System.out.println("Knn : noteAttribuée -> " + classeknn
							+ ", PosNeg : noteAttribuée -> " + classePosNeg
							+ " note ref -> "
							+ reference.get(string.getTweet()));

					if (classeknn != reference.get(string.getTweet())) {
						errKnn++;
					} else {
						System.out.println("OK knn ");
					}
					if (classePosNeg != reference.get(string.getTweet())) {
						errPosNeg++;
					} else {
						System.out.println("OK Pos/Neg ");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		System.out.println("nb erreurs KNN : " + errKnn
				+ " nb erreurs POSNEG : " + errPosNeg);
		return 0;

	}

	/**
	 * @param i
	 * @return
	 */
	private List<TweetInfos> concatLists(int i) {
		List<TweetInfos> result = new ArrayList<TweetInfos>();
		for (int j = 0; j < sousEnsembles.size(); j++) {
			if (j != i)
				result.addAll(sousEnsembles.get(j));
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		new ValidationCroisee().calculerTxErreur(10);
	}
}
