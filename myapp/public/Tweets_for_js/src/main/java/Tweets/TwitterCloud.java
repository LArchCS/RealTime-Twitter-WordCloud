package Tweets;

import java.util.*;
import java.io.*;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import org.json.JSONObject;


public class TwitterCloud {
	// the number of tweets to extract from Twitter
	private int NUMBER_TOKENS;
	List<String> tokens;

	public TwitterCloud(int num) {
		NUMBER_TOKENS = num;
		tokens = new LinkedList<String>();
	}
	
	// create JSon file containing {text: text, frequency: frequency)
	public void createJson(String[] args) throws InterruptedException, FileNotFoundException {
		// Step 1: collect tweets
		TwitterAPI tAPI = new TwitterAPI();
		List<String> tweets = tAPI.getTweets(args, NUMBER_TOKENS);
		// Step 2: Tokenizing and Stemming
		for (String tweet: tweets) {
			addTokens(tweet);
		}
				
		HashMap<String, Integer> frequency = new HashMap<String, Integer>();
		for (String token : tokens) {
			// "RT" means re-tweet
			// it is a common phrase in tweets, but not a word we want to count
			if (token.equalsIgnoreCase("RT")) {
				continue;
			}
			frequency.put(token, (frequency.getOrDefault(token, 0) + 1));
		}
		int minSize = 20;
		int maxSize = 80;
		int maxFreq = 0;
		int minFreq = 10000;
		for (int freq : frequency.values()) {
			maxFreq = Math.max(maxFreq, freq);
			minFreq = Math.min(minFreq, freq);
		}
		//Step 3: Building a frequency map and draw image
		PrintStream output = new PrintStream(new File("wordList.js"));
		output.print("var wordList = [");
		int i = 0;
		for (String key : frequency.keySet()) {
			i += 1;
			JSONObject js = new JSONObject();
			js.put("text", key);
			int temp = frequency.get(key);
			int size = (int) ((temp * 1.0 / maxFreq) * (maxSize) + minSize);
			//size = size / 3 + (new Random()).nextInt(size);
			js.put("size", size / 2 + (new Random()).nextInt(size) + 1);
			output.print(js);
			if (i >= 250) {
				break;
			}
			output.print(", ");
			//output.print(frequency.get(key) + " " + key + "\n");
		}
		output.print("];");
		System.out.println("Json created.");

	}

	public List<String> getWords(){
		return tokens;
	}
	
	// Step 2: Tokenizing and Stemming
	public void addTokens(String tweet) {
		// input format:
		// String tweet = "Russian thrown out window before he could testify for Putin"
		// output format:
		// List<String> tokens = [russian, thrown, out, window, befor, he, could, testifi, putin]
		try (EnglishAnalyzer an = new EnglishAnalyzer()) {
			TokenStream sf = an.tokenStream(null, tweet);
			try {
				sf.reset();
				while (sf.incrementToken()) {
					CharTermAttribute cta = sf.getAttribute(CharTermAttribute.class);
					//Add finished tokens to the list.
					tokens.add(cta.toString());
				}
			} catch (Exception e) {
				System.err.println("Could not tokenize string: " + e);
			} 
		}
	}
}
