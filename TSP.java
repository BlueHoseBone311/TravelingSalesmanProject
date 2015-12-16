import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Class that implements the traveling salesman problem using recursion 
 * Copyright (C) 2014. All Rights Reserved
 * @author Christian E. Merchant
 * @version 1.00 2014-07-25
 */

public class TSP {

/**
 * Instance variables to hold the cities to which to be traveled, an index get the permutations of the cities, the distance table that
 * stores distances between cities, and variables for the optimalPermutation and the corresponding minimum distance. 
 */
private ArrayList<String> cities;
private ArrayList<Integer> index;
private ArrayList<ArrayList<Integer>> distanceTable;
private int minimumDistance;
private ArrayList<Integer>optimalPermutation;
private static final int MAX_VALUE = 1000000;

	/**
	 * Constructor that initializes the instance variables. Here we read the <code>inFile</code> one line at a time, parsing the input and 
	 * adding the first line to the cities, and each subsequent line to the <code>distanceTable</code>. The minimumDistance is set to a 
	 * arbitrarily large number so that the initial comparison later will be automatically smaller. Note that we also index the file as the cities
	 * are being added so we can invert/recurse on this later
	 * @param inFile the data file with cities and distances to be read
	 */
	public TSP (Scanner inFile)
	{
		cities = new ArrayList<String>();
		distanceTable = new ArrayList<ArrayList<Integer>>();
		minimumDistance = MAX_VALUE;
		optimalPermutation = new ArrayList<Integer>();
		index = new ArrayList<Integer>();
		ArrayList<Integer> dist = new ArrayList<Integer>();
	    
		if (inFile.hasNextLine())
		{
			String currentline = inFile.nextLine();
			Scanner lineScanner = new Scanner(currentline);
			int i=0; 
			while (lineScanner.hasNext())
			{
			cities.add(lineScanner.next().trim());
			index.add(i);
			i++;
			}
			FileUtils.CloseFile(lineScanner);
		}	
		while (inFile.hasNextLine())
		{
		    String currentline = inFile.nextLine();
		    Scanner lineScanner = new Scanner(currentline);
		    while (lineScanner.hasNext())
		    {
		    	if (lineScanner.hasNextInt())
		    	{	
		    	dist.add(lineScanner.nextInt());
		    	}
		    	else {lineScanner.next();}
		    }
		    ArrayList<Integer> d = new ArrayList<Integer>();
		    d.addAll(dist);
		    distanceTable.add(d);
		    dist.clear();
		    FileUtils.CloseFile(lineScanner);
		}
	}
	/**
	 * Method to return the distance of the optimal permutation 
	 * @return minimumDistance the optimal distance for the given cities
	 */
	public int getMinDistance()
	{
		return minimumDistance;
	}
	
	/**
	 * Method to solve the traveling salesman problem for the given data set. Note that this version of a solution riffs on the word permutations 
	 * example from the text (chapter 8) using ints instead of strings. We create a private method to actually perform the necessary operations, 
	 * we just set it up here
	 * @param outFile
	 * @return string with the optimal ordering of cities
	 */
	public String solveTSP(PrintWriter outFile)
	{
		ArrayList<Integer>permutations = new ArrayList<Integer>();
		this.tsp(permutations, index);
		return this.toString(cities, this.optimalPermutation);
	}
	/**
	 * Helper method that actually solve the traveling salesman problem. In our base case, we calculate the distance for the cities, making sure to 
	 * use a modulus to grab the last city back to Seattle distance and each permutation is output to the <code>logFile</code>. Next, if the calculated distance 
	 * is less than the current minimum distance, we setit to the new minimum distance instance variable and add the corresponding permutation 
	 * to the optimal permutation instance variable
	 * To get to this base case, we permute over the index using recursion to achieve all possible cycles
	 * @param thePerm the <code>arraylist</code> that will hold each permutation of the index 
	 * @param currentNumbers the index that we will permute to find the optimal value
	 */
	private void tsp (ArrayList<Integer>thePerm, ArrayList<Integer>currentNumbers)
	{
		int number = 0;
		int distanceofPermutation = 0;
		if (0 == currentNumbers.size())
		{
			for (int i=0; i<thePerm.size(); i++)
			{
				distanceofPermutation+= distanceTable.get(thePerm.get(i)).get(thePerm.get((i+1)%thePerm.size()));
			}
			FileUtils.logFile.printf("PERM IS: %s with a distance/cost of %d%n", this.toIntString(thePerm), distanceofPermutation);
			
			if (distanceofPermutation < this.minimumDistance)
			{
				this.minimumDistance = distanceofPermutation;
				
				if (!thePerm.isEmpty())
				{		this.optimalPermutation.clear();
						this.optimalPermutation.addAll(thePerm);
				}	
			}
		}
		else
		{
			for (int i = 0; i<currentNumbers.size(); i ++)
			{
				ArrayList<Integer>newNumbers = new ArrayList<Integer>(currentNumbers);
				number = currentNumbers.get(i);
				thePerm.add(number);
				newNumbers.remove(i);
				tsp (thePerm, newNumbers);
				thePerm.remove(thePerm.size()-1);
			}
		}
	}
	/**
	 * Method to convert to a string. Used to translate the optimal permutation to a list of corresponding cities
	 * @param A <code>ArrayList</code> of strings to be filled
	 * @param B <code>ArrayList</code> of integers to be added
	 * @return string of strings
	 */
	public String toString(ArrayList<String> A, ArrayList<Integer> B)
	{
		String s = "";
		for (int i = 0; i<B.size(); i++)
		{
			s += String.format("%s  ", A.get(B.get(i)));
		}	
		return s;
	}
	/**
	 * Method to conver to a string taking an integer <code>ArrayList</code>
	 * @param arrlist the <code>ArrayList</code> to be converted
	 * @return string of integers
	 */
	public String toIntString(ArrayList<Integer> arrlist)
	{
		String s = "";
		for (int i = 0; i<arrlist.size(); i++)
		{
			s+=String.format("%d, ", arrlist.get(i));
		}
		return s;
	}
	
	/**
	 * Method to output an <code>ArrayList</code> as a string
	 * @param lst the array list of strings to be output as a single string
	 * @return a string with the list data
	 */
	public String toString(ArrayList<String> lst)
	{
		String s = "";
		for (int i = 0; i<lst.size(); i++)
		{
			s+= String.format("%25s",lst.get(i));
		}
		return s;
	}
	
	/**
	 * Method to output get the distance table as a string
	 * @return <code>distanceTable</code> as a string
	 */
	public String getCostTable ()
	{
		String s = String.format("%15s %s%n","", this.toString(cities));
		
		for (int i = 0; i < distanceTable.size(); i++)
		{	
			s +=String.format("%-15s", cities.get(i));
			for(int j = 0; j<distanceTable.get(i).size(); j++)
			{	
			s +=String.format("%25d",distanceTable.get(i).get(j));
			}
			s += String.format("%n");
		}	
		
		return s;
	}
	
	
}//End Class TSP
