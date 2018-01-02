import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/*
 * SD2x Homework #11
 * Improve the efficiency of the code below according to the guidelines in the assignment description.
 * Please be sure not to change the signature of the detectPlagiarism method!
 * However, you may modify the signatures of any of the other methods as needed.
 */

public class PlagiarismDetector {

	// Do not change the signatures of detectPlagiarism
	public static Map<String, Integer> detectPlagiarism(String dirName, int windowSize, int threshold) { 
		// Get the directory, make an array of filename strings we need to use
		File dirFile = new File(dirName);
		String[] files = dirFile.list();
		
		Map<String, Integer> numberOfMatches = new HashMap<String, Integer>();
		
		// Iterate through every file
		for (int i = 0; i < files.length; i++) { 
			String file1 = files[i];
			
			// Iterate through every file another time
			// do we really need this iteration?
			for (int j = 0; j < files.length; j++) { 
				String file2 = files[j];
				
				// reads a file and converts it into a Set/List of distinct phrases
				// should it be a set or an list?
				Set<String> file1Phrases = createPhrases(dirName + "/" + file1, windowSize); 
				Set<String> file2Phrases = createPhrases(dirName + "/" + file2, windowSize); 
				
				if (file1Phrases == null || file2Phrases == null)
					return null;
				
				// what's the difference b/w file1Phrases and file2Phrases??
				// should we use a set for this?
				Set<String> matches = findMatches(file1Phrases, file2Phrases);
				
				if (matches == null)
					return null;
								
				if (matches.size() > threshold) {
					String key = file1 + "-" + file2;
					// If the map numberOfMatches doesn't contain the key and file1 is not the same as file2
					// add key and number of matches (value) to the map
					// Get rid of == false and use ! instead
					if (numberOfMatches.containsKey(file2 + "-" + file1) == false && file1.equals(file2) == false) {
						numberOfMatches.put(key,matches.size());
					}
				}				
			}
			
		}		
		
		return sortResults(numberOfMatches);

	}

	
	/*
	 * This method reads the given file and then converts it into a Collection of Strings.
	 * It does not include punctuation and converts all words in the file to uppercase.
	 */
	protected static List<String> readFile(String filename) {
		if (filename == null) return null;
		
		// Should it be a LinkedList, ArrayList, or HashSet?
		List<String> words = new LinkedList<String>();
		
		try {
			Scanner in = new Scanner(new File(filename));
			while (in.hasNext()) {
				words.add(in.next().replaceAll("[^a-zA-Z]", "").toUpperCase());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return words;
	}

	/*
	 * This method reads a file and converts it into a Set/List of distinct phrases,
	 * each of size "window". The Strings in each phrase are whitespace-separated.
	 */
	protected static Set<String> createPhrases(String filename, int window) {
		if (filename == null || window < 1) return null;
				
		// Should it be a list or set?
		List<String> words = readFile(filename);
		
		Set<String> phrases = new HashSet<String>();
		
		// why words.size() - window + 1?  Because the last phrase will have the last few words
		for (int i = 0; i < words.size() - window + 1; i++) {
			String phrase = "";
			for (int j = 0; j < window; j++) {
				// why get(i+j)? We'll get phrases that contain words from the previous phrase
				phrase += words.get(i+j) + " ";
			}

			phrases.add(phrase);

		}
		
		return phrases;		
	}

	

	
	/*
	 * Returns a Set of Strings that occur in both of the Set parameters.
	 * However, the comparison is case-insensitive.
	 */
	protected static Set<String> findMatches(Set<String> myPhrases, Set<String> yourPhrases) {
	
		Set<String> matches = new HashSet<String>();
		
		if (myPhrases != null && yourPhrases != null) {
		
			for (String mine : myPhrases) {
				for (String yours : yourPhrases) {
					if (mine.equalsIgnoreCase(yours)) {
						matches.add(mine);
					}
				}
			}
		}
		return matches;
	}
	
	/*
	 * Returns a LinkedHashMap in which the elements of the Map parameter
	 * are sorted according to the value of the Integer, in non-ascending order.
	 */
	protected static LinkedHashMap<String, Integer> sortResults(Map<String, Integer> possibleMatches) {
		
		// Because this approach modifies the Map as a side effect of printing 
		// the results, it is necessary to make a copy of the original Map
		Map<String, Integer> copy = new HashMap<String, Integer>();

		for (String key : possibleMatches.keySet()) {
			copy.put(key, possibleMatches.get(key));
		}	
		
		LinkedHashMap<String, Integer> list = new LinkedHashMap<String, Integer>();

		for (int i = 0; i < copy.size(); i++) {
			int maxValue = 0;
			String maxKey = null;
			for (String key : copy.keySet()) {
				if (copy.get(key) > maxValue) {
					maxValue = copy.get(key);
					maxKey = key;
				}
			}
			
			list.put(maxKey, maxValue);
			
			copy.put(maxKey, -1);
		}

		return list;
	}
	
	/*
	 * This method is here to help you measure the execution time and get the output of the program.
	 * You do not need to consider it for improving the efficiency of the detectPlagiarism method.
	 */
    public static void main(String[] args) {
    	if (args.length == 0) {
    		System.out.println("Please specify the name of the directory containing the corpus.");
    		System.exit(0);
    	}
    	String directory = args[0];
    	long start = System.currentTimeMillis();
    	Map<String, Integer> map = PlagiarismDetector.detectPlagiarism(directory, 4, 5);
    	long end = System.currentTimeMillis();
    	double timeInSeconds = (end - start) / (double) 1000;
    	System.out.println("Execution time (wall clock): " + timeInSeconds + " seconds");
    	Set<Map.Entry<String, Integer>> entries = map.entrySet();
    	for (Map.Entry<String, Integer> entry : entries) {
    		System.out.println(entry.getKey() + ": " + entry.getValue());
    	}
    }
}
