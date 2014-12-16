package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import view.View;
import classification.ClassifBayes;
import classification.ClassifBayesBiGramme;
import classification.ClassifKnn;
import classification.classifKeyword;

/**
 * Model de l'application
 * 
 * @author sais poux
 *
 */
public class Model extends Observable {
	private static List<TweetInfos> listTweets;
	private static List<TweetInfos> base;

	public enum Classe {
		POSITIF, NEGATIF, NEUTRE
	};

	private static List<TweetInfos> LIST_TWEET_POS;
	private static List<TweetInfos> LIST_TWEET_NEG;
	private static List<TweetInfos> LIST_TWEET_NEUTRE;

	public String rateLimit;
	public String recherche;

	/**
	 * @return la liste de tweet de la derniere recherche effectuée
	 */
	public static List<TweetInfos> getListTweets() {
		return listTweets;
	}

	/**
	 * Set la liste de tweet avec le parametre passé en argument
	 * 
	 * @param listTweets
	 *            nouvelle liste de tweets
	 */
	public void setListTweets(List<TweetInfos> listTweets) {
		this.listTweets = listTweets;
	}

	/**
	 * @return la liste de tweets de la base d'apprentissage
	 */
	public static List<TweetInfos> getBase() {
		return base;
	}

	public void proxyHandler(int choix) throws IOException {

		String fichier = new java.io.File(".").getCanonicalPath()
				+ "/twitter4j.properties";
		InputStream ips = new FileInputStream(fichier);
		InputStreamReader ipsr = new InputStreamReader(ips);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(ipsr);
		String read;
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 6; i++) {
			read = br.readLine();
			sb.append(read + "\n");
		}
		br.close();
		BufferedWriter out = new BufferedWriter(new FileWriter(fichier));
		if (choix == 0) {
			out.write(sb.toString());
		} else {
			sb.append("http.proxyHost=cacheserv.univ-lille1.fr\nhttp.proxyPort=3128");
			out.write(sb.toString());
		}
		out.close();
	}

	public static List<TweetInfos> getTweetByClasse(Classe classe) {
		switch (classe) {
		case POSITIF:
			return LIST_TWEET_POS;

		case NEGATIF:
			return LIST_TWEET_NEG;

		case NEUTRE:
			return LIST_TWEET_NEUTRE;
		}
		return null;
	}

	/* FONCTIONS D'APPEL A L'API */

	/**
	 * Retourne la limite d'appel à l'API Twitter restante
	 *
	 * @return La limite d'appel restante
	 * @throws TwitterException
	 */
	public String getLimit() throws TwitterException {
		Twitter twitter = new TwitterFactory().getInstance();
		String limit, remaining;
		RateLimitStatus status = twitter.getRateLimitStatus().get(
				"/search/tweets");
		limit = Integer.toString(status.getLimit());
		remaining = Integer.toString(status.getRemaining());

		return "Limite : " + remaining + "/" + limit;
	}

	/**
	 * Méthode qui réalise une recherche de tweet à l'aide de l'API
	 *
	 * @param keyWord
	 *            le mot clé de la recherche à effectuer
	 */
	public void doSearch(String keyWord) {
		Twitter twitter = new TwitterFactory().getInstance();
		List<Status> listStatus;
		listTweets = new ArrayList<TweetInfos>();
		try {
			Query query = new Query(keyWord);
			query.resultType(ResultType.mixed);
			query.setLang("fr");
			query.count(50);
			QueryResult result = twitter.search(query);

			listStatus = result.getTweets();

			for (Status status : listStatus) {
				listTweets.add(new TweetInfos(status.getId(), status.getUser()
						.getName(), nettoyerTweet(status.getText()), status
						.getCreatedAt().toString(), keyWord, -1));

			}
			setChanged();
			notifyObservers();

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de charger les tweets de la base d'apprentissage dans les deux
	 * listes base et ListTweet (affichage), et charge les tweets dans 3 autres
	 * listes en fonction de leur classe
	 *
	 */
	public static void chargerBaseTweet() {
		base = new ArrayList<TweetInfos>();
		listTweets = new ArrayList<TweetInfos>();
		LIST_TWEET_NEG = new ArrayList<TweetInfos>();
		LIST_TWEET_NEUTRE = new ArrayList<TweetInfos>();
		LIST_TWEET_POS = new ArrayList<TweetInfos>();

		try {
			String fichier = new java.io.File(".").getCanonicalPath()
					+ "/tweets/base.csv";
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(ipsr);
			String read;
			while ((read = br.readLine()) != null) {
				String[] ligne = read.split(";");
				// Création du tweet
				TweetInfos tweet = new TweetInfos(Long.parseLong(ligne[0]),
						ligne[1], ligne[2], ligne[3], ligne[4],
						Integer.parseInt(ligne[5]));
				// Ajout à la liste base d'apprentissage
				base.add(tweet);
				// Ajout à la list de tweets pour l'affichage
				listTweets.add(tweet);

				switch (tweet.getNote()) {
				case 0:
					LIST_TWEET_NEG.add(tweet);
					break;

				case 2:
					LIST_TWEET_NEUTRE.add(tweet);
					break;

				case 4:
					LIST_TWEET_POS.add(tweet);
					break;
				}

			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	public void noter(List<TweetInfos> listeTweets, int algo)
			throws IOException {

		for (TweetInfos tweet : listTweets) {
			// Nettoyage du tweet + récupération de sa classe
			String text = nettoyerTweet(tweet.getTweet());
			int classe = 0;

			switch (algo) {
			case 1:
				classe = classifKeyword.getClassePosNeg(text);
				break;
			case 2:
				classe = ClassifKnn.knn(text, 30, base);
				break;
			case 3:
				classe = ClassifBayes.classifierBayes(listTweets, text, 0);
				break;
			case 4:
				classe = ClassifBayes.classifierBayes(listTweets, text, 1);
				break;
			case 5:
				classe = ClassifBayesBiGramme.classifierBayesBiGramme(
						listTweets, text, 1);
				break;
			case 6:
				classe = ClassifBayesBiGramme.classifierBayesBiGramme(
						listTweets, text, 0);
				break;
			}
			tweet.setNote(classe);
		}
	}

	/* FONCTIONS POUR LA SAUVEGARDE */
	/**
	 * Methode qui sauvegarde les tweets recherchés dans un csv aprés les avoir
	 * néttoyé et classés
	 *
	 * @param listTweet
	 *            la liste des tweets de la recherche
	 * @param algo
	 *            l'algo à utiliser pour la classification
	 */
	public void save() {

		try {
			FileWriter writer = new FileWriter(
					new java.io.File(".").getCanonicalPath()
							+ "/tweets/search.csv", true);

			for (TweetInfos tweet : listTweets) {
				if (!alreadyIn(tweet.getId())) {
					// Nettoyage du tweet + récupération de sa classe

					// Ecriture
					writer.write(tweet.getId() + ";" + tweet.getUser() + ";"
							+ tweet.getTweet() + ";" + tweet.getDate() + ";"
							+ tweet.getSearch() + ";" + tweet.getNote() + "\n");
				}
			}

			writer.close();

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Méthode qui test si un tweet fait déjà parti d'un fichier csv à l'aide de
	 * son id
	 *
	 * @param id
	 *            L'id du tweet à tester
	 * @return vrai si le tweet est déjà dans le fichier csv, faux sinon
	 */
	public boolean alreadyIn(long id) {
		try {
			String chemin = new java.io.File(".").getCanonicalPath()
					+ "/tweets/base.csv";
			@SuppressWarnings("resource")
			BufferedReader fichier_source = new BufferedReader(new FileReader(
					chemin));
			String chaine;

			try {
				while ((chaine = fichier_source.readLine()) != null) {
					String[] tabChaine = chaine.split(";");
					if (tabChaine[0].equals(Long.toString(id)))
						return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fichier_source.close();
		} catch (FileNotFoundException e) {
			System.out.println("Le fichier est introuvable !");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * @param fichier
	 * @param copy
	 * @throws IOException
	 */
	public String nettoyerTweet(String tweet) throws IOException {

		/* Nettoyage */
		String l0 = Pattern.compile("@[a-zA-Z0-9]*( | : | :|: |:)")
				.matcher(tweet).replaceAll("@");
		String l1 = Pattern.compile("@|#|RT |URL").matcher(l0).replaceAll("");
		String l2 = Pattern.compile("(http|https)://([^; ])*").matcher(l1)
				.replaceAll("");
		String l3 = Pattern.compile("(http|https)://").matcher(l2)
				.replaceAll("");
		String l4 = Pattern.compile("(http|https):/").matcher(l3)
				.replaceAll("");
		String l5 = Pattern.compile("(http|https):").matcher(l4).replaceAll("");
		String l6 = Pattern.compile("(http|https)").matcher(l5).replaceAll("");

		/*
		 * J'ai supprimé toute la ponctuation
		 */

		String l7 = Pattern.compile(" \\. | \\.|\\. ").matcher(l6)
				.replaceAll(" ");
		String l8 = Pattern.compile(" ! | !|! ").matcher(l7).replaceAll(" ");
		String l9 = Pattern.compile(" \\? |\\? | \\?").matcher(l8)
				.replaceAll(" ");
		String l10 = Pattern.compile(" : | :|: ").matcher(l9).replaceAll(" ");
		String l11 = Pattern.compile("é|è|ê").matcher(l10).replaceAll("e");
		String l12 = Pattern.compile("ù").matcher(l11).replaceAll("u");
		String l13 = Pattern.compile("à").matcher(l12).replaceAll("a");
		String l14 = Pattern.compile("ô").matcher(l13).replaceAll("o");
		String cleanTweet = Pattern.compile(" , | ,|, ").matcher(l14)
				.replaceAll(" ");

		return cleanTweet.toLowerCase();
	}

	public static void main(String[] args) throws IOException {
		new View(new Model());

	}

}
