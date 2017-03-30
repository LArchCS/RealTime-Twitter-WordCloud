# RealTime_Twitter_WordCloud

The idea is to collect words and their frequency from real time twitter using Java and its libraries, then generate word could using Javascript and d3.js, and finally using nood.js or its express frameword to host.

-	First, input key words to search on twitter
1.	Navigate to \myapp\public\Tweets_for_js folder
2.	Open CliendCode.java, input key words to search on Twitter in editor, for example – {“donald”, “trump”}
3.	Run the file

-	Second, Javascript file
1.	CliendCode.java will collect words and their frequency searched from Twitter, and create an array of javascript objects in file wordlist.js
2.	script.js reads from wordlist.js and create the word cloud in html

-	Third, node.js local host
I tried both nood.js and its framework express
1.	Navigate to \myapp folder in command terminal
To try nood.js
2.	Run node server.js in command terminal
3.	Open browser and type in http://localhost:8080/yourfile.html
To try express
2.	Run node app.js in command terminal, if nothing happen, try debug command set DEBUG=myapp:* & npm start for windows or DEBUG=myapp:* npm start for MacOS
3.	Open browser and type in localhost:3000
