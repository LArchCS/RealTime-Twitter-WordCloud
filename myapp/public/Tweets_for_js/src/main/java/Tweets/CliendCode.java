package Tweets;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CliendCode {
	// The main method to run the entire code
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		createJson();
	}
	
	public static void createJson() throws InterruptedException, FileNotFoundException {
		// how many tweets to collect
		int num = 50;
		// key words to search on twitter
		String[] keywords = {"donald", "trump" };
		// Turn off debugging information
		BasicConfigurator.configure(); List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for ( Logger logger : loggers ) {
			logger.setLevel(Level.OFF);
		}
		TwitterCloud tc = new TwitterCloud(num);
		tc.createJson(keywords);
	}
}

