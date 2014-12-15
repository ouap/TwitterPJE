package model.performance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Model;
import model.TweetInfos;

public class ValidationCroisee {
	List<List<TweetInfos>> sousEnsembles;
	Map<TweetInfos, Integer> reference;

	/**
	 * Initialisation de la liste des sous-ensemble et de la map référence
	 *
	 * @param k
	 *            le nombre de sous-ensembles
	 */
	void initArray(int k) {
		sousEnsembles = new ArrayList<List<TweetInfos>>(k);
		reference = new HashMap<TweetInfos, Integer>();
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
		List<TweetInfos> base = Model.getBase();
		int neg, pos, neutre;
		neg = neutre = pos = 0;
		initArray(k);

		for (TweetInfos tweetInfos : base) {

			switch (tweetInfos.getNote()) {
			case 0:
				if (neg >= k)
					neg = 0;
				reference.put(tweetInfos, tweetInfos.getNote());
				sousEnsembles.get(neg).add(tweetInfos);
				neg++;
				break;
			case 2:
				if (neutre >= k)
					neutre = 0;
				reference.put(tweetInfos, tweetInfos.getNote());
				sousEnsembles.get(neutre).add(tweetInfos);
				neutre++;
				break;
			case 4:
				if (pos >= k)
					pos = 0;
				reference.put(tweetInfos, tweetInfos.getNote());
				sousEnsembles.get(pos).add(tweetInfos);
				pos++;
				break;
			}

		}
		for (List<TweetInfos> sous : sousEnsembles) {
			System.out.println("Taille " + sous.size());
		}

	}

	void calculerTxErreur(int k) throws IOException {
		int classeKnn, classeBayes, classeBayesBiG, classePosNeg;
		int errKnn, errBayes, errBayesBiG, errPosNeg;
		errBayes = errBayesBiG = errKnn = errPosNeg = 0;

		try {
			creerSousEnsembles(k);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < k; i++) {
			List<TweetInfos> baseCalcul = concatEnsemble(i);
			System.out.println("----------------------------------");
			System.out.println("NOTATION SOUS-ENSEMBLE " + (i + 1));
			for (TweetInfos tweetcourrant : sousEnsembles.get(i)) {
				classeKnn = Model.knn(tweetcourrant.getTweet(), 30, baseCalcul);
				classePosNeg = Model.getClassePosNeg(tweetcourrant.getTweet());
				// classeBayes = classifBayes.classifierBayes(fichier, tweet,
				// classif);
				// classeBayesBiG =
				// classifBayesBiGramme.classifierBayesBiGramme(fichier, tweet,
				// classif);
				System.out.println("Note KNN -> " + classeKnn
						+ ", Note PosNeg -> " + classePosNeg + " Note ref : "
						+ reference.get(tweetcourrant));

				if (classeKnn != reference.get(tweetcourrant)) {
					errKnn++;
				} else {
					System.out.println("	KNN OK");
				}

				if (classePosNeg != reference.get(tweetcourrant)) {
					errPosNeg++;
				} else {
					System.out.println("	Pos/Neg OK");
				}

			}
		}

		System.out.println("Nb erreurs knn : " + errKnn + "/"
				+ reference.size() + "\nNb erreurs Pos/Neg : " + errPosNeg
				+ "/" + reference.size());
	}

	/**
	 * Fait l'union de tout les sous-ensembles sauf celui qui doit être noté
	 * 
	 * @param exception
	 *            indice du sous-ensemble à ne pas prendre en compte
	 * @return la liste de tweet des sous-ensembles concaténés
	 */
	public List<TweetInfos> concatEnsemble(int exception) {
		List<TweetInfos> concat = new ArrayList<TweetInfos>();
		for (int i = 0; i < sousEnsembles.size(); i++) {
			if (i != exception) {
				concat.addAll(sousEnsembles.get(i));
			}
		}

		System.out.println("Taille BaseUnion : " + concat.size());
		return concat;
	}

	public static void main(String[] args) throws IOException {
		Model.chargerBaseTweet();
		new ValidationCroisee().calculerTxErreur(10);
	}
}
