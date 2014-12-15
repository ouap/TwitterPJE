package classification;
import java.util.List;

import model.TweetInfos;
public class classifBayesBiGramme {

	/**
	 * Fonction qui permet de mettre a la puissance b
	 * 
	 * @param a
	 * @param b
	 * @return a puissance b
	 */
	public static float myPow(float a, float b) {
		float res = 1;
		for (int i = 0; i < b; i++) {
			res *= a;
		}
		return res;
	}

	/**
	 * Calcule les trois .csv negatifs, positifs et neutre et renvoie le tableau
	 * contenant le nombre de mots positifs, negatifs et neutres et le nombre de
	 * tweets positifs, negatifs et neutres.
	 * 
	 * @param fichier
	 * @return
	 * @throws IOException
	 */	
	public static int[] nbTweetsMoodBiGramme(List<TweetInfos> listMood){
		int[] res = new int[2];
		int cpt = 0;
		res[0] = listMood.size();
		for(int i=0;i<listMood.size();i++){
			cpt += listMood.get(i).getTweet().length()-1;
		}
		res[1] = cpt;
		return res;
	}

	/**
	 * Calcule le tableau de tous les mots presents dans une base
	 * d'apprentissage sans repetition (negatifs, positifs ou neutre)
	 * 
	 * @param fichier
	 *            le fichier de base
	 * @param fichier2
	 *            la base d'apprentissage (negatifs, positifs ou neutre)
	 * @return mots
	 * @throws IOException
	 */
	public static String[] tabMotsMoodBiGramme(List<TweetInfos> listMood){
		int tab[] = nbTweetsMoodBiGramme(listMood);
		String resTmp[] = new String[tab[1]];
		int cpt = 0;
		int i,j;
		for(i=0;i<listMood.size();i++){
			String[] tmp = listMood.get(i).getTweet().split(" ");
			for(j=0;j<tmp.length;j++){
				resTmp[cpt] = tmp[j]+tmp[j+1];
				cpt++;
			}
		}
		int cpt2 = 1;
		for(i = 1;i<resTmp.length;i++){
			int alreadyPresent = 0;
			for(j = 0;j<i;j++){
				if(resTmp[i] == resTmp[j])
					alreadyPresent = 1;
			}
			if (alreadyPresent == 0)
				cpt2++;
		}
		String res[] = new String[cpt2];
		int tmp = 1;
		res[0] = resTmp[0];
		for(i=0;i<res.length;i++){
			int alreadyPresent = 0;
			for(j=0;j<i;j++){
				if(resTmp[i] == resTmp[j])
					alreadyPresent = 1;
			}
			if (alreadyPresent == 0){
				res[tmp] = resTmp[i];
				tmp++;
			}
		}
		return res;
	}


	/**
	 * Pour un tableau deja calcule avec les mots presents dans les tweets, on
	 * calcule pour chacun d'entre eux le nombre d'occurrences dans la base
	 * d'apprentissage
	 * 
	 * @param mots
	 *            le tableau avec les mots presents
	 * @param fichier
	 *            la base d'apprentissage (soit negatifs, positifs ou neutres)
	 * @return
	 */
	public static int[] nbMotsBiGramme(String[] mots,List<TweetInfos> listMood) {
		int nbMots[] = new int[mots.length];
		int i,j,k;
		for(i=0;i<mots.length;i++){
			nbMots[i] = 0;
		}
		for(i=0;i<listMood.size();i++){
			String tmp[] = listMood.get(i).getTweet().split(" ");
			for(j=0;j<tmp.length-1;j++){
				for(k=0;k<mots.length;k++){
					if(tmp[j]+tmp[j+1]==mots[k]){
						nbMots[k]++;
					}
				}
			}
		}
		return nbMots;
	}
	
	
	public static int nbMotsTotalMoodBiGramme(int[] nbMots) {
		int nbTot = 0;
		for (int nbMot : nbMots) {
			nbTot += nbMot;
		}
		return nbTot;
	}

	/**
	 * Calcul du tableau de biGramme pour un tweet
	 * @param tweet
	 * @return
	 */

	public static String[] tweetToTabBiGramme(String tweet){
		String[] tab = tweet.split(" ");
		String[] biGramme = new String[tab.length-1];
		if(tweet.length()==0){
			return tab;
		}
		int cpt=1;
		int tmp;
		int pos=1;
		biGramme[0] = tab[0]+" "+tab[1];
		for(int i=1;i<tab.length-1;i++){
			tmp = 0;
			biGramme[i] = tab[i]+" "+tab[i+1];
			for(int j=0;j<i;j++){
				if(biGramme[i].equals(biGramme[j])){
					tmp = 1;
				}
			}
		 if (tmp == 0){
			 cpt++;
		 }
		}
		String motsTweetBiGramme[] = new String[cpt];
		motsTweetBiGramme[0] = biGramme[0];
		for(int k = 1;k<biGramme.length;k++){
			tmp = 0;
			for(int l=0;l<pos;l++){
				if(motsTweetBiGramme[l].equals(biGramme[k])){
					tmp = 1;
				}
			}
			if (tmp == 0){
				motsTweetBiGramme[pos] = biGramme[k];
				pos++;
			}
		}
		return motsTweetBiGramme;
	}

	public static int[] nbMotstweetBiGramme(String tweet){
		int cpt;
		String[] motsTweet = tweetToTabBiGramme(tweet);
		int[] nbMots = new int[motsTweet.length];
		String[] sp = tweet.split(" ");
		String[] biGrammeEntier = new String[sp.length-1];
		for(int j=0;j<biGrammeEntier.length;j++){
			biGrammeEntier[j] = sp[j]+" "+sp[j+1];
		}
		for(int i=0;i<motsTweet.length;i++){
			cpt = 0;
			for(int k=0;k<biGrammeEntier.length;k++){
				if(biGrammeEntier[k].equals(motsTweet[i])){
					cpt+=1.0;
				}
			}
			nbMots[i] = cpt;
		}
		return nbMots;
	}

	
	public static float probaTweetMoodBiGramme(List<TweetInfos> listTweets,List<TweetInfos> listMood, List<TweetInfos> secondList, List<TweetInfos> thirdList, String tweet, int classif){
		int nbTweetsMood[] = nbTweetsMoodBiGramme(listTweets);
		int nbTweets = listTweets.size();
		
		String[] motsMood = tabMotsMoodBiGramme(listMood);
		String[] secondMotsMood = tabMotsMoodBiGramme(secondList);
		String[] thirdMotsMood = tabMotsMoodBiGramme(thirdList);

		
		int nbMotsMood[] = nbMotsBiGramme(motsMood, listMood);
		int nbMotsMoodSecond[] = nbMotsBiGramme(secondMotsMood, secondList);
		int nbMotsMoodThird[] = nbMotsBiGramme(thirdMotsMood, thirdList);
		
		int nbMotsTotMood = nbMotsTotalMoodBiGramme(nbMotsMood);
		int nbMotsTotSecond = nbMotsTotalMoodBiGramme(nbMotsMoodSecond);
		int nbMotsTotThird = nbMotsTotalMoodBiGramme(nbMotsMoodThird);
		
		int nbTotal = nbMotsTotMood + nbMotsTotSecond + nbMotsTotThird;
		
		String[] motsTweet = tweetToTabBiGramme(tweet);
		int[] nbMotsTweet = nbMotstweetBiGramme(tweet);
		float probaMood = 0;
		probaMood = nbTweetsMood[0] / nbTweets;
		float proba = 0;
		for (int i = 0; i < motsTweet.length; i++) {
			if (motsTweet[i].length() > 3) {
				for (int j = 0; j < motsMood.length; j++) {
					float probaMot = 0;
					if (motsMood[j].equals(motsTweet[i])) {
						if (motsMood[j].length() > 3) {
							if (proba == 0) {
								proba = 1;
							}
							if (classif == 0) {
								/* Presence */
								probaMot = (nbMotsMood[j] + 1)
										/ (nbMotsTotMood + nbTotal);
							} else {
								/* Frequence */
								probaMot = myPow(
										((nbMotsMood[j]+1) / (nbMotsTotMood + nbTotal)),
										nbMotsTweet[i]);

							}
						}
					}
					if (probaMot != 0) {
						proba *= probaMot;
					}
				}
			}
		}
		proba *= probaMood;
		return proba;
	}



	/**
	 * Classification de bayes, classif sera mis � 0 pour la classification par
	 * pr�sence, � 1 pour la classification par fr�quence
	 * 
	 * @param fichier
	 * @param tweet
	 * @param classif
	 * @return
	 * @throws IOException
	 */	
	public int classifierBayesBiGramme(List<TweetInfos> listTweets, List<TweetInfos> listPos, List<TweetInfos> listNeg, List<TweetInfos> listNeutre, String tweet, int classif)
	 {
		float probaPos = probaTweetMoodBiGramme(listTweets, listPos, listNeg, listNeutre, tweet, classif);
		System.out.println("positifs : " + probaPos);
		float probaNeg = probaTweetMoodBiGramme(listTweets, listNeg, listPos, listNeutre, tweet, classif);
		System.out.println("Negatifs : " + probaNeg);
		float probaNeutre = probaTweetMoodBiGramme(listTweets,listNeutre, listPos, listNeg, tweet, classif);
		System.out.println("Neutres : " + probaNeutre);
		if (probaPos > probaNeg && probaPos > probaNeutre) {
			return 4;
		} else {
			if (probaNeg > probaNeutre && probaNeg > probaPos) {
				return 0;
			} else
				return 2;
		}
	}
}
