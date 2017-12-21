package cps842assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class eval {
	// get MAP value of each query and return a double for accumulative MAP value
	public static Double countMapValues(List<Integer> relativeFromQrel, List<Integer> resultsFromSearch)
	{
		double globalAccMapValue = 0;
		int numberQrelHits = 0;
		
		for (int i = 0; i < resultsFromSearch.size(); i++)
		{
			int currentDoc = resultsFromSearch.get(i);
			int j = i + 1;
			if (relativeFromQrel.contains(currentDoc))
			{
				numberQrelHits +=1;
				
				globalAccMapValue = globalAccMapValue + (numberQrelHits/j);
			}
		}
		
		globalAccMapValue = ((double) globalAccMapValue) / relativeFromQrel.size();
		// return the globalMapValue for accumulator
		return globalAccMapValue;
	}
	
	// get the precision value of the query and return a double for accumulative precision value
	public static Double countPrecision(List<Integer> relativeFromQrel, List<Integer> resultsFromSearch)
	{
		int totalNumofRelative = 0;

		
		for (int i = 0; i < relativeFromQrel.size() ; i++)
		{
			int temp = relativeFromQrel.get(i);
			
			if (resultsFromSearch.contains(temp))
			{
				totalNumofRelative +=1;
			}
		}
		
		double precision = ((double) totalNumofRelative) / relativeFromQrel.size();
		// return the precision value
		return precision;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException 
	{
		
		try 
		{
			// reads in normalized map		
			File postings=new File("normalizeMap");
	        FileInputStream fis=new FileInputStream(postings);
	        ObjectInputStream ois=new ObjectInputStream(fis);

	        Map<Integer, Double> normalizedMap=(Map<Integer, Double>)ois.readObject();

	        ois.close();
	        fis.close();
			
	        // reads in weight term frequency map
			File tFMap=new File("weightTFMap");
	        FileInputStream fis1=new FileInputStream(tFMap);
	        ObjectInputStream ois1=new ObjectInputStream(fis1);

	        Map<Integer, List<Double>> weightTFMap=(Map<Integer, List<Double>>)ois1.readObject();

	        ois1.close();
	        fis1.close();
	        
			// reads in document frequency map
			File df=new File("df");
	        FileInputStream fis2=new FileInputStream(df);
	        ObjectInputStream ois2=new ObjectInputStream(fis2);

	        Map<String, Integer> dfMap =(Map<String, Integer>)ois2.readObject();

	        ois2.close();
	        fis2.close();
	        
			// initialize variables
	        String line;
	        String theAutoQuery;
	        StringBuffer docBuffer = new StringBuffer();
	        int docNumber = 0;
	        int totalQueriess = 0;
	        double averagePrecision = 0;
	        double averageMap = 0;
	        StringBuffer queryBuffer = new StringBuffer();
			
			//calls search class and initialize with searchQueryText
	        Search searchQueryText = new Search();
			
			Scanner reader = new Scanner(System.in);
			System.out.println("Reading input from query.text: ");

			// reads input from query.text line by line using file reader
			FileReader text = new FileReader("query.text");
			BufferedReader bufferedReader = new BufferedReader(text);
			
			while ((line = bufferedReader.readLine()) != null) 
			{	
				if (line.startsWith(".I")) 
				{
					// gets doc ID by looking for lines that start with ".I"
					docBuffer.append(line);
					docBuffer.append("\n");
					docNumber = Integer.parseInt(line.replaceAll(".I ", ""));
					totalQueriess +=1;
				}
				
				
				
				else if (line.equals(".W"))
				{
					// gets the query between .W and .A or .N
					line = bufferedReader.readLine();
					queryBuffer.setLength(0);
					
					while (line.equals(".A") == false && line.equals(".N") == false)
					{

						queryBuffer.append(line);
						queryBuffer.append("\n");
						line = bufferedReader.readLine();
					}
					// .N is also part of query for each document, so gets between .N and empty space
					if (line.equals(".N"))
					{
						line = bufferedReader.readLine();
						while (line.startsWith("") == false && line.equals(null) == false)
						{
						String cutBeginning = line.substring(5);
						queryBuffer.append(cutBeginning);
						queryBuffer.append("\n");
						line = bufferedReader.readLine();
						}
					}
					// change entire string query buffer to string array
					theAutoQuery = queryBuffer.toString();
					//System.out.println(theAutoQuery);
					
					// send the string array query to search function "queryTokenization"
					String[] tokenizedQuery = searchQueryText.queryTokenization(theAutoQuery);
					
					// search query against MAPS and generate a list of relative documents
					List<Integer> docIDs = searchQueryText.searchFromQueryText(normalizedMap, weightTFMap, dfMap, tokenizedQuery);
					
					String docStringStartsWith;
					int docRelativeQuery = 0;
					List<Integer> relativeDocuments = new ArrayList<Integer>();
					
					FileReader qrel = new FileReader("qrels.text");
					BufferedReader bufferedReader1 = new BufferedReader(qrel);
					// adds a 0 infront of numbers between 1-9, 1 becomes 01
					if (docNumber < 10)
					{
						docStringStartsWith = String.format("%02d",docNumber);
					}
					else
					{
						docStringStartsWith = String.valueOf(docNumber);
						
					}
					
					
					String qrelLine;
					// read in relative query document and match first number in each line
					while ((qrelLine = bufferedReader1.readLine()) != null) 
					{	
						// if line in relative query document starts with document number, then put the relative into a list
						if (qrelLine.startsWith(docStringStartsWith)) 
						{
							// the relative document is in position 3 to 7
							String doc4Digit = qrelLine.substring(3, 7);
							docRelativeQuery = Integer.parseInt(doc4Digit);
							relativeDocuments.add(docRelativeQuery);
						}
					}
					
					// for queries with no relative documents, do not calculate MAP and precision value
					if (relativeDocuments.isEmpty() == false)
					{
					double currentPrecision = countPrecision(relativeDocuments, docIDs);
					averagePrecision = averagePrecision + currentPrecision;
					
					double currentMapValues = countMapValues(relativeDocuments, docIDs);
					averageMap = averageMap + currentMapValues;
					
					System.out.println("The Query ID is = " + docNumber + " | The Precision value for this query is = " + currentPrecision + " | The MAP value for this query is = " + currentMapValues);
					}

				}// in else loop
				
			}// while line != null
			
			// total average MAP and precision values
			averagePrecision = averagePrecision / totalQueriess;
			System.out.println("Overall average precision value is = " + averagePrecision);
			
			averageMap = averageMap / totalQueriess;
			System.out.println("Overall average MAP value is = " + averageMap);

		} 
			catch (IOException e) 
				{
				e.printStackTrace();
				}
	}
	
}
