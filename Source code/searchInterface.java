package cps842assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class searchInterface {
	
	// read in stopword file and generate string array
	private static String[] getStopWords() throws FileNotFoundException
	{
		FileReader text = new FileReader("stopwords.txt");
		BufferedReader bufferedReader = new BufferedReader(text);
		String line;
		String delimiter1 = " ";
		String delimiter2 = "\n";
		StringBuffer docBuffer = new StringBuffer();
		String processString;
		String removeSpecial;
		String[] stopWords = null;
		
		try 
		{
			while ((line = bufferedReader.readLine()) != null) 
			{
				docBuffer.append(line);
				docBuffer.append("\n");				
			}
			bufferedReader.close();
			processString = docBuffer.toString();
			


			removeSpecial = processString.replaceAll(delimiter2, delimiter1);
			removeSpecial = removeSpecial.replaceAll("[^a-zA-Z\\s]", delimiter1);
			removeSpecial = removeSpecial.replaceAll("^ +| +$|( )+", delimiter1);
			removeSpecial = removeSpecial.toLowerCase();
			removeSpecial = removeSpecial.trim();
			stopWords = removeSpecial.split(delimiter1);
			
			
		} 
		
		catch (IOException e) 
			{
				e.printStackTrace();
			}
		return stopWords;
	}
	
	// for scores that are greater than 0, get title by searching with the document ID, get author by searching with the document ID
	public static void getTitle(Map<Integer, String> titleMap,Map<Integer, String> authorMap, List<Integer> topFiftyDocuments, List<Double> topFiftyScores)
	{
		for (int i = 0; i < topFiftyDocuments.size(); i++)
		{
			if (topFiftyScores.get(i) > 0)
			{
				String title = titleMap.get(topFiftyDocuments.get(i));
				String author = authorMap.get(topFiftyDocuments.get(i));
				System.out.println("Doc Id is =" + topFiftyDocuments.get(i) + " | Title is = " + title + " | Author is = " + author +" | Score is = "+ topFiftyScores.get(i));
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException 
	{
		
		Search userSearch = new Search();
		
		try 
		{
			// generate stop word string array and load in maps
			String[] stopWords = getStopWords();	
			
			File postings=new File("normalizeMap");
	        FileInputStream fis=new FileInputStream(postings);
	        ObjectInputStream ois=new ObjectInputStream(fis);

	        Map<Integer, Double> normalizedMap=(Map<Integer, Double>)ois.readObject();

	        ois.close();
	        fis.close();
	        
			File summ=new File("weightTFMap");
	        FileInputStream fis1=new FileInputStream(summ);
	        ObjectInputStream ois1=new ObjectInputStream(fis1);

	        Map<Integer, List<Double>> weightTFMap=(Map<Integer, List<Double>>)ois1.readObject();

	        ois1.close();
	        fis1.close();
	        
			File df=new File("df");
	        FileInputStream fis2=new FileInputStream(df);
	        ObjectInputStream ois2=new ObjectInputStream(fis2);

	        Map<String, Integer> dfMap =(Map<String, Integer>)ois2.readObject();

	        ois2.close();
	        fis2.close();
	        
			File titles=new File("titles");
	        FileInputStream fis3=new FileInputStream(titles);
	        ObjectInputStream ois3=new ObjectInputStream(fis3);

	        Map<Integer, String> titleMap=(Map<Integer, String>)ois3.readObject();

	        ois3.close();
	        fis3.close();
		   
	        
			File authors=new File("authors");
	        FileInputStream fis4=new FileInputStream(authors);
	        ObjectInputStream ois4=new ObjectInputStream(fis4);

	        Map<Integer, String> authorMap=(Map<Integer, String>)ois4.readObject();

	        ois4.close();
	        fis4.close();
	        
	        Scanner reader = new Scanner(System.in);
	        int stopWordCheck = 0;
	        while(true)
	        {
	        	
			
	        	System.out.println("Enter query or stopwordon or stopwordoff or ZZEND: ");
				// case check for options of using or not using stop words or ending program
	        	String query = reader.nextLine();
	        	if (query.equals("ZZEND"))
				{
					break;
				}
				else if (query.equals("stopwordon"))
				{
					stopWordCheck = 1;
					System.out.println("Stop words on");
				}
				else if (query.equals("stopwordoff"))
				{
					stopWordCheck = 0;
					System.out.println("Stop words off");
				}
				
				else if (query.equals("liststopwords"))
				{
					for (int a = 0; a < stopWords.length; a++)
					{
						System.out.println(stopWords[a]);
					}
				}
				
	        	// if use enter query and stop words is on
				else if (stopWordCheck == 1)
				{
					// tokenize user query
					List<String> list = new ArrayList<String>();
					String[] tokenizedQuery = userSearch.queryTokenization(query);
					// for each word in string array of tokenized query, check with stop word string array as a list
					for (int h = 0; h < tokenizedQuery.length; h++)
					{
						if (Arrays.asList(stopWords).contains(tokenizedQuery[h]) == false)
						{
							// if the word is not in the stopwords list, then add to a list
							list.add(tokenizedQuery[h]);
						}
						
					}
					// converts the list of tokenized words from user that were not in stop word list and puts it back into string array
					// called tokenized query
					tokenizedQuery = list.toArray(new String[0]);
					
					// check if tokenzied query is empty or is null
					if (tokenizedQuery != null && tokenizedQuery.length > 0)
					{
						// gets top fifty scores and top fifty documents recpectively and saves it into list of int and double
						List<Integer> topFifty = userSearch.searchFromQueryText(normalizedMap, weightTFMap, dfMap, tokenizedQuery);
		        		List<Double> topScores = userSearch.searchFromUserQuery(normalizedMap, weightTFMap, dfMap, tokenizedQuery);			
		        		// formats output given the top fifty score and top fifty documents
						getTitle(titleMap, authorMap, topFifty, topScores);

					}
					else
					{
						// if user query were only in stop words or nothing at all
						System.out.println("No queries due to stop words on, please enter again.");
					}

				}
	        	// if stop words off, then just take entire user input as query and tokenize it to call getTitle to print out answers
				else if (stopWordCheck == 0)
				{
					String[] tokenizedQuery = userSearch.queryTokenization(query);
					List<Integer> topFifty = userSearch.searchFromQueryText(normalizedMap, weightTFMap, dfMap, tokenizedQuery);
		        	List<Double> topScores = userSearch.searchFromUserQuery(normalizedMap, weightTFMap, dfMap, tokenizedQuery);	
		        	getTitle(titleMap,authorMap, topFifty, topScores);
				}

	        	
	        }
		
		
		} 
		catch (IOException e) 
			{
			e.printStackTrace();
			}
	}
}
