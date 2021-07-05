/*
   getData.java

   Description:
      A simple Java program that connects to NyTimes' Covid CSV file
      then processes that data into an easy to use HashMap data structure.
      Recieves data input from HTML form.
      
      * Prototype for IsCovidNearMe.java *
      
   Notes:
      While the csv could be locally downloaded and ran instead, this particular
      file is updated on a daily basis- making reading directly from the internet easier to manage.
      
      The HashMap setup is scalable, however is setup the way it is using pairs right now because we only
      have two fields to save per county. If we can get more, (Population, Vaccinations), then instead of 
      a Pair, we can use custom classes (class County) to store the data in.
      
      That being said, the current hashmap uses two "Pairs," or an object that can store two variables in one.
      
      To access hashmap:
         dataMap.get( new Pair<String, String>("myState", "myCounty") );  (returns a pair- cases and deaths)
         
      To use pair:
         myPair.getKey()   (Gets first variable in pair)
         myPair.getValue() (Gets second variable in pair)
      
      
   Made by rivejona@kean.edu (Jon)
   
*/
package com.google.sps.servlets;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handles requests sent to the /hello URL. Try running a server and navigating to /hello! */
@WebServlet("/getData")
public class getData extends HttpServlet {

  static final long serialVersionUID = 1;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
// #######################
// # Retrieving the Data #
// #######################
      
   // Create a Java URL to connect to the CSV we want to read from.
      URL url = new URL("https://raw.githubusercontent.com/nytimes/covid-19-data/master/live/us-counties.csv");
      
   // From that URL, send a "Get" request to retrieve the information.
      URLConnection data = url.openConnection();
      
   // Create a Scanner that can read from our URL response.
      Scanner input = new Scanner(data.getInputStream());
      
      
// #######################   Notes:
// # Processing the Data #      Since I want the data to be indexed by State AND County, I must use "Pairs."
// #######################      Also, since the data we have for every county is also a pair, I'll use one in our data aswell.

   // Create a hashmap to store our data, indexed by a pair of strings, that will store a pair of ints. (State, County) => (cases, deaths)
      HashMap<Pair<String, String>, Pair<Integer, Integer>> dataMap = new HashMap<Pair<String, String>, Pair<Integer, Integer>>();
      
   // Skip the first line of our file, it only contains table headers
      input.nextLine();
      
   // Fill the hashmap with our data! (Read our data line by line, splitting by comma)
      while(input.hasNextLine()) {
         String[] tokens = input.nextLine().split(",");
         
         String county = tokens[1].toLowerCase();
         String state  = tokens[2].toLowerCase();
         
         // DEBUG: System.out.println("State: " + state + " County: " + county + " Cases: " + tokens[4] + " Deaths: " + tokens[5]);
         
         int cases  = tokens[4].isEmpty() ? 0 : Integer.parseInt(tokens[4]);
         int deaths = tokens[5].isEmpty() ? 0 : Integer.parseInt(tokens[5]);
         
         Pair<String, String> myCounty = new Pair<String, String>(state, county);
         Pair<Integer, Integer> myCountyData = new Pair<Integer, Integer>(cases, deaths);
         
         dataMap.put(myCounty, myCountyData);
      }
      
      input.close();


// #############################
// # Displaying Output To User #  In this stage, we create JSON from all the data we've collected.  
// #############################

      response.setContentType("application/json;");

      String state  = request.getParameter("state").toLowerCase();
      String county = request.getParameter("county").toLowerCase();
      
      Pair<String, String> myCounty = new Pair<String, String>(state, county);

   // If the state & county found is in our database,
      if (dataMap.containsKey(myCounty)) {
        Pair<Integer, Integer> myCountyData = dataMap.get(myCounty);

        int cases  = myCountyData.getKey();
        int deaths = myCountyData.getValue();

        response.getWriter().println("{ \"cases\": \"" + cases + "\", \"deaths\":\"" + deaths + "\" }");
      }
   // If not...
      else {
        response.getWriter().println("Not Found");

        /* Debug
          response.getWriter().println("For State: " + state + " and County: " + county);
          response.getWriter().println("HashMap Size: " + dataMap.size());
          response.getWriter().println("HashMap Data:");

        for (Pair<String, String> key : dataMap.keySet())
            response.getWriter().println(key);
        */
      }

  }
}

// Looks like the Pair class isn't included in java by default.
// Here's my implementation. (Only valid for same type pairs, i/e String, String, Int, Int, for simplicity)
class Pair<K, V> {

    private final K key; 
    private final V value; 
    
public Pair(K key, V value) { 
   this.key = key; 
   this.value = value; 
}

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
          return true;
          
        else if (!(other instanceof Pair))
          return false;
        
        Pair<K, V> other_ = (Pair<K, V>) other; // Cast other to an instance of pair, to make the rest of the code easier
          
        if (this.key instanceof String && other_.getKey() instanceof String) // if the pairs store Strings, use String's secure equals method
           return this.key.equals(other_.getKey()) && this.value.equals(other_.getValue());
        
        else                                                                 // if they don't, then we don't really care, now do we
          return this.key == other_.getKey() && this.value == other_.getValue();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    
    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
}
