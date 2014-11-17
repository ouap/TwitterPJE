package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import view.View;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class Model extends Observable {
	public List<Status> listTweets;
	public String[] noteTweets = new String[15];
	public String rateLimit;
	public String recherche;

	public List<Status> getTweets() throws IllegalArgumentException {
		if (listTweets == null)
			throw new IllegalArgumentException();
		return listTweets;
	}

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
	 * @return la liste de Status correspondant à la recherche
	 */
	public List<Status> doSearch(String keyWord) {
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(keyWord);
			QueryResult result = twitter.search(query);
			listTweets = result.getTweets();
			setChanged();
			notifyObservers();
			return listTweets;
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
		return null;
	}

	/**
	 * Permet de charger les tweets du fichier csv
	 * 
	 * @param fichier
	 *            le nom du fichier csv à charger
	 */
	public static void chargerTweet(String fichier) {
		try {
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine()) != null) {
				System.out.println(ligne);
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	/**
	 * Methode qui sauvegarde les tweets recherchés dans un csv aprés les avoir
	 * néttoyé et classés
	 * 
	 * @param tweets
	 *            la liste des tweets de la recherche
	 */
	public void save(List<Status> tweets) {
		int i = 0;

		try {
			FileWriter writer = new FileWriter(
					new java.io.File(".").getCanonicalPath()
							+ "/tweets/tweets.csv", true);
			for (Status tweet : tweets) {
				// Nettoyage du tweet + récupération de sa classe
				String text = nettoyerTweet(tweet.getText());
				int classe = knn(text, 3);

				// Ecriture
				writer.write(tweet.getId() + ";" + tweet.getUser().getName()
						+ ";" + text + ";" + tweet.getCreatedAt() + ";"
						+ recherche + ";" + classe + "\n");
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
					+ "/tweets/tweets.csv";
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

		return cleanTweet;
	}

	/**
	 * Retourne la classe d'un tweet en fonction du nb de mot positif/négatif
	 * 
	 * @param tweet
	 *            Le tweet à classer
	 * @return la classe du tweet
	 */
	int getClassePosNeg(String tweet) {
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
	 * Classe un tweet à l'aide de la méthode de classification knn
	 * 
	 * @param t
	 *            le tweet à classer
	 * @param k
	 *            le nombre de voisins à prendre en compte
	 * @return la classe associée au tweet
	 * @throws IOException
	 */
	public int knn(String t, int k) throws IOException {
		Map<String, Integer> voisins = new HashMap<String, Integer>(k);
		Map<String, Integer> distanceVoisins = new HashMap<String, Integer>(k);
		String fichier = new java.io.File(".").getCanonicalPath()
				+ "/tweets/tweets.csv";
		InputStream ips = new FileInputStream(fichier);
		InputStreamReader ipsr = new InputStreamReader(ips);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(ipsr);
		String read;
		String newTweet = null;
		int classe;

		for (int i = 0; i < k; i++) {
			if ((read = br.readLine()) != null) {
				// On récupère les informations du tweet
				String[] ligne = read.split(";");
				newTweet = ligne[2];
				classe = Integer.parseInt(ligne[5]);
				// On associe le tweet lu avec sa distance avec le tweet de
				// référence
				distanceVoisins.put(newTweet, distanceTweet(newTweet, t));
				voisins.put(newTweet, classe);
			}
		}

		while ((read = br.readLine()) != null) {
			String[] ligne = read.split(";");
			newTweet = ligne[2];
			classe = Integer.parseInt(ligne[5]);

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
		br.close();
		return vote(voisins);
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
	private String compareDistancesVoisins(int distanceTweet,
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
	public int vote(Map<String, Integer> voisins) {
		int classe0, classe2, classe4, max;
		classe0 = classe2 = classe4 = 0;

		// On calcule la nombre d'apparition de chaque classe
		for (Entry<String, Integer> entry : voisins.entrySet()) {
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

	public static void main(String[] args) throws IOException {
		new View(new Model());

	}
}