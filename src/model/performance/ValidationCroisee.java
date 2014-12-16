package model.performance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Model;
import model.Model.Classe;
import model.TweetInfos;
import view.BarChart;
import view.PieChart;
import classification.ClassifBayes;
import classification.ClassifKnn;
import classification.classifKeyword;

public class ValidationCroisee {
	List<List<TweetInfos>> sousEnsembles;
	Map<TweetInfos, Integer> reference;
	Map<String, Float> dataBase;
	Map<String, Integer> dataBayes;
	Map<String, Integer> dataKnnPosNeg;

	public ValidationCroisee() {
		dataBase = new HashMap<String, Float>();
		dataKnnPosNeg = new HashMap<String, Integer>();
		dataBayes = new HashMap<String, Integer>();
		Model.chargerBaseTweet();
		try {
			calculerTxErreur(10);
		} catch (IOException e) {
			System.out.println("Erreur lors l'analyse du nombre d'erreurs");
		}
		createRatioBase();
		afficherRatioBase(dataBase);
		afficherBayesData(dataBayes);
		afficherKnnData(dataKnnPosNeg);
	}

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

	/**
	 * Fonction qui calcule le taux d'erreur de chaque algo en pourcentage
	 * 
	 * @param k
	 *            le nombre de sous-ensemble
	 * @throws IOException
	 */
	void calculerTxErreur(int k) throws IOException {
		int classeKnn, classeBayesUniFreq, classeBayesUniPres, classeBayesBiGFreq, classeBayesBiGPres, classePosNeg;
		int errKnn, errBayesUniFreq, errBayesUniPres, errBayesBigFreq, errBayesBigPres, errPosNeg;
		errBayesUniFreq = errBayesBigFreq = errBayesBigPres = errBayesUniPres = errKnn = errPosNeg = 0;

		try {
			creerSousEnsembles(k);
		} catch (IOException e) {
			System.out.println("Erreur lors de la creation des sous ensembles");
		}

		for (int i = 0; i < k; i++) {
			List<TweetInfos> baseCalcul = concatEnsemble(i);

			for (TweetInfos tweetcourrant : sousEnsembles.get(i)) {
				classeKnn = ClassifKnn.knn(tweetcourrant.getTweet(), 30,
						baseCalcul);

				classePosNeg = classifKeyword.getClassePosNeg(tweetcourrant
						.getTweet());

				classeBayesUniPres = ClassifBayes.classifierBayes(baseCalcul,
						tweetcourrant.getTweet(), 0);
				/*
				 * classeBayesUniFreq = ClassifBayes.classifierBayes(baseCalcul,
				 * tweetcourrant.getTweet(), 1); classeBayesBiGPres =
				 * ClassifBayesBiGramme .classifierBayesBiGramme(baseCalcul,
				 * tweetcourrant.getTweet(), 0); classeBayesBiGFreq =
				 * ClassifBayesBiGramme .classifierBayesBiGramme(baseCalcul,
				 * tweetcourrant.getTweet(), 1);
				 */

				// Verification de la notation de chaque classifieur
				if (classeKnn != reference.get(tweetcourrant)) {
					errKnn++;
				}

				if (classePosNeg != reference.get(tweetcourrant)) {
					errPosNeg++;
				}
				if (classeBayesUniPres != reference.get(tweetcourrant)) {
					errBayesUniPres++;

				}
				/*
				 * if (classeBayesUniFreq != reference.get(tweetcourrant)) {
				 * errBayesUniFreq++;
				 * 
				 * } if (classeBayesBiGFreq != reference.get(tweetcourrant)) {
				 * errBayesBigFreq++; } if (classeBayesBiGPres !=
				 * reference.get(tweetcourrant)) { errBayesBigPres++; }
				 */

			}
		}

		// Enregistrement des données
		dataBayes.put("UnigrammeFreq",
				(int) (((float) errBayesUniFreq / reference.size()) * 100));
		dataBayes.put("UnigrammePres",
				(int) (((float) errBayesUniPres / reference.size()) * 100));
		dataBayes.put("BigrammeFreq",
				(int) (((float) errBayesBigFreq / reference.size()) * 100));
		dataBayes.put("BigrammePres",
				(int) (((float) errBayesBigPres / reference.size()) * 100));

		dataKnnPosNeg.put("Knn",
				(int) (((float) errKnn / reference.size()) * 100));
		dataKnnPosNeg.put("Pos/Neg",
				(int) (((float) errPosNeg / reference.size()) * 100));

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

	/**
	 * Calcule le ratio des tweets positifs, negatifs et neutres de la base
	 * d'apprentissage
	 */
	public void createRatioBase() {
		float pos, neg, neutre;
		int total = Model.getBase().size();

		pos = ((float) Model.getTweetByClasse(Classe.POSITIF).size() / total) * 100;
		neg = ((float) Model.getTweetByClasse(Classe.NEGATIF).size() / total) * 100;
		neutre = ((float) Model.getTweetByClasse(Classe.NEUTRE).size() / total) * 100;
		dataBase.put("Positif", pos);
		dataBase.put("Negatif", neg);
		dataBase.put("Neutre", neutre);

	}

	/**
	 * Affiche le ratio de la base d'apprentissage
	 * 
	 * @param ratioBase
	 */
	public void afficherRatioBase(Map<String, Float> ratioBase) {
		new PieChart("Ratio Tweets Base", ratioBase);
	}

	/**
	 * Affiche les donnnées concernant les algorithmes Bayésiens
	 * 
	 * @param dataBayes
	 *            les données des test effectués sur les algorithmes bayésiens
	 * 
	 */
	public void afficherBayesData(Map<String, Integer> dataBayes) {
		new BarChart("Statistiques Bayes", dataBayes);
	}

	/**
	 * Affiche les donnnées concernant les algorithmes Knn et Keyword
	 * 
	 * @param dataKnnKeyword
	 *            les données des test effectués sur les algorithmes knn et
	 *            keyword
	 */
	public void afficherKnnData(Map<String, Integer> dataKnnKeyword) {
		new BarChart("Statistiques Knn & KeyWord", dataKnnKeyword);
	}

}
