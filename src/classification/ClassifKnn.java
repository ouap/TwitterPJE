package classification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.TweetInfos;

public class ClassifKnn {

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
	private static int distanceTweet(String t1, String t2) {
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
	protected static int vote(Map<String, Integer> voisins) {
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

}
