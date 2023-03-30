package tf_idf;
import java.util.*;
import java.io.*;
import java.lang.*;

public class TF_IDF {
	
	static boolean isAlphaNumeric(char c) {
	    if ((c >= '0' & c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
		      return true;
	    }
	    else {
		    return false;
	    }
	}
	
	static Map<String, TreeMap<String, Integer>> removeStopWords(Map<String, TreeMap<String, Integer>> map) {
		try {
		    File f = new File("stopwords.txt");
		    System.out.println("Successfully read file " + f.getName());
		    Set<String> songTitles = map.keySet();
		    Iterator<String> songItr = songTitles.iterator();
			for (int i = 0; i < songTitles.size(); i++) {
			    	Scanner s = new Scanner(f);
				String songName = songItr.next();
				TreeMap<String, Integer> currSong = map.get(songName);
				while (s.hasNext()) {
					String word = s.next();
					if (currSong.containsKey(word)) {
						map.remove(word);
						System.out.println(word + " has been removed from " + songName);
					}
				}
				s.close();
			}
			
		}
		catch (Exception e) {
			System.err.print(e.toString());
		}
		return map;
	}
	
	static Map<String, TreeMap<String, Integer>> readFiles() {
		Map<String, TreeMap<String, Integer>> songs = new TreeMap<>();
		File dir = new File("Queen/");
		for (File file : dir.listFiles()) {
			try {
			    Scanner s = new Scanner(file);
			    System.out.println("Successfully read file " + file.toString());
			    Map<String, Integer> songTokens = new TreeMap<>();
			    while (s.hasNext()) {
			        StringBuilder sb = new StringBuilder(32);
			    	String rawStr = s.next();
			    	for (int i = 0; i < rawStr.length(); i++) {
			    		char c = rawStr.charAt(i);
			    		if (isAlphaNumeric(c))
			    		{
			    			sb.append(Character.toLowerCase(c));
			    		}
			    	}
			    	String str = sb.toString();
			    	if (!songTokens.containsKey(str)) {
				    	System.out.println("Found new key '" + str + "'.");
				    	System.out.println("Number of occurrences of key '" + str + "' is now 1.");
			    		songTokens.put(str, 1);
			    	}
			    	else {
				    	System.out.println("Found duplicate key '" + str + "'.");
			    		int i = songTokens.get(str);
			    		i++;
				    	System.out.println("Number of occurrences of key '" + str + "' is now " + i + ".");
			    		songTokens.replace(str, i);
			    	}
			    }
			    songs.put(file.getName(), null);
			    s.close();
			}
			catch (Exception e) {
				System.err.print(e.toString());
			}
		}
		return songs;
	}
	
	public static void main(String args[]) {
		Map<String, TreeMap<String, Integer>> songs = readFiles();
		removeStopWords(songs);
		getTotalTerms(songs); //this adds a key to each song map labeled: "TOTALSONGS", whose value is the total words in the song after removing stop words
		Map<String, TreeMap<String, Double>> TFMap = termFreqCalcMap(songs); //Song Title --> Term --> Term Frequency(double)
	}

	public static void getTotalTerms(Map<String, TreeMap<String, Integer>> map) {
		Set<String> songTitles = map.keySet();
		Iterator<String> titleItr = songTitles.iterator();
		while(titleItr.hasNext()) {
			int termCount = 0;
			String title = titleItr.next();
			TreeMap<String, Integer> currSong = map.get(title);
			Set<String> terms = currSong.keySet();
			Iterator<String> termsItr = terms.iterator();
			while(termsItr.hasNext()) {
				termCount += currSong.get(termsItr.next());
			}
			currSong.put("TOTALTERMS", termCount);
		}
	}
	
	private static Map<String, TreeMap<String, Double>> termFreqCalcMap(Map<String, TreeMap<String, Integer>> map) {
		Map<String, TreeMap<String, Double>> TFMap = new TreeMap<>();
		Set<String> songTitles = map.keySet();
		Iterator<String> titleItr = songTitles.iterator();
		while(titleItr.hasNext()) {
			TreeMap<String, Double> TFCalc = new TreeMap<String, Double>();
			String title = titleItr.next();
			TreeMap<String, Integer> currSong = map.get(title);
			Set<String> terms = currSong.keySet();
			Iterator<String> termsItr = terms.iterator();
			while(termsItr.hasNext()) {
				String term = termsItr.next();
				double total = (currSong.get("TOTALTERMS"));
				double calc = (double) ((currSong.get(term)) / total);
				TFCalc.put(term, calc);
			}
			TFMap.put(title, TFCalc);
		}
		return TFMap;
	}
	
	private static Map<String, TreeMap<String, Double>> calcTFIDF(Map<String, TreeMap<String, Integer>> map) {
	    Map<String, TreeMap<String, Double>> tfidfMap = new TreeMap<>();
	    Map<String, Double> idfMap = calcIDF(map);
	    for (String title : map.keySet()) {
	        TreeMap<String, Integer> song = map.get(title);
	        TreeMap<String, Double> tfidf = new TreeMap<>();
	        int totalTerms = song.get("TOTALTERMS");
	        for (String term : song.keySet()) {
	            if (!term.equals("TOTALTERMS")) {
	                double tf = (double) song.get(term) / totalTerms;
	                double idf = idfMap.get(term);
	                double tfidfScore = tf * idf;
	                tfidf.put(term, tfidfScore);
	            }
	        }
	        tfidfMap.put(title, tfidf);
	    }
	    return tfidfMap;
	}
	
	private static Map<String, Double> processQuery(String query, Map<String, Double> idfMap) {
	    Map<String, Double> tfidfMap = new TreeMap<>();
	    int totalTerms = 0;
	    String[] tokens = query.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
	    for (String token : tokens) {
	        if (!idfMap.containsKey(token)) {
	            continue; // skip tokens that are not in the IDF map
	        }
	        totalTerms++;
	        double tf = 1.0 / tokens.length; // assume all tokens have equal weight in the query
	        double idf = idfMap.get(token);
	        double tfidfScore = tf * idf;
	        tfidfMap.put(token, tfidfScore);
	    }
	    // Normalize scores by dividing by the total number of terms in the query
	    for (String token : tfidfMap.keySet()) {
	        double tfidfScore = tfidfMap.get(token) / totalTerms;
	        tfidfMap.put(token, tfidfScore);
	    }
	    return tfidfMap;
	}
	
	private static Map<String, Double> rankSongs(Map<String, TreeMap<String, Double>> tfidfMap, Map<String, Double> queryTfidfMap) {
	    Map<String, Double> scoreMap = new TreeMap<>();
	    for (String song : tfidfMap.keySet()) {
	        double score = 0.0;
	        TreeMap<String, Double> songTfidfMap = tfidfMap.get(song);
	        for (String token : queryTfidfMap.keySet()) {
	            if (songTfidfMap.containsKey(token)) {
	                double tokenScore = songTfidfMap.get(token) * queryTfidfMap.get(token);
	                score += tokenScore;
	            }
	        }
	        scoreMap.put(song, score);
	    }
	    return scoreMap;
	}	
}
