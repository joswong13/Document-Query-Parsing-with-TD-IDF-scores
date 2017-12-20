How to run the program:

Method One - You can copy and paste all the Java files to a Java IDE such as Eclipse.

1) After copy and pasting all the Java files to their own classes, run "eval" first by typing in "cacm.all". This will
create all the postings files and dictionaries as well as other various files used to calculate the TF-IDF values. This program
will take around 1 minute to finish.

2) If you wish to enter search terms manually, then run searchInterface.java in a Java IDE. Else, run the eval.java to see the MAP and 
precision values. Both these programs takes about 2 minutes to start up.

Method Two - Use the included runnable jar files.

1) In a terminal, you can use the command "java -jar invert.jar" then type in cacm.all into the prompt. This will run for about a minute.
2) You can then use "java -jar searchInterface.jar" to manually type in your own search query terms. Or use "java -jar eval.jar" to run the 
program to get the precisions and MAP numbers.

Commands:
- ZZEND to end the program in terminal
- stopwordon to enable stop words
- stopwordoff to disable stop words

Program Overview:
The program will search and display up to the top fifty results. If there are less results, the program will not display any
results that had a score of zero for the similarity scores. The document TF-IDF (weight) value is based on the ((1+log(f)) * log(N/df)) 
while the query weight value is based on the TF value which is (1+log(f)). The postings list for the IDf values are based on the document ID
and is store in the IDF.txt file created by invert.java and the term matrix is also sorted by document ID which is stored in weightTF.txt, also
created by invert.java. 


Files Included:
cacm.all - file that has the title, author, abstract, and other information about documents.
query.txt - file with queries that has keywords or authors that is used for automated search query
qrel.txt - file that shows the document that has high relevance to the query from query.txt
eval.java - automated query loader, reads in query.txt and parses the input into the query format and using search.java, calculate the relevance and hit rate of my search engine
search.java - file for functions to convert the query to a matrix and calculate the more relevant documents
invert.java - given cacm.all, parse all the information into a postings.txt and other files used to calculate the TF-IDF score of the words and query
searchInterface.java - allows users to manually enter query, options include turning stop words on and off (by turning on stop words, it will remove all common
words that will not be searched such as "a", "the" , etc), output is the most relevant documents to the user query 


Data Structures:

Example row in postings.txt.

Word - [[[Document Id], [Number of times the word occurs in the document], [Position the word is found in]]]

The left side is a word that occured in one of the documents, the first number is the document number that the word is found in, the second number is 
the number of times the word occurs in the document, the third number is the position that the word occurs (if the word occurs multiple times in a 
document, then the third list will have multiple values indicating the position that the word can be found as shown in the examples below). 



abacus - [[[216], [1], [7]]]
abstract - [[[253], [1], [14]], [[1323], [3], [79, 130, 134]], .....]



Example row Document Matrix:

Each column in this matrix represents the TF-IDF value a word in the dictionary (all the words that occurs in the list of documents in cacm.all).
1-[0.0, 0.0, ... 2.55, ... 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,  0.0, 0.0]
2-[1.0, 0.0, ... 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,  0.0, 0.0]
3-[0.0, 0.0, ... 0.0, 0.0, 0.0, 4.6, 0.0, 0.0, 0.0, 0.0, 0.0,  0.0, 0.0]
.
.
.