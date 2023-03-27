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
		    Scanner s = new Scanner(f);
		    System.out.println("Successfully read file " + f.getName());
		    while (s.hasNext()) {
		    	String word = s.next();
		    	if (map.containsKey(word)) {
		    		// this doesn't work; it needs to check the submap of each map element (i.e. each song) instead
		    		map.remove(word);
		    	}
		    }
		    s.close();
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
	}

}
