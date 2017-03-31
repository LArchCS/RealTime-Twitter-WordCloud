package Tweets;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;


// this class uses HoseBird API, and JSON and collects tweets from Twitter
public class TwitterAPI {
	private List<String> tweets;
	private int number_tokens;
	private BlockingQueue<String> msgQueue;
	private Hosts hosebirdHosts;
	private Authentication hosebirdAuth;
	
	public TwitterAPI() {
		tweets = new LinkedList<String>();
		msgQueue = getBlockingQueue();
		hosebirdAuth = getAuthentication();
		hosebirdHosts = getHoseBirdHosts();
	}
	
	public List<String> getTweets(String[] args, int num) throws InterruptedException{
		System.out.println("Collecting " + num + " tweets...");
		number_tokens = num;
		StatusesFilterEndpoint hosebirdEndpoint = getHosebirdEndpoint(args);
		ClientBuilder builder = getClientBuilder(hosebirdHosts, hosebirdAuth, hosebirdEndpoint, msgQueue);
		Client hosebirdClient = getClient(builder);
		collectTweets(hosebirdClient, msgQueue);
		System.out.println(number_tokens + " tweets collected..");
		return tweets;
	}
	
	// create hosebirdHosts
	private Hosts getHoseBirdHosts() {
		//Direct HoseBird to the right place
		return new HttpHosts(Constants.STREAM_HOST);
	}
	// create hosebirdEndpoint
	private StatusesFilterEndpoint getHosebirdEndpoint(String[] args) {
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// select search key words
		List<String> terms = new ArrayList<String>();
		for (String trump : args) {
			terms.add(trump);
		}
		hosebirdEndpoint.trackTerms(terms);
		return hosebirdEndpoint;
	}
	// create Authentication
	private Authentication getAuthentication() {
		// Add authentication
		String CONSUMER_KEY = "n5p1pzFB6SBHMKd4QNnCstdU1";
		String CONSUMER_SECRET = "uKyfzedwdfqVBHBT9qCukrN7M3qaGP3sw8cR7e7UvzzKu19tGT";
		String TOKEN = "807997722-yIfuJztWyJzVqFTvaNwboJ7PTHF9MEQWlVNxekpQ";
		String TOKEN_SECRET = "i95pzzdVocwsTYWODggLetJQXnpfWfl4Yq7jUPXvb8x6S";
		/*
		String CONSUMER_KEY = System.getenv("CONSUMER_KEY");
		String CONSUMER_SECRET = System.getenv("CONSUMER_SECRET");
		String TOKEN = System.getenv("TOKEN");
		String TOKEN_SECRET = System.getenv("TOKEN_SECRET");
		*/
		Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
		return hosebirdAuth;
	}
	// create BlockingQueue<String>
	private BlockingQueue<String> getBlockingQueue() {
		// store incoming tweet data in BlockingQueue.
		// A BlockingQueue handles waiting for requests (from the internet, in this case)
		return new LinkedBlockingQueue<String>(100000);
	}
	// create ClientBuilder
	private ClientBuilder getClientBuilder(Hosts hosebirdHosts, Authentication hosebirdAuth, StatusesFilterEndpoint hosebirdEndpoint, BlockingQueue<String> msgQueue) {
		//Connect to service and start watching for the terms of interest
		ClientBuilder builder = new ClientBuilder()
			.hosts(hosebirdHosts)
			.authentication(hosebirdAuth)
			.endpoint(hosebirdEndpoint)
			.processor(new StringDelimitedProcessor(msgQueue));
		return builder;
	}
	// create Client
	private Client getClient(ClientBuilder builder) {
		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}
	// collect tweets
	private void collectTweets(Client hosebirdClient, BlockingQueue<String> msgQueue) throws InterruptedException {
		hosebirdClient.connect();
		//Until we've gather enough tokens
		while (tweets.size() < number_tokens) {
			String msg = msgQueue.take();  // JSON representation of tweets
			JSONObject js = new JSONObject(msg);
			try {
				// ask for text from JSON
				// and omit reference URL if the tweet is a re-tweet
				String[] contents = ((String) js.get("text")).split("https:");
				String content = contents[0];
				//System.out.println("::  " + content);
				tweets.add(content);
			} catch (Exception e) {
				//System.out.println("This tweet has no text content");
			}
		}
		//JsonObject object = new JSONObject();
		hosebirdClient.stop();
	}
}

