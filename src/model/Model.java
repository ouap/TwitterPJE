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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import classification.classifBayes;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
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
	public List<TweetInfos> getListTweets() {
		return listTweets;
	}

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
						.getName(), status.getText(), status.getCreatedAt()
						.toString(), keyWord, -1));

			}
			setChanged();
			notifyObservers();

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
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
				classe = getClassePosNeg(text);
				break;
			case 2:
				classe = knn(text, 30, base);
				break;
			case 3:
				classe = new classifBayes().classifierBayes(listTweets,
						LIST_TWEET_POS, LIST_TWEET_NEG, LIST_TWEET_NEUTRE,
						text, 0); /* A FINIR POUR TESTER LES DIFFERENTES CLASSIF ! */
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
	public void save(List<TweetInfos> listTweets, int algo) {
		int i = 0;

		try {
			FileWriter writer = new FileWriter(
					new java.io.File(".").getCanonicalPath()
							+ "/tweets/search.csv", true);

			for (TweetInfos tweet : listTweets) {
				if (!alreadyIn(tweet.getId())) {
					// Nettoyage du tweet + récupération de sa classe
					String text = nettoyerTweet(tweet.getTweet());
					int classe = 0;

					switch (algo) {
					case 1:
						classe = knn(text, 30, base);
						break;
					case 2:
						classe = 2;
						break;
					case 3:
						classe = getClassePosNeg(text);
						break;
					}

					// Ecriture
					writer.write(tweet.getId() + ";" + tweet.getUser() + ";"
							+ text + ";" + tweet.getDate() + ";"
							+ tweet.getSearch() + ";" + classe + "\n");
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
		String cleanTweet = Pattern.compile(" , | ,|, ").matcher(l10)
				.replaceAll(" ");

		return cleanTweet.toLowerCase();
	}

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
	public static int motsPositifs(String tweet) {
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
	public static int motsNegatifs(String tweet) {
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

	/* FONCTIONS POUR KNN */

	/**
	 * Retourne la distance entre deux tweets
	 *
	 * @param t1
	 *            le tweet 1
	 * @param t2
	 *            le tweet 2
	 * @return la distance entre les tweets t1 et t2
	 */
	public static int distanceTweet(String t1, String t2) {
		int nbTotal = 0;
		int nbMotsCommuns = 0;
		String[] tab1 = t1.split(" ");
		String[] tab2 = t2.split(" ");
		nbTotal = tab1.length + tab2.length;

		for (String element : tab1) {
			for (String element2 : tab2) {
				if (element.equals(element2)) {
					nbMotsCommuns++;
				}
			}
		}

		// System.out.println(nbMotsCommuns);
		return ((nbTotal - nbMotsCommuns) / nbTotal);
	}

	/**
	 * Compare la distance d'un nouveau tweet avec le tweet de référence avec
	 * celles de tout les voisins les plus proches
	 *
	 * @param distanceTweet
	 *            la distance du nouveau tweet
	 * @param distanceVoisins
	 *            Map contenant les couples tweets/distances des k tweets les
	 *            plus proches du tweet de reference
	 * @return null si le nouveau tweet à une distance plus élevée que les
	 *         autres, le voisin à remplacer sinon
	 */
	private static String compareDistancesVoisins(int distanceTweet,
			Map<String, Integer> distanceVoisins) {
		int max = 0;
		String plusEloigne = null;

		for (Entry<String, Integer> entry : distanceVoisins.entrySet()) {
			String key = entry.getKey();
			int distance = entry.getValue();
			if (max < distance) {
				max = distance;
				plusEloigne = key;
			}
		}

		if (max > distanceTweet) {
			return plusEloigne;
		}

		return null;
	}

	/**
	 * Retourner la classe à associer au tweet à classer
	 *
	 * @param voisins
	 *            map contenant le tweet et la classe des k plus proches voisins
	 *            du tweet à classer
	 * @return la classe à associer au tweet à classer
	 */
	public static int vote(Map<String, Integer> voisins) {
		int classe0, classe2, classe4, max;
		classe0 = classe2 = classe4 = 0;

		// On calcule la nombre d'apparition de chaque classe
		for (Entry<String, Integer> entry : voisins.entrySet()) {
			// System.out.println("Voisin : " + entry.getKey() + "Note: "+
			// entry.getValue());
			switch (entry.getValue()) {
			case 0:
				classe0++;
				break;
			case 2:
				classe2++;

				break;
			case 4:
				classe4++;
				break;
			}
		}

		// On renvois la classe qui à la valeur la plus élevée
		max = Math.max(classe0, Math.max(classe2, classe4));
		if (max == classe0)
			return 0;
		else if (max == classe2) {
			return 2;
		} else {
			return 4;
		}
	}

	/**
	 * Classe un tweet à l'aide de la méthode de classification knn
	 *
	 * @param t
	 *            le tweet à classer
	 * @param k
	 *            le nombre de voisins à prendre en compte
	 * @return la classe associée au tweet
	 * @throws IOException
	 */
	public static int knn(String t, int k, List<TweetInfos> listName)
			throws IOException {
		Map<String, Integer> voisins = new HashMap<String, Integer>(k);
		Map<String, Integer> distanceVoisins = new HashMap<String, Integer>(k);
		int i, j;
		String newTweet;
		int classe;
		// System.out.println("Tweet a classer : " + t);
		for (i = 0; i < k; i++) {
			// On récupère les informations du tweet
			newTweet = listName.get(i).getTweet();
			classe = listName.get(i).getNote();
			// On associe le tweet lu avec sa distance avec le tweet de
			// référence
			distanceVoisins.put(newTweet, distanceTweet(newTweet, t));
			voisins.put(newTweet, classe);

		}
		// System.out.println("Taille voisins : " + voisins.size());

		for (j = i; j < listName.size(); j++) {
			newTweet = listName.get(j).getTweet();
			classe = listName.get(j).getNote();

			// On ajoute ce tweet à la place d'un des voisins si nécéssaire
			int newDistance = distanceTweet(newTweet, t);
			String key = compareDistancesVoisins(distanceTweet(newTweet, t),
					distanceVoisins);
			// Si il y a un ancien voisin dont la distance est supérieur, on
			// remplace
			if (key != null) {
				voisins.remove(key);
				voisins.put(newTweet, classe);
				distanceVoisins.remove(key);
				distanceVoisins.put(newTweet, newDistance);
			}

		}

		return vote(voisins);
	}

	public static void main(String[] args) throws IOException {
		new View(new Model());

	}

}
