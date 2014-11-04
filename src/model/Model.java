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
import java.util.List;
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
	 * Usage: java twitter4j.examples.search.SearchTweets [query]
	 *
	 * @param args
	 *            search query
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
	 * Methode qui sauvegarde les tweets recherchés dans un csv
	 * 
	 * @param tweets
	 */
	public void save(List<Status> tweets) {
		int i = 0;
		try {
			FileWriter writer = new FileWriter(
					"/Users/sais/Documents/workspace/TwitterProject/tweets/tweets.csv",
					true);
			for (Status tweet : tweets) {

				writer.write(tweet.getId() + ";" + tweet.getUser().getName()
						+ ";" + tweet.getText() + ";" + tweet.getCreatedAt()
						+ ";" + recherche + ";" + noteTweets[i++] + "\n");
			}
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Méthode qui test si un tweet fait déjà parti d'un fichier csv à l'aide de
	 * son id
	 * 
	 * @param id
	 *            L'id du tweet à tester
	 * @return
	 */
	public boolean alreadyIn(long id) {
		try {
			String chemin = "/Users/sais/Documents/workspace/TwitterProject/tweets/tweets.csv";
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
	 * @return La limite restante d'appel à l'api twitter
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

	public static void nettoyerFichier(String fichier, String copy)
			throws IOException {
		int positif = 0;
		int negatif = 0;
		int classification;
		FileWriter fw = new FileWriter(copy, false);
		try {
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			String tmp = " ";
			BufferedWriter output = new BufferedWriter(fw);
			while ((ligne = br.readLine()) != null) {

				/* Récupération du tweet */
				String tweet[] = ligne.split(";");

				/* Nettoyage */
				String l0 = Pattern.compile("@[a-zA-Z0-9]*( | : | :|: |:)")
						.matcher(tweet[2]).replaceAll("@");
				String l1 = Pattern.compile("@|#|RT |URL").matcher(l0)
						.replaceAll("");
				String l2 = Pattern.compile("(http|https)://([^; ])*")
						.matcher(l1).replaceAll("");
				String l3 = Pattern.compile("(http|https)://").matcher(l2)
						.replaceAll("");
				String l4 = Pattern.compile("(http|https):/").matcher(l3)
						.replaceAll("");
				String l5 = Pattern.compile("(http|https):").matcher(l4)
						.replaceAll("");
				String l6 = Pattern.compile("(http|https)").matcher(l5)
						.replaceAll("");

				/*
				 * J'ai supprimé toute la ponctuation
				 */

				String l7 = Pattern.compile(" \\. | \\.|\\. ").matcher(l6)
						.replaceAll(" ");
				String l8 = Pattern.compile(" ! | !|! ").matcher(l7)
						.replaceAll(" ");
				String l9 = Pattern.compile(" \\? |\\? | \\?").matcher(l8)
						.replaceAll(" ");
				String l10 = Pattern.compile(" : | :|: ").matcher(l9)
						.replaceAll(" ");
				String nettoye = Pattern.compile(" , | ,|, ").matcher(l10)
						.replaceAll(" ");

				if (!(nettoye.equals(tmp))) {

					/* Annotation */
					positif = motsPositifs(nettoye);
					negatif = motsNegatifs(nettoye);

					int result = positif - negatif;
					if (result > 0) {
						classification = 4;
					} else {
						if (result < 0) {
							classification = 0;
						} else {
							classification = 2;
						}
					}

					output.write(nettoye + " " + classification + "\n");

					output.flush();
				}
				tmp = nettoye;
			}

			output.close();
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Calcule le nombre de mots positifs dans le tweet
	 * 
	 * @param un
	 *            tweet
	 * @return le nombre de mots positifs
	 */
	public static int motsPositifs(String tweet) {
		int positif = 0;
		try {
			InputStream ips = new FileInputStream("positive.txt");
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
	 * Calcule le nombre de mots negatifs dans le tweet
	 * 
	 * @param un
	 *            tweet
	 * @return le nombre de mots negatifs
	 */
	public static int motsNegatifs(String tweet) {
		int negatif = 0;
		try {
			InputStream ips = new FileInputStream("negative.txt");
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
		System.out.println(nbMotsCommuns);
		return ((nbTotal - nbMotsCommuns) / nbTotal);
	}

	public int knn(String t, int k) {
		for (int i = 1; i <= k; i++) {

		}
		return k;
	}

	public static void main(String[] args) {
		new View(new Model());
	}
}