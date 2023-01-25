/**
 * This program uses another class named Document.java to hold document data and functions to print data
 * 
 * TO RUN: change the three (3) static variables to the location of the cranfield-corpus documents on the drive, 
 * 			and change the topK to the maximum number of results to be returned to the user.
 * 
 * 	Using TreeMaps and file reading, the program will read document data, and query data to parse tf-idf weights
 * 		the program then goes into a user query to prompt for one of the set queries and body / title weighting
 * 		after a quick search, the user will be shown a topK table of ranked titles and their weights 
 * 		and be asked if they wish to continue. 
 * 
 * 
 * Authors Evan Brown
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Scanner;
import java.util.*;

public class Asn3Java {

    // no more than this many input files needs to be processed
    final static int MAX_NUMBER_OF_INPUT_FILES = 1000;

    // an array to hold Gutenberg corpus file names
    static String inputFile = "C:\\Users\\Evan\\Desktop\\CSCI4130\\EclipseStuff\\Newfolder\\src\\cranfield-corpus\\cran.all.1400";
    static String queryFile = "C:\\Users\\Evan\\Desktop\\CSCI4130\\EclipseStuff\\Newfolder\\src\\cranfield-corpus\\cran.qry";
    static String queryDataFile = "C:\\Users\\Evan\\Desktop\\CSCI4130\\EclipseStuff\\Newfolder\\src\\cranfield-corpus\\cranqrel";
    
    static int topK = 5;
    
    // returns index of a character in the alphabet 
    // uses zero-based indexing
    public static int getLetterValue(char letter) {
        return (int) Character.toUpperCase(letter) - 65;
    }
	
	public static void main(String[] args){
        
        // extract input file name from command line arguments
        // this is the name of the file from the Gutenberg corpus
        System.out.println("Input files directory path name is: " + inputFile + "\nand: " + queryFile);
        
        File fileEntry = new File (inputFile);
        
        // br for efficiently reading characters from an input stream
        BufferedReader br = null;
        
        // wordPattern specifies pattern for words using a regular expression
        Pattern wordPattern = Pattern.compile("[a-zA-Z]+");
        
        LinkedList<String> stopWords = new LinkedList<String> (Arrays.asList ("a", "about", "above", "above", "across", "after", "afterwards", 
        		"again", "against", "all", "almost", "alone", "along", "already", "also","although",
        		"always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any",
        		"anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back",
        		"be","became", "because","become","becomes", "becoming", "been", "before", "beforehand",
        		"behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", 
        		"bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt",
        		"cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", 
        		"eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever",
        		"every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill",
        		"find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from",
        		"front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence",
        		"her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself",
        		"his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is",
        		"it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",
        		"may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move",
        		"much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next",
        		"nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", 
        		"often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours",
        		"ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same",
        		"see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side",
        		"since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", 
        		"sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their",
        		"them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein",
        		"thereupon", "these", "they", "thick", "thin", "third", "this", "those", "though", "three", "through",
        		"throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty",
        		"two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what",
        		"whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein",
        		"whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom",
        		"whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", 
        		"yourself", "yourselves", "the"));
        
        // wordMatcher finds words by spotting word word patterns with input
        Matcher wordMatcher;
        
        // a line read from file
        String line;
        
        // an extracted word from a line
        String word;
        
        /* Printing out data for testing, other lines that use this are commented out
        PrintWriter docWriter = null;
        
        
        try { // open file and catch error
			docWriter = new PrintWriter("TestData.txt");
        }
        catch (FileNotFoundException e) {
			System.err.println("File: DocumentData.txt not found");
			System.exit(1);
		}
        */
        
        // initialization
        // lists to hold the documents based on their doc id's
        TreeMap<Integer, Document> docData = new TreeMap<>();
        TreeMap<String, LinkedList<Integer>> titleTermData = new TreeMap<>();
        TreeMap<String, LinkedList<Integer>> bodyTermData = new TreeMap<>();
        
    	// Start parsing documents
    	
        int docNum = 0;
        Integer docID = 0;
        Document newDoc = new Document();

        // open the input file, read one line at a time, extract words
        // in the line, extract characters in a word, write words into
    	// positional index
        try {
            // get a BufferedReader object, which encapsulates
            // access to a (disk) file
            br = new BufferedReader(new FileReader(fileEntry));
            
            line = br.readLine();
            while (line != null) {
                wordMatcher = wordPattern.matcher(line);
            	
                newDoc = new Document();
            	String temp = "";
                if (line.substring(0, 2).equals(".I")) {
                	// parse id
                	docID = Integer.parseInt(line.substring(3));
                	line = br.readLine();
                	wordMatcher = wordPattern.matcher(line);
                	// parse title
                    while (!line.equals(".A")) {
                		if (line.equals(".T")) {
                			line = br.readLine();
                		}// if
                    	wordMatcher = wordPattern.matcher(line);
                		while (wordMatcher.find()) {
                            word = line.substring(wordMatcher.start(), wordMatcher.end());
                            word = word.toLowerCase();
                            
                            if (!stopWords.contains(word)) {
                                newDoc.updateTitle(word);
                                if (titleTermData.containsKey(word)) { // if the word is in the list
                                	if (!titleTermData.get(word).contains(docID)) { // if word doesn't have docID already
                                    	titleTermData.get(word).add(docID);	
                                	}
                                }
                                else { // word is not in the list, create new space and add instance
                                	titleTermData.put(word, new LinkedList<Integer>());
                                	titleTermData.get(word).add(docID);
                                }
                            }
                            
                        } // while - wordMatcher
                		line = br.readLine();
                    	wordMatcher = wordPattern.matcher(line);
                	} // while not ".A"
                    
                    while (!line.equals(".W")) {
                    	line = br.readLine();
                    	// not used part of document
                    }
                	// parse body
                    while (line != null && !line.substring(0, 2).equals(".I")) {
                		if (line.equals(".W")) {
                			line = br.readLine();
                		}// if
                    	wordMatcher = wordPattern.matcher(line);
                		while (wordMatcher.find()) {
                            word = line.substring(wordMatcher.start(), wordMatcher.end());
                            word = word.toLowerCase();
                            
                            if (!stopWords.contains(word)) {
                                newDoc.updateBody(word);
                                
                                if (bodyTermData.containsKey(word)) { // if the word is in the list
                                	if (!bodyTermData.get(word).contains(docID)) { // if word doesn't have docID already
                                		bodyTermData.get(word).add(docID);	
                                	} // if
                                } // if
                                else { // word is not in the list, create new space and add instance
                                	bodyTermData.put(word, new LinkedList<Integer>());
                                	bodyTermData.get(word).add(docID);
                                } // else                          	
                            } // if
                            
                        } // while - wordMatcher
                		line = br.readLine();
                	} // while not ".I"
                }// if .I
                else {
                	System.out.println("Error at line: " + line);
                }
                docData.put(docID, newDoc);
                // docWriter.print("DOC\n    " + docData.get(docID).toString() + "\n");
            	docNum++;
            } // while - line
        } // try end
        catch (IOException ex) {
            System.err.println("File " + inputFile + " not found. Program terminated.\n");
            System.exit(1);
        } // catch end
        
        
    	// Stop parsing documents
        // Start calculating tf idf values
    	
        
    	// calculate tf-idf of title document terms
    	TreeMap<String, TreeMap<Integer, Double>> docTermTitleTfIdf = new TreeMap<>();
    	double titleTf = 0.0;
    	double titleIdf = 0.0;
    	
    	for (Map.Entry<String, LinkedList<Integer>> entry : titleTermData.entrySet()) {
    		int size = entry.getValue().size();
    		TreeMap<Integer, Double> titleTfIdf = new TreeMap<>();
    		for (int i = 0; i < entry.getValue().size() - 1; i++) {
        		titleIdf = 1 + Math.log(docNum / size);
        		if (docData.get(entry.getValue().peek()) == null) {
        			titleTf = 0.0;
        		}
        		else {
            		titleTf = docData.get(entry.getValue().peek()).termFreqTitle(entry.getKey());
            		// System.out.println("Title Tf: " + titleTf + "\n  title Idf: " + titleIdf);
        		}
        		// docWriter.print("TITLE\n    term: " + entry.getKey() + "\n    doc id: " + entry.getValue().peek() + "\n    tf-idf: " + (titleTf * titleIdf) + "\n");
        		titleTfIdf.put(entry.getValue().pop(), (titleTf * titleIdf));
        	}
    		docTermTitleTfIdf.put(entry.getKey(), titleTfIdf);
    	}
    	
    	
    	// calculate tf-idf of body document terms
    	TreeMap<String, TreeMap<Integer, Double>> docTermBodyTfIdf = new TreeMap<>();
    	double bodyTf = 0.0;
    	double bodyIdf = 0.0;
    	
    	for (Map.Entry<String, LinkedList<Integer>> entry : bodyTermData.entrySet()) {
    		int size = entry.getValue().size();
    		TreeMap<Integer, Double> bodyTfIdf = new TreeMap<>();
    		for (int i = 0; i < entry.getValue().size(); i++) {
        		bodyIdf = 1 + Math.log(docNum / size);
        		if (docData.get(entry.getValue().peek()) == null) {
        			bodyTf = 0.0;
        		}
        		else{
        			bodyTf = docData.get(entry.getValue().peek()).termFreqBody(entry.getKey());
            		// System.out.println("Body Tf: " + bodyTf + "\n  body Idf: " + bodyIdf);
        		}
        		// docWriter.print("BODY\n    term: " + entry.getKey() + "\n    doc id: " + entry.getValue().peek() + "\n    tf-idf: " + (bodyTf * bodyIdf) + "\n");
        		bodyTfIdf.put(entry.getValue().pop(), (bodyTf * bodyIdf));
        	}
    		docTermBodyTfIdf.put(entry.getKey(), bodyTfIdf);
    	}
    	
    	
    	// Stop calculating tf idf
    	// Start parsing query
    	

        fileEntry = new File (queryFile);
        TreeMap<Integer, Document> queryData = new TreeMap<>();
    	TreeMap<String, LinkedList<Integer>> queryTermBase = new TreeMap<>();
    	int queryNum = 0;
    	Document queryDoc;
    	
    	try {
    		br = new BufferedReader(new FileReader(fileEntry));
            
    		Integer queryId = 0;
    		String queryBody = "";
    		line = br.readLine();
        	wordMatcher = wordPattern.matcher(line);
    		
            while (line != null) {
            	
            	queryDoc = new Document();
            	
            	if (line.substring(0, 2).equals(".I")) {
            		// parse ID
            		queryId = Integer.parseInt(line.substring(3));
            		line = br.readLine();
                	wordMatcher = wordPattern.matcher(line);
            		// parse the body
            		while (line != null && !line.substring(0, 2).equals(".I")) {
                		if (line.equals(".W")) {
                			line = br.readLine();
                		}// if
                    	wordMatcher = wordPattern.matcher(line);
	                    // process one word at a time
	                    while (wordMatcher.find()) {
	                        // extract the word
	                        word = line.substring(wordMatcher.start(), wordMatcher.end());
	                        word = word.toLowerCase();
	                        
	                        if (!stopWords.contains(word)) {
                                queryDoc.updateBody(word);
                                
	                        	if (queryTermBase.containsKey(word)) { // if the word is in the list
                                	if (!queryTermBase.get(word).contains(queryId)) { // if word doesn't have docID already
                                		queryTermBase.get(word).add(docID);	
                                	} // if
                                } // if
                                else { // word is not in the list, create new space and add instance
                                	queryTermBase.put(word, new LinkedList<Integer>());
                                	queryTermBase.get(word).add(queryId);
                                } // else  
	                        }// if
	                    } // while - wordMatcher
	                    line = br.readLine();
            		} // while line isn't ".I"
            	} // if .I
            	queryData.put(queryId, queryDoc);
            } // while - read line
        	queryNum++;
    	} // try
    	catch (IOException ex) {
            System.err.println("File " + queryFile + " not found. Program terminated.\n");
            System.exit(1);
    	} // catch
    	
    	
    	// calculate tf-idf of query id terms
    	TreeMap<String, TreeMap<Integer, Double>> queryTermTfIdf = new TreeMap<>();
    	double queryTf = 0.0;
    	double queryIdf = 0.0;
    	
    	for (Map.Entry<String, LinkedList<Integer>> entry : queryTermBase.entrySet()) {
    		int size = entry.getValue().size();
    		TreeMap<Integer, Double> queryTfIdf = new TreeMap<>();
    		for (int i = 0; i < entry.getValue().size(); i++) {
        		queryIdf = 1 + Math.log(docNum / size);
        		if (queryData.get(entry.getValue().peek()) == null) {
        			queryTf = 0.0;
        		}
        		else {
            		queryTf = queryData.get(entry.getValue().peek()).termFreqBody(entry.getKey());
        		}
        		// docWriter.print("QUERY\n    term: " + entry.getKey() + "\n    query id: " + entry.getValue().peek() + "\n    tf-idf: " + (bodyTf * bodyIdf) + "\n");
        		queryTfIdf.put(entry.getValue().pop(), (queryTf * queryIdf));
        	}
    		queryTermTfIdf.put(entry.getKey(), queryTfIdf);
    	}

    	
    	// Stop parsing and calculating query
    	// Start user input
    	
    	
        Scanner userIn = new Scanner(System.in);
		String userInput, cranQuery;
		Document userQuery;
		int queryId;
		double titleWeight = 0.0;
		double bodyWeight = 0.0;
		
		double docTitleSum = 0.0;
		double docBodySum = 0.0;
		
		// store result vectors by their documents ids and their vector results on the user query
		TreeMap<Integer, Double> titleVectors = new TreeMap<>();
		TreeMap<Integer, Double> bodyVectors = new TreeMap<>();
		
		boolean brokenBoost = false;
    	boolean userDone = false;
    	
    	while (!userDone) {
    		brokenBoost = false;  		
    		
    		System.out.print("\nPlease input a Cranfield query. Ex: 001 ");
    		cranQuery = String.valueOf(Integer.parseInt(userIn.nextLine()));
    		queryId = Integer.parseInt(cranQuery);
    		
    		System.out.print("Please input a pair of title and abstract boosts that sum to one. Ex: 0.7 0.3 ");
    		String boost = userIn.nextLine();
    		String[] boostSplit = boost.split("\\s+");
    		if (boostSplit.length >= 2) {
    			titleWeight = Double.parseDouble(boostSplit[0]);
    			bodyWeight = Double.parseDouble(boostSplit[1]);
    			if ((titleWeight + bodyWeight) > 1 || (titleWeight + bodyWeight) < 1) {
    				brokenBoost = true;
    			}
    		}
    		else {
    			brokenBoost = true;
    		}
    		if (brokenBoost) {
    			System.out.print("There was something wrong with your boosts. Please keep a space in between the values."
    					+ "\n  Weights initialized to 0.5 each.\n\n");
    			titleWeight = 0.5;
    			bodyWeight = 0.5;
    		}
    		
    		// get (1) user query and all docs title and body tf-idf by terms and multiply (weighting too) = weights
    		// then print the weights of the results by doc
    		// get the top k
    		userQuery = queryData.get(queryId);
    		if (userQuery == null) {
    			System.out.println("You used an incorrect query value. Exiting.");
    			System.exit(1);
    		}
    		String[] userQuerySplit = userQuery.getBody().split("\\s+");
    		
    		for (int j = 1; j < docNum; j++) {
    			// go through each document 1 at a time
    			docTitleSum = 0.0;
    			docBodySum = 0.0;
    			
    			for (int i = 0; i < userQuerySplit.length; i++) {
    				// go through all the terms per document
        			String term = userQuerySplit[i];
        			
        			if (docTermTitleTfIdf.containsKey(term)) {
	        			if (docTermTitleTfIdf.get(term).containsKey(j)) {
	        				docTitleSum += queryTermTfIdf.get(term).get(queryId) * docTermTitleTfIdf.get(term).get(j);
	        				//System.out.println("title sum: " + docTitleSum + "\n  query tfidf: " + queryTermTfIdf.get(term).get(queryId) +
	        				//		"\n  doc title tfidf: " + docTermTitleTfIdf.get(term).get(j));
	        			}
        			}
        			if (docTermBodyTfIdf.containsKey(term)) {
	        			if (docTermBodyTfIdf.get(term).containsKey(j)) {
	        				docBodySum += queryTermTfIdf.get(term).get(queryId) * docTermBodyTfIdf.get(term).get(j);
	        				//System.out.println("body sum: " + docBodySum + "\n  query tfidf: " + queryTermTfIdf.get(term).get(queryId) +
	        				//		"\n  doc body tfidf: " + docTermBodyTfIdf.get(term).get(j));
	        			}
        			}
        			
    			} // for calculating title and body terms
    			
    			titleVectors.put(j, (docTitleSum * titleWeight));
    			bodyVectors.put(j, (docBodySum * bodyWeight));
    			
    			// System.out.println("docid: " + j + "\n  title weight: " + titleVectors.get(j) 
    			//	+ "\n  body weight: " + bodyVectors.get(j));
    		} // for user query's terms
    		
    		
    		TreeMap<Double, String> bestVectors = new TreeMap<>();
    		int bestIndex = 0;
    		double bestVector = 0.0;
    		for (Map.Entry<Integer, Double> titleEntry : titleVectors.descendingMap().entrySet()) {
    			bestVector = titleEntry.getValue();
    			bestIndex = titleEntry.getKey();
    			for (Map.Entry<Integer, Double> bodyEntry : bodyVectors.descendingMap().entrySet()) {
    				if (bodyEntry.getValue() > bestVector) {
    					bestVector = bodyEntry.getValue();
    					bestIndex = bodyEntry.getKey();
    				}
    			} // for body
    			if (docData.get(bestIndex) != null) {
        			bestVectors.put(bestVector, docData.get(bestIndex).getTitle());		
    			}
    			else {
    				System.out.println("Error at docID: " + bestIndex);
    			}
    			// System.out.println("TF: " + bestVector + "\n  doc: " + bestVectors.get(bestVector));
    		} // for title
    		
    		int i = 1;
    		System.out.print("Here are the top " + topK + " result's titles: \n");
    		for (Map.Entry<Double, String> entry : bestVectors.descendingMap().entrySet()) {
    			System.out.printf("Rank: %3s with weight: %4.2f \n  Title: %s\n", i, entry.getKey(), entry.getValue());
    			i++;
    			if (i > topK) {
    				break;
    			}
    		}
    		
    		
    		System.out.print("\nWould you like to continue?(Y/N).");
    		userInput = userIn.nextLine();
    		if (!userInput.equals("Y") && !userInput.equals("y")) {
    			userDone = true;
    		}
    	}
	} // main()
} // class

/* Test cases

	CASE 1 normal input and then a wrong query code

Input files directory path name is: C:\Users\Evan\Desktop\CSCI4130\EclipseStuff\Newfolder\src\cranfield-corpus\cran.all.1400
and: C:\Users\Evan\Desktop\CSCI4130\EclipseStuff\Newfolder\src\cranfield-corpus\cran.qry

Please input a Cranfield query. Ex: 001 001
Please input a pair of title and abstract boosts that sum to one. Ex: 0.7 0.3 0.5 0.5
Here are the top 5 result's titles: 
Rank:   1 with weight: 1.41 
  Title: similarity laws stressing heated wings 
Rank:   2 with weight: 0.89 
  Title: aircraft flutter 
Rank:   3 with weight: 0.84 
  Title: scale models thermo aeroelastic research 
Rank:   4 with weight: 0.65 
  Title: advantages limitations models 
Rank:   5 with weight: 0.47 
  Title: structural aerelastic considerations high speed flight 

Would you like to continue?(Y/N).y

Please input a Cranfield query. Ex: 001 036
Please input a pair of title and abstract boosts that sum to one. Ex: 0.7 0.3 0.3 0.7
You used an incorrect query value. Exiting.


	CASE 2 incorrect sum of boosts followed by testing not continuing
	
Input files directory path name is: C:\Users\Evan\Desktop\CSCI4130\EclipseStuff\Newfolder\src\cranfield-corpus\cran.all.1400
and: C:\Users\Evan\Desktop\CSCI4130\EclipseStuff\Newfolder\src\cranfield-corpus\cran.qry

Please input a Cranfield query. Ex: 001 001
Please input a pair of title and abstract boosts that sum to one. Ex: 0.7 0.3 0.5 0.6
There was something wrong with your boosts. Please keep a space in between the values.
  Weights initialized to 0.5 each.

Here are the top 5 result's titles: 
Rank:   1 with weight: 1.41 
  Title: similarity laws stressing heated wings 
Rank:   2 with weight: 0.89 
  Title: aircraft flutter 
Rank:   3 with weight: 0.84 
  Title: scale models thermo aeroelastic research 
Rank:   4 with weight: 0.65 
  Title: advantages limitations models 
Rank:   5 with weight: 0.47 
  Title: structural aerelastic considerations high speed flight 

Would you like to continue?(Y/N).n


 */
