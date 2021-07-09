/*
   getData.java

   Description:
      A simple Java program that connects to NyTimes' Covid CSV file,
      that recieves data input from HTML form. Given state and county,
      serves JSON data containing the cases and deaths in that area.
      
      * Backend for the "Is Covid Near Me?" Project *
      
   Notes:
      The implementation has changed slightly due to change in source.
      The program connects to a live github file that updates periodically.

      Since it has a months worth of information, the program currently skips through
      the file until we reach the most recent data, then parses through line by line
      until we find the record containing our state and county.
      
      
   Made by rivejona@kean.edu (Jon)
   
*/
package com.google.sps.servlets;

import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;

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

   // debug long startingTime = System.currentTimeMillis();  avg time 1.35 seconds first attempt, .5 seconds for second attempt
      
   // Create a Java URL to connect to the CSV we want to read from.
      URL url = new URL("https://raw.githubusercontent.com/nytimes/covid-19-data/master/rolling-averages/us-counties-recent.csv");
      
   // Create a BufferedReader that can read from our URL. (faster)
      BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));
      
      
// #######################   Notes:
// # Processing the Data #      We're dealing with rolling averages data now. File's a lot bigger, I'm not loading it all into memory.
// #######################      I'm going to parse through the file, and based on user input, retrieve the necessary line from file.
      
   // Skip the first line of our file, it only contains table headers
      input.readLine();
   
   // The file contains a month's worth of information, with the most recent being yesterday's data.
   // Save yesterday's date, which we can use to tell the computer when to stop
      String yesterday = LocalDate.now().minusDays(1).toString();

   // Read a single line from our file, and initialize our tokens and fileDay (the day the file is currently on)
      String[] tokens = input.readLine().split(",");
      String fileDay = tokens[0];

   // Skip through the file line by line until we reach yesterday's date. 
      while(!fileDay.equals(yesterday)) {
          tokens = input.readLine().split(",");
          fileDay = tokens[0];
      }

   // Now that we've reached yesterday's date, scan through the rest of the file for the record containing our state and county.

   // Desired state and county
      String state  = request.getParameter("state").toLowerCase();
      String county = request.getParameter("county").toLowerCase();

   // State and County the file is currently on
      String fileState  = tokens[3].toLowerCase();
      String fileCounty = tokens[2].toLowerCase();

      String[] results = null;
      String line;

   // Loop through the rest of the file. if we reach EOF, that means location not found.
      while((line = input.readLine()) != null) {

     // If the line we're on has the same county and state we're looking for...
        if(fileState.equals(state) && fileCounty.equals(county))
            results = tokens; // line found!
        else {
            tokens = line.split(",");
            fileState  = tokens[3].toLowerCase();
            fileCounty = tokens[2].toLowerCase();
        }
      }
      
      input.close();


// #######################
// # Output Data To JSON #  In this stage, we create JSON from all the data we've collected.  
// #######################

      response.setContentType("application/json;");

      if(results != null) {
         String newCases = results[4].isEmpty() ? "0" : results[4]; // new cases TODAY
         String avgCases = results[5].isEmpty() ? "0" : results[5]; // avg new cases over the past week (so by average, __ new cases every day)

         String newDeaths = results[7].isEmpty() ? "0" : results[7]; // new deaths TODAY
         String avgDeaths = results[8].isEmpty() ? "0" : results[8]; // avg new deaths over the past week

         response.getWriter().println("{");
         response.getWriter().println("\"newCases\":\""  + newCases  + "\",");
         response.getWriter().println("\"avgCases\":\""  + avgCases  + "\",");
         response.getWriter().println("\"newDeaths\":\"" + newDeaths + "\",");
         response.getWriter().println("\"avgDeaths\":\"" + avgDeaths + "\" ");
         response.getWriter().println("}");
      }
      else {
         response.getWriter().println("{ \"error\":\"Not Found\" }");
      }

      // debug System.out.println("Time took: " + (System.currentTimeMillis()-startingTime)/1000.0 + " seconds.");

  }
}