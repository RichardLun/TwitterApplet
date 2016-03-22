import twitter4j.*;       //set the classpath to lib\twitter4j-core-4.0.2.jar
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

public class Twitter_Driver
{
   private static PrintStream consolePrint;
   
   public static void main (String []args) throws TwitterException, IOException
   {
      consolePrint = System.out;
   
     // PART 1
     // set up classpath and properties file
   
      TJTwitter bigBird = new TJTwitter(consolePrint);
     //create message to tweet, then call the tweetOut method
     //bigBird.tweetOut("I just tweeted from my Java program! #APCSRocks @TJColonials Thanks @cscheerleader!");
     // PART 2
     // Choose a public Twitter user's handle
      /*
       Scanner scan = new Scanner(System.in);
     consolePrint.print("Please enter a Twitter handle, do not include the @symbol --> ");
     String twitter_handle = scan.next();
   
     while (!twitter_handle.equals("done"))
     {
   	 // Print the most popular word they tweet
   	 bigBird.makeSortedListOfWordsFromTweets(twitter_handle);
   	 consolePrint.println("The most common word from @" + twitter_handle + " is: " + bigBird.mostPopularWord());
   	 consolePrint.println();
   	 consolePrint.print("Please enter a Twitter handle, do not include the @ symbol --> ");
   	 twitter_handle = scan.next();
     }
     */
   
     // PART 3
      bigBird.investigate();
   
   
   }//end main         

}//end driver        

class TJTwitter 
{
   private Twitter twitter;
   private PrintStream consolePrint;
   private List<Status> statuses;
   private List<String> sortedTerms;
   
   public TJTwitter(PrintStream console)
   {
     // Makes an instance of Twitter - this is re-useable and thread safe.
      twitter = TwitterFactory.getSingleton(); //connects to Twitter and performs authorizations
      consolePrint = console;
      statuses = new ArrayList<Status>();
      sortedTerms = new ArrayList<String>();
   }
   
   /******************  Part 1 *******************/
   public void tweetOut(String message) throws TwitterException, IOException
   {
      twitter.updateStatus(message);
   }
   @SuppressWarnings("unchecked")
   /******************  Part 2 *******************/
   public void makeSortedListOfWordsFromTweets(String handle) throws TwitterException, IOException
   {
      statuses.clear();
      sortedTerms.clear();
      PrintStream fileout = new PrintStream(new FileOutputStream("tweets.txt")); // Creates file for dedebugging purposes
      Paging page = new Paging (1,200);
      int p = 1;
      while (p <= 10)
      {
         page.setPage(p);
         statuses.addAll(twitter.getUserTimeline(handle,page));
         p++;
      }
      int numberTweets = statuses.size();
      fileout.println("Number of tweets = " + numberTweets);
   
      fileout = new PrintStream(new FileOutputStream("garbageOutput.txt"));
   
      int count=1;
      for (Status j: statuses)
      {
         fileout.println(count+".  "+j.getText());
         count++;
      }
   
   	//Makes a list of words from user timeline
      for (Status status : statuses)
      {
         String[]array = status.getText().split(" ");
         for (String word : array)
            sortedTerms.add(removePunctuation(word).toLowerCase());
      }
   
     // Remove common English words, which are stored in commonWords.txt
      sortedTerms = removeCommonEnglishWords(sortedTerms);
      sortAndRemoveEmpties();
   
   }
   
   // Sort words in alpha order. You should use your old code from the Sort/Search unit.
   // Remove all empty strings ""
   @SuppressWarnings("unchecked")
   private void sortAndRemoveEmpties()
   {
      for(int x = 0; x < sortedTerms.size(); x++)
      {
         if(sortedTerms.get(x).equals("") || sortedTerms.get(x).equals(" "))
         {
            sortedTerms.remove(x);
            x--;
         }
      }
      java.util.Collections.sort(sortedTerms);
   }
   
   // Removes common English words from list
   // Remove all words found in commonWords.txt  from the argument list.
   // The count will not be given in commonWords.txt. You must count the number of words in this method.
   // This method should NOT throw an exception. Use try/catch.
   @SuppressWarnings("unchecked")
   private List removeCommonEnglishWords (List<String> list)
   {
      try{
         BufferedReader b = new BufferedReader(new FileReader("commonWords.txt"));
         List<String> common = new ArrayList<String>();
         for(int x = 0; x < 91; x++)
         {
            common.add(b.readLine());
         }
         List<String> temp = list;
         for(int x = 0; x < temp.size(); x++)
         {
            for(int y = 0; y < common.size(); y++)
            {
               if(common.get(y).equalsIgnoreCase(temp.get(x)))
               {
                  list.set(x, "");
               }
            }
         }
      }
      catch(Exception e){
         System.out.println("ERROR: Bad Filename");
      }
      return list;
   }
   
   //Remove punctuation - borrowed from prevoius lab
   //Consider if you want to remove the # or @ from your words. They could be interesting to keep (or remove).
   private String removePunctuation( String s )
   {
      s = s.replaceAll("[^A-Za-z0-9]", "");
      return s;
   }
   //Should return the most common word from sortedTerms. 
   //Consider case. Should it be case sensitive? The choice is yours.
   @SuppressWarnings("unchecked")
   public String mostPopularWord()
   {
      if(sortedTerms.size() == 0)
      {
         System.out.println("No Tweets :(");
      }
      int count = 1, tempCount;
      String popular = sortedTerms.get(0);
      String temp = "";
      for (int i = 0; i < sortedTerms.size() - 1; i++)
      {
         temp = sortedTerms.get(i);
         tempCount = 0;
         for (int j = 1; j < sortedTerms.size(); j++)
         {
            if (temp.equals(sortedTerms.get(j)))
               tempCount++;
         }
         if (tempCount > count)
         {
            popular = temp;
            count = tempCount;
         }
      }
      return popular;
   }
   

   /******************  Part 3 *******************/
   public void investigate ()
   {
      try {
            // gets Twitter instance with default credentials
         Twitter twitter = new TwitterFactory().getInstance();
         User user = twitter.verifyCredentials();
         List<Status> statuses = twitter.getHomeTimeline();
         List<String> removed = new ArrayList<String>();
         List<String> cool = new ArrayList<String>();
         Scanner scan = new Scanner(System.in);
         System.out.print("Who/what do you not want to see: ");
         String no = scan.next();
         System.out.print("Who/what is cool: ");
         String yes = scan.next();
         System.out.println("-------Tweets-------");
         for (Status status : statuses)
         {
            if(status.getUser().getScreenName().toLowerCase().contains(no) || status.getText().toLowerCase().contains(no))
            {
               removed.add("@" + status.getUser().getScreenName() + " - " + status.getText());
               continue;
            }
            if(status.getUser().getScreenName().toLowerCase().contains(yes) || status.getText().toLowerCase().contains(yes))
            {
               cool.add("@" + status.getUser().getScreenName() + " - " + status.getText());
               continue;
            }
            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
         }
         System.out.println("");
         if(cool.size() != 0)
         {
            System.out.println("-------AWESOME :)-------");
            for(String s: cool)
            {
               System.out.println(s);
            }
         }
         else
         {
            System.out.println("Nothing is cool :(");
         }
         System.out.println("");
         if(removed.size() != 0)
         {
         
            System.out.println("-------Removed-------");
            for(String s: removed)
            {
               System.out.println(s);
            }
         }
         else
         {
            System.out.println("Nothing was removed :)");
         }
      } 
      catch (TwitterException bad) {
         bad.printStackTrace();
         System.out.println("Failed to get timeline: " + bad.getMessage());
         System.exit(-1);
      }
   }
   // A sample query to determine how many people in Arlington, VA tweet about the Miami Dolphins
   public void sampleInvestigate ()
   {
      Query query = new Query("Miami Dolphins");
      query.setCount(100);
      query.setGeoCode(new GeoLocation(38.8372839,-77.1082443), 5, Query.MILES);
      query.setSince("2015-12-1");
      try {
         QueryResult result = twitter.search(query);
         System.out.println("Count : " + result.getTweets().size()) ;
         for (Status tweet : result.getTweets()) {
            System.out.println("@"+tweet.getUser().getName()+ ": " + tweet.getText());
         }
      }
      catch (TwitterException e) {
         e.printStackTrace();
      }
      System.out.println();
   }  
   
}