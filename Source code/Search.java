package cps842assignment2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Search {
	
	//get top fifty docID results from query search
	public static List<Integer> searchFromQueryText(Map<Integer, Double> normalizedMap,Map<Integer, List<Double>> weightTFMap, Map<String, Integer> dfMap, String[] query) throws IOException
	{        
    	List<Integer> showList = convertQuerytoMatrix( dfMap ,query );
    	
    	Map<Integer, Double> showMap = new HashMap<Integer, Double>();
    	
    	showMap = calcCosineSimScore(weightTFMap,normalizedMap,showList);
    	 
    	List<Integer> topFifty = getTopFiftyResults(showMap);
    	
    	return topFifty;
    	
	}
	
	
	
	//get top fifty scores associated with the top fifty docID
	public static List<Double> searchFromUserQuery(Map<Integer, Double> normalizedMap,Map<Integer, List<Double>> weightTFMap, Map<String, Integer> dfMap, String[] query) throws IOException
	{
        
    	List<Integer> showList = convertQuerytoMatrix( dfMap ,query );
    	
    	Map<Integer, Double> showMap = new HashMap<Integer, Double>();
    	
    	showMap = calcCosineSimScore(weightTFMap,normalizedMap,showList);
    	 
    	List<Double> topFifty = getTopFiftyScores(showMap);
    	
    	return topFifty;
    	
	}
	// rounds double value to 2 decimal places
	public static double doublee(double value)
	{
		 return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	
	public static String[] queryTokenization(String query)
	{

	String delimiter1 = " ";
	String delimiter2 = "\n";
	String removeSpecial;
	String[] queryArray = null;
	
	
	removeSpecial = query.replaceAll(delimiter2, delimiter1);
	removeSpecial = removeSpecial.replaceAll("[^a-zA-Z\\s]", delimiter1);
	removeSpecial = removeSpecial.replaceAll("^ +| +$|( )+", delimiter1);
	removeSpecial = removeSpecial.toLowerCase();
	removeSpecial = removeSpecial.trim();
	queryArray = removeSpecial.split(delimiter1);
	
	
	return queryArray;
	}

	public static List<Integer> convertQuerytoMatrix(Map<String, Integer> documentFreqMap, String[] query)
	{
		Map<String, Integer> queryMatrix = new HashMap<String, Integer>();
		
		for (int i = 0; i < query.length; i++)
		{
			Integer queryTerm = queryMatrix.get(query[i]);
			
			if (!queryMatrix.containsKey(query[i]))
			{
				queryMatrix.put(query[i], 1);
			}
			else
			{
				queryMatrix.put(query[i], queryTerm + 1);
			}
		}		
		Iterator<Entry<String,Integer>> docFreqMapIt = documentFreqMap.entrySet().iterator();
		
		List<Integer> queryTermMatrix = new ArrayList<Integer>();
		
		//counters
		int totalTerms = 0;
		
		while (docFreqMapIt.hasNext() && totalTerms < documentFreqMap.size())
		{
			Entry<String, Integer> docFreqMapPairs = docFreqMapIt.next();
			String docTerm = docFreqMapPairs.getKey();
			
			if (queryMatrix.containsKey(docTerm))
			{
				queryTermMatrix.add(queryMatrix.get(docTerm));
			}
			else
			{
				queryTermMatrix.add(0);
			}
			totalTerms+=1;
		}
		
		return queryTermMatrix;
	
	}
	
	public static Map<Integer, Double> calcCosineSimScore(Map<Integer,List<Double>> documentVectors, Map<Integer, Double> normalizedDocumentScore, List<Integer> queryMatrix) 
	{
		int sum = 0;
		// calc query normalization
		for (int i = 0; i < queryMatrix.size(); i ++)
		{
			int squared = queryMatrix.get(i) * queryMatrix.get(i);
			sum = sum + squared;	
			
		}
		double queryNormalized = Math.sqrt(sum);
		queryNormalized = doublee(queryNormalized);
		
		int docNum = 0;
		Map<Integer, Double> similarityMap = new HashMap<Integer, Double>();
		
		Iterator<Entry<Integer,List<Double>>> docVectorMapIt = documentVectors.entrySet().iterator();
		
		while (docVectorMapIt.hasNext() && docNum < documentVectors.size())
		{
			Entry<Integer,List<Double>> docVecPairs = docVectorMapIt.next();
			List<Double> docMatrix = docVecPairs.getValue();
			int docId = docVecPairs.getKey();
			
			double matrixSum = 0;
			double simiBotScore = 0;
			double simiScore = 0;
			
			for (int j = 0; j < docMatrix.size(); j++)
			{
				if (queryMatrix.get(j) != 0)
				{
					int queryMatrixNum = queryMatrix.get(j);
					double docMatrixNum = docMatrix.get(j);
					double calcMatrixMultiplication = 0;
					calcMatrixMultiplication = queryMatrixNum * docMatrixNum; 
					matrixSum = matrixSum + calcMatrixMultiplication;
				}
			}
			simiBotScore = (queryNormalized * normalizedDocumentScore.get(docId));
			simiScore = matrixSum/simiBotScore;
			simiScore = doublee(simiScore);
			
			similarityMap.put(docId, simiScore);
		}

		
		return similarityMap;
	}
	
	public static List<Integer> getTopFiftyResults(Map<Integer, Double> similarityMap) throws IOException
	{
	
		Map<Integer, Double> sortedSim = sortByComparator(similarityMap, false);

		Iterator<Entry<Integer, Double>> simMapIt = sortedSim.entrySet().iterator();
		
		List<Integer> topTwentyList = new ArrayList<Integer>();
		
		for ( int m = 0 ; m < 50 ; m++)
		{
			Entry<Integer, Double> simPairs = simMapIt.next();
			int docID = simPairs.getKey();
			topTwentyList.add(docID);
		}
		
		return topTwentyList;
	}
	
	public static List<Double> getTopFiftyScores(Map<Integer, Double> similarityMap) throws IOException
	{
	
		Map<Integer, Double> sortedSim = sortByComparator(similarityMap, false);

		Iterator<Entry<Integer, Double>> simMapIt = sortedSim.entrySet().iterator();
		
		List<Double> topTwentyScores = new ArrayList<Double>();
		
		for ( int m = 0 ; m < 50 ; m++)
		{
			Entry<Integer, Double> simPairs = simMapIt.next();
			double docID = simPairs.getValue();
			topTwentyScores.add(docID);
		}
		
		return topTwentyScores;
	}
	

    private static Map<Integer, Double> sortByComparator(Map<Integer, Double> unsortMap, final boolean order)
    {

        List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, Double>>()
        {
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Entry<Integer, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    
	@SuppressWarnings("resource")
	public static void main(String[] args) throws ClassNotFoundException 
	{
		try 
		{
						
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
		   
		
	        while(true)
	        {
	        	Scanner reader = new Scanner(System.in);
        
	        	System.out.println("Enter query or stopwordon or stopwordoff or ZZEND: ");
		
	        	String query = reader.nextLine();
        
	        	String[] tokenizedQuery = queryTokenization(query);
        
	        	List<Integer> showList = convertQuerytoMatrix( dfMap ,tokenizedQuery );
	        	
	        	Map<Integer, Double> showMap = new HashMap<Integer, Double>();
	        	
	        	showMap = calcCosineSimScore(weightTFMap,normalizedMap,showList);
	        	 
	        	List<Integer> topFifty = getTopFiftyResults(showMap);
	        	List<Double> topScores = getTopFiftyScores(showMap);
	        	
	        	for (int w = 0; w < topFifty.size(); w++ )
	        	{
	        		System.out.println("Doc ID = " + topFifty.get(w) + " | Top Scores = " + topScores.get(w));
	        	}
	        	
	        }
		
		
		} 
		catch (IOException e) 
			{
			e.printStackTrace();
			}
	}
}
