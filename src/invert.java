package cps842assignment2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;



public class invert 
{
	public static double doublee(double value)
	{
		 return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	public static void normalize(Map<Integer, List<Double>> weightTFIDF) throws IOException
	{
		Iterator<Entry<Integer, List<Double>>> normIT = weightTFIDF.entrySet().iterator();
		
		Map<Integer, Double> normalizeMap = new HashMap<Integer, Double>();
		
		int countNorm = 0;
		
		while (normIT.hasNext() && countNorm < weightTFIDF.size())
		{
			Entry<Integer, List<Double>> pairs = normIT.next();
			
			int docID = pairs.getKey();
			
			List<Double> calcNorm = new ArrayList<Double>();
			calcNorm = pairs.getValue();
			double sum = 0;
			
			for (int n = 0; n < calcNorm.size(); n++)
			{
				if (calcNorm.get(n)!= 0)
				{
					sum = sum + Math.pow(calcNorm.get(n), 2);
				}
			}
			double normalized = Math.sqrt(sum);
			normalized = doublee(normalized);
			
			normalizeMap.put(docID, normalized);
		}
		
		BufferedWriter normalizeWriter = new BufferedWriter (new FileWriter("normalize.txt"));
		
		Iterator<Entry<Integer,Double>> normalizedITWriter = normalizeMap.entrySet().iterator();
		
		int counting = 0;
		
		while (normalizedITWriter.hasNext() && counting < normalizeMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Map.Entry<Integer, Double> normalMapEntrySet = normalizedITWriter.next();
	        
	        normalizeWriter.write(normalMapEntrySet.getKey() + "-" + normalMapEntrySet.getValue() + "\n");

	        // increment the record count once we have printed to the file
	        counting++;
	    }
		saveNormalizedScore(normalizeMap);
		normalizeWriter.close();
		
	}
			
	private static void writeDF(HashMap<String, Integer> map) throws IOException
	{
		BufferedWriter outputTitleCount = new BufferedWriter (new FileWriter("Dictionary.txt"));
		
		Map<String, Integer> titleMap = new TreeMap<>(map);
		
		Iterator<Entry<String, Integer>> it = titleMap.entrySet().iterator();
		int count = 0;
		// write document frequency
		while (it.hasNext() && count < titleMap.size()) 
		{
	        // the key/value pair is stored here in pairs
	        Map.Entry<String, Integer> pairs = it.next();
	        outputTitleCount.write(pairs.getKey() + " - " + pairs.getValue() + "\n");

	        // increment the record count once we have printed to the file
	        count++;
	    }
		outputTitleCount.close();
		saveDFMap(titleMap);
		//System.out.println("WriteDF good");
	}

	private static void writeSummary(HashMap<Integer, List<String>> map) throws IOException
	{
		BufferedWriter outputTitleCount = new BufferedWriter (new FileWriter("Summary.txt"));
		
		Map<Integer, List<String>> titleMap = new TreeMap<>(map);
		
		Iterator<Entry<Integer, List<String>>> it = titleMap.entrySet().iterator();
		int count = 0;
		// write document frequency
		while (it.hasNext() && count < titleMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Entry<Integer, List<String>> pairs = it.next();
	        outputTitleCount.write(pairs.getKey() + " - " + pairs.getValue() + "\n");

	        // increment the record count once we have printed to the file
	        count++;
	    }
		outputTitleCount.close();
		saveSummMap(titleMap);
	}
	
	private static void writeIDF(HashMap<String, Integer> map, HashMap<String, Double> getIDF, int totalDoc) throws IOException
	{
		BufferedWriter outputIDF = new BufferedWriter (new FileWriter("IDF.txt"));
		
		Map<String, Integer> titleMap = new TreeMap<>(map);
		
		Iterator<Entry<String, Integer>> it = titleMap.entrySet().iterator();
		int count = 0;
		// write document frequency
		while (it.hasNext() && count < titleMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Map.Entry<String, Integer> pairs = it.next();
	        double fraction = ((double) totalDoc)/pairs.getValue();
	        double calcIDF = Math.log10(fraction);
	        double val = doublee(calcIDF);
	        
	        getIDF.put(pairs.getKey(), val);
	        outputIDF.write(pairs.getKey() + " - " + val + "\n");

	        // increment the record count once we have printed to the file
	        count++;
	    }
		outputIDF.close();
		//System.out.println("WriteDF good");
	}
	
	private static void writeWeightTF(HashMap<String,List<List<List<Integer>>>> postingsMap, HashMap<String, Double> getIDF, int docNumber) throws IOException
	{
		
		// convert to tree map
		Map<String,List<List<List<Integer>>>> weightTreeMap = new TreeMap<>(postingsMap);
		
		//listings
		List<List<List<Integer>>> allPostings = new ArrayList<List<List<Integer>>>();
		
		
		//new hashmap for weight
		Map<Integer, List<Double>> weightTFMap = new HashMap<Integer,List<Double>>();
		
		Iterator<Entry<String,List<List<List<Integer>>>>> itWeight = weightTreeMap.entrySet().iterator();
		
		//exiting iterating through the treemap
		int countWeight = 0;
		// getting position of the term on sorted postings list
		int countTermPos = 0;
		
		
		
		//add zero matrix to doc number, sets entire matrix to 0 
		for ( int j = 1; j <= docNumber; j++)
		{
			List<Double> weightMatrix = new ArrayList<Double>();
			for (int m = 0; m < weightTreeMap.size(); m++)
			{
				
				weightMatrix.add((double) 0);
			}
			weightTFMap.put(j, weightMatrix);
			
		}
		
		while (itWeight.hasNext() && countWeight < weightTreeMap.size())
		{
			Entry<String, List<List<List<Integer>>>> pairs = itWeight.next();
			//get the term
			String theTerm = pairs.getKey();
			
			//get the value [list list list]
			allPostings = pairs.getValue();
			
			//iterate for [[Doc ID],[Freq],[Position].....] and update term matrix with corresponding termFreq
			for ( int i = 0; i < allPostings.size(); i++)
			{
				//get the IDF value from the getIDF hashmap
				double termIDF = getIDF.get(theTerm);
				
				//create [[],[],[]]
				List<List<Integer>> getDocTF = new ArrayList<List<Integer>>();
				getDocTF = allPostings.get(i);
				
				//gets DocID
				List<Integer> docIDList = getDocTF.get(0);
				int docID = docIDList.get(0);
				
				
				//gets Term Freq
				List<Integer> termFreqofTerm = getDocTF.get(1);
				int termFreq = termFreqofTerm.get(0);
				//test
				
		        double calcTF =  1 + (Math.log10(termFreq));
		        double calcTFIDF = calcTF * termIDF;
		        
		        double val = doublee(calcTFIDF);

		        List<Double> currentDocTermMatrix = new ArrayList<Double>();
				
				currentDocTermMatrix = weightTFMap.get(docID);
				
				currentDocTermMatrix.set(countTermPos, val);
				
				weightTFMap.put(docID, currentDocTermMatrix);
	
			}
			
			countTermPos += 1;	
			countWeight +=1;
		}
		
		
		
		//writing to text file
		//writer
		BufferedWriter weightTF = new BufferedWriter (new FileWriter("weightTF.txt"));
		
		Iterator<Entry<Integer,List<Double>>> itWeightTFMap = weightTFMap.entrySet().iterator();
		
		int counting = 0;
		
		while (itWeightTFMap.hasNext() && counting < weightTFMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Map.Entry<Integer, List<Double>> wTFMap = itWeightTFMap.next();
	        
	        weightTF.write(wTFMap.getKey() + "-" + wTFMap.getValue() + "\n");

	        // increment the record count once we have printed to the file
	        counting++;
	    }
		weightTF.close();
		
		normalize(weightTFMap);
		saveWeightTF(weightTFMap);
		
	}
	
	private static void writeTitleMap(HashMap<Integer, String> titleOnly) throws IOException {
		
		BufferedWriter outputTitle = new BufferedWriter (new FileWriter("title.txt"));
		
		Map<Integer, String> titleMap = new TreeMap<>(titleOnly);
		
		Iterator<Entry<Integer, String>> it = titleMap.entrySet().iterator();
		int count = 0;
		// write document frequency
		while (it.hasNext() && count < titleMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Map.Entry<Integer, String> pairs = it.next();
	        outputTitle.write(pairs.getKey() + " - " + pairs.getValue() + "\n");

	        // increment the record count once we have printed to the file
	        count++;
	    }
		outputTitle.close();
		saveTitleMap(titleMap);
	}
	
	private static void writeAuthorMap(HashMap<Integer, String> authorOnly) throws IOException {
		
		BufferedWriter outputAuthor = new BufferedWriter (new FileWriter("author.txt"));
		
		Map<Integer, String> authorMap = new TreeMap<>(authorOnly);
		
		Iterator<Entry<Integer, String>> it = authorMap.entrySet().iterator();
		int count = 0;

		while (it.hasNext() && count < authorMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Map.Entry<Integer, String> pairs = it.next();
	        outputAuthor.write(pairs.getKey() + " - " + pairs.getValue() + "\n");

	        // increment the record count once we have printed to the file
	        count++;
	    }
		outputAuthor.close();
		saveAuthorMap(authorMap);
	}
	
	private static void writePost(HashMap<String,List<List<List<Integer>>>> postingsHashMap) throws IOException
	{
		BufferedWriter outputTitleCount = new BufferedWriter (new FileWriter("Postings.txt"));
		
		Map<String,List<List<List<Integer>>>> titleMap = new TreeMap<>(postingsHashMap);
		
		Iterator<Entry<String,List<List<List<Integer>>>>> it = titleMap.entrySet().iterator();
		int count1 = 0;
		
		while (it.hasNext() && count1 < postingsHashMap.size()) 
		{

	        // the key/value pair is stored here in pairs
	        Entry<String, List<List<List<Integer>>>> pairs = it.next();
	        outputTitleCount.write(pairs.getKey() + " - " + pairs.getValue() + "\n");
	        // increment the record count once we have printed to the file
	        count1++;
	    }
		outputTitleCount.close();
		saveTreeMap(titleMap);
		//System.out.println("Writepost good");
		//Output looks like this
		//term - [[[doc ID],[term freq],[position]], [[doc ID],[term freq],[position]]]
		//term1 - [[[doc ID],[term freq],[position]], [[doc ID],[term freq],[position]]]
	}
	
	public static void saveTreeMap(Map<String,List<List<List<Integer>>>> postingsHashMap)
	{
	    //write to file : "fileone"
	    try{
	    File fileOne=new File("postings1");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(postingsHashMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	   }
	
	public static void saveSummMap(Map<Integer, List<String>> summMap)
	{
	    //write to file : "fileone"
	    try{
	    File fileOne=new File("summ");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(summMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	   }

	public static void saveTitleMap(Map<Integer, String> titleMap)
	{
	    //write to file : "fileone"
	    try{
	    File fileOne=new File("titles");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(titleMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	   }
	public static void saveAuthorMap(Map<Integer, String> authorMap)
	{
	    //write to file : "fileone"
	    try{
	    File fileOne=new File("authors");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(authorMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	   }
	
	public static void saveWeightTF(Map<Integer, List<Double>> weightTFMap)
	{
		  //write to file : "fileone"
	    try{
	    File fileOne=new File("weightTFMap");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(weightTFMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	}
	
	public static void saveNormalizedScore(Map<Integer, Double> normalizeMap)
	{
		  //write to file : "fileone"
	    try{
	    File fileOne=new File("normalizeMap");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(normalizeMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	}
	public static void saveDFMap(Map<String, Integer> dfMap)
	{
	    //write to file : "fileone"
	    try{
	    File fileOne=new File("df");
	    FileOutputStream fos=new FileOutputStream(fileOne);
	        ObjectOutputStream oos=new ObjectOutputStream(fos);

	        oos.writeObject(dfMap);
	        oos.flush();
	        oos.close();
	        fos.close();
	    }
	    catch(Exception e)
	    {
	    	
	    }
	   }
	

		
	//stemming
	/*			
	 * Porter stem = new Porter();				
	for ( int j = 0; j < titleWordsA.length; j++)
		{
		stemWord = titleWordsA[j];
		titleWordsA[j] = stem.stripAffixes(stemWord);
		}
	*/ 
	
	private static void setDocumentFrequency(Map<String, Integer> dFMap, String line)
	{		
		String delimiter1 = " ";
		String delimiter2 = "\n";
		ArrayList<String> titleWordsOccur1 = new ArrayList<String>();
		String removeSpecial;
		String[] stringToArray;
		
		//replace new line with space
		removeSpecial = line.replaceAll(delimiter2, delimiter1);
		// take out special characters and digits
		removeSpecial = removeSpecial.replaceAll("[^a-zA-Z\\s]", delimiter1);
		// take out multiple spaces in front, end and middle
		removeSpecial = removeSpecial.replaceAll("^ +| +$|( )+", delimiter1);
		//remove uppercase
		removeSpecial = removeSpecial.toLowerCase();
		removeSpecial = removeSpecial.trim();
		//send to array
		stringToArray = removeSpecial.split(delimiter1);
		
		//iterate thru array
		for (int i = 0; i < stringToArray.length; i++)
		{

			String temp = stringToArray[i];

			
			// if titleWordsA[j] alrdy occurred, dont go into if loop
			if (titleWordsOccur1.contains(stringToArray[i]) == false)	
			{
				// if first occurrence in this title, add to titleWordsOccur[]
				titleWordsOccur1.add(stringToArray[i]);			
				// get word count from global dFMap
				Integer count = dFMap.get(stringToArray[i]);
				if (count == null)
				{
					// first time word shows up in dictionary
					dFMap.put(temp, 1);
					//System.out.println(temp);
				}
				else
				{
					// next occurence increment of dictionary
					dFMap.put(temp, count + 1);
				}
			}
		}	 
	}
	
	
	private static void setSummary(HashMap<Integer, List<String>> summary, String freq1, int docNumber) 
	{
		String delimiter1 = " ";
		String delimiter2 = "\n";			
		String removeSpecial;
		String[] stringToArray1;
		List<String> summ = new ArrayList<String>();
		
		removeSpecial = freq1.replaceAll(delimiter2, delimiter1);
		removeSpecial = removeSpecial.replaceAll("[^a-zA-Z\\s]", delimiter1);
		removeSpecial = removeSpecial.replaceAll("^ +| +$|( )+", delimiter1);
		removeSpecial = removeSpecial.toLowerCase();
		removeSpecial = removeSpecial.trim();
		stringToArray1 = removeSpecial.split(delimiter1);
		
		for (int h = 0; h < stringToArray1.length; h++)
		{
			summ.add(stringToArray1[h]);
		}
		
		summary.put(docNumber, summ);
		
	}
	
	private static void postingsFile(Map<String,List<List<List<Integer>>>> postingsMap, String titleAndAbstract, Integer docNum)
	{		
		String delimiter1 = " ";
		String delimiter2 = "\n";			
		String removeSpecial;
		String[] stringToArray;
		
		HashMap<String,Integer> wordCount = new HashMap<String,Integer>();	//temp map
		HashMap<String,List<Integer>> position = new HashMap<String,List<Integer>>();	//temp map
		removeSpecial = titleAndAbstract.replaceAll(delimiter2, delimiter1);
		removeSpecial = removeSpecial.replaceAll("[^a-zA-Z\\s]", delimiter1);
		removeSpecial = removeSpecial.replaceAll("^ +| +$|( )+", delimiter1);
		removeSpecial = removeSpecial.toLowerCase();
		removeSpecial = removeSpecial.trim();
		stringToArray = removeSpecial.split(delimiter1);
		
		// word count and position
		for (int i = 0; i < stringToArray.length; i++)
		{
			List<Integer> pos = new ArrayList<Integer>();
			Integer count = wordCount.get(stringToArray[i]);
			if (wordCount.containsKey(stringToArray[i]) == false)
			{
				//List<Integer> pos = new ArrayList<Integer>();
				pos.add(i);
				wordCount.put(stringToArray[i], 1);
				position.put(stringToArray[i], pos);
			}
			else
			{
				wordCount.put(stringToArray[i], count+1);
				pos = position.get(stringToArray[i]);
				pos.add(i);
				position.put(stringToArray[i], pos);
			}
	 
		}
		Iterator<Entry<String, Integer>> it = wordCount.entrySet().iterator();
		//Iterator<Entry<String, List<Integer>>> it1 = position.entrySet().iterator();
		int count = 0;
		while (it.hasNext() && count < wordCount.size()) 
		{
			List<List<Integer>> listOfDocFreqPost = new ArrayList<List<Integer>>();
			List<List<List<Integer>>> listofListOfDocFreqPost = new ArrayList<List<List<Integer>>>();
			Map.Entry<String, Integer> pairs = it.next();
			
			String key = pairs.getKey();
			List<Integer> doc = new ArrayList<Integer>();
			List<Integer> freq = new ArrayList<Integer>();
			doc.add(docNum);
			freq.add(pairs.getValue());
			
			List<Integer> posOfKey = new ArrayList<Integer>();
			posOfKey = position.get(key);
			//[[.I],[freq],[position]]
			listOfDocFreqPost.add(doc);
			listOfDocFreqPost.add(freq);
			listOfDocFreqPost.add(posOfKey);
			count+=1;
			//[[[.I],[freq],[position]]]
			listofListOfDocFreqPost.add(listOfDocFreqPost);
			//termFreqPos.put(docNum, listOfDocFreqPost);
			//test variables
			//System.out.println(key + " - " + docNum + " - " + freq + " - " + listOfFreqPost + "\n");
			
			if (postingsMap.containsKey(key) == false)
			{
				postingsMap.put(key, listofListOfDocFreqPost);
			}
			else
			{
				List<List<List<Integer>>> tempList = new ArrayList<List<List<Integer>>>();
				//[[[.I],[freq],[position]],[[.I],[freq],[position]],[[.I],[freq],[position]]]
				tempList = postingsMap.get(key);
				tempList.add(listOfDocFreqPost);
				postingsMap.put(key, tempList);
			}
			
			
			
		}
		
		
		
		
	}
	
	private static void setTitleOnly(HashMap<Integer, String> titleOnly, String title, int docNum) 
	{
		String formatTitle = title.replaceAll("\n", " ").trim();
		titleOnly.put(docNum, formatTitle);
		
	}
	
	private static void setAuthorOnly(HashMap<Integer, String> authorOnly, String author, int docNum) 
	{
		String formatAuthor = author.replaceAll("\n", " ").trim();
		authorOnly.put(docNum, formatAuthor);
		
	}
	
	private static void createDictPost(BufferedReader bufferedReader, HashMap<String,List<List<List<Integer>>>> postingsHashMap) throws IOException
	{
		StringBuffer titleAbstractBuffer = new StringBuffer();
		StringBuffer titleBuffer = new StringBuffer();
		StringBuffer docBuffer = new StringBuffer();
		StringBuffer authorBuffer = new StringBuffer();
		
		String line;
		String freq1;
		String title;
		String author;
		int docNumber = 0;
		int totalDoc = 0;
		
		HashMap<String,Integer> titleCount = new HashMap<String,Integer>();			
		HashMap<Integer,List<String>> summary = new HashMap<Integer,List<String>>();
		HashMap<Integer, String> titleOnly = new HashMap<Integer, String>();
		HashMap<Integer, String> authorOnlyMap =  new HashMap<Integer, String>();
		
		//used for writeIDF
		HashMap<String, Double> getIDF = new HashMap<String, Double>();
		
		
		while ((line = bufferedReader.readLine()) != null) 
		{				
			// document ID
			if (line.startsWith(".I")) 
			{
				docBuffer.append(line);
				docBuffer.append("\n");
				docNumber = Integer.parseInt(line.replaceAll(".I ", ""));
				totalDoc+=1;
				//line = bufferedReader.readLine();	
			}
			// document frequency
			else if (line.equals(".T"))
			{	
				line = bufferedReader.readLine();
				//reset titleabstractbuffer
				titleAbstractBuffer.setLength(0);
				titleBuffer.setLength(0);
				// if no title, won't add .W to list and .B
				while (line.equals(".W") == false && line.equals(".B") == false)
				{
					titleAbstractBuffer.append(line);
					titleAbstractBuffer.append("\n");
					titleBuffer.append(line);
					titleBuffer.append("\n");
					line = bufferedReader.readLine();
				}
				// appends abstract if there is one
				if (line.equals(".W"))
				{
					line = bufferedReader.readLine();
					while (line.equals(".B") == false && line.equals(null) == false)
					{
						titleAbstractBuffer.append(line);
						titleAbstractBuffer.append("\n");
						line = bufferedReader.readLine();
					}
					
				}
				// sends .T and .W together if possible
				freq1 = titleAbstractBuffer.toString();
				title = titleBuffer.toString();
				setTitleOnly(titleOnly, title, docNumber);
				setDocumentFrequency(titleCount, freq1);
				setSummary(summary, freq1,docNumber);
				postingsFile(postingsHashMap,freq1,docNumber);
				
			}// else line is .T
			
			else if (line.equals(".A"))
			{
				line = bufferedReader.readLine();
				authorBuffer.setLength(0);
				while (line.equals(".N") == false && line.equals(".X") == false)
				{
					authorBuffer.append(line);
					authorBuffer.append("\n");
					line = bufferedReader.readLine();
				}
				author = authorBuffer.toString();
				setAuthorOnly(authorOnlyMap, author, docNumber);
			}
		}//while loop at top
			
		bufferedReader.close();
		
		//write text files outputs
		writeDF(titleCount);
		writeSummary(summary);
		writeIDF(titleCount, getIDF, totalDoc);
		writePost(postingsHashMap);
		writeTitleMap(titleOnly);
		writeWeightTF(postingsHashMap, getIDF, totalDoc);
		writeAuthorMap(authorOnlyMap);
	}
	






	@SuppressWarnings("resource")
	public static void main(String[] args) throws ClassNotFoundException 
	{

		try 
		{
			
			Scanner reader = new Scanner(System.in);
			System.out.println("Enter text file to create dictionary and positings file ending in '.all': ");
			String filename = reader.nextLine();
			
			FileReader text = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(text);
			HashMap<String,List<List<List<Integer>>>> postingsHashMap = new HashMap<String,List<List<List<Integer>>>>();
			
			createDictPost(bufferedReader, postingsHashMap);			
			

		} 
			catch (IOException e) 
				{
				e.printStackTrace();
				}
	}
	
	

	

}

