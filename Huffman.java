
import java.io.File;
import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Huffman {

    /**
     Code
     provided from previous version and modified for 2020.
    */
    public static void encode()throws IOException{
        // initialize Scanner to capture user input
        Scanner sc = new Scanner(System.in);

        // capture file information from user and read file
        System.out.print("Enter the filename to read from/encode: ");
        String f = sc.nextLine();

        // create File object and build text String
        File file = new File(f);
        Scanner input = new Scanner(file).useDelimiter("\\z");
        String text = input.next();

        // close input file
        input.close();

        // initialize Array to hold frequencies (indices correspond to
        // ASCII values)
        int[] freq = new int[256];
        // concatenate/sanitize text String and create character Array
        // nice that \\s also consumes \n and \r
        // we can add the whitespace back in during the encoding phase

        char[] chars = text.replaceAll("\\s", "").toCharArray();

        // count character frequencies
        for(char c: chars)
            freq[c]++;


        //Your work starts here************************************8
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        
        //add each character and their frequency to the pair list
        for(int i = 0; i<256; i++){
            if(freq[i]!=0){
                // this method of rounding is good enough
                Pair p = new Pair((char)i, Math.round(freq[i]*10000d/chars.length)/10000d);
                pairs.add(p);
            }
        }
        
        //two pairs that will be used to create the Huffman tree
        ArrayList<BinaryTree<Pair>> S = new ArrayList<BinaryTree<Pair>>();
        ArrayList<BinaryTree<Pair>> T = new ArrayList<BinaryTree<Pair>>();
        
        //this code block will turn each pair in pairs into a BinaryTree object and then add it
        //to the S arrayList in ascending order (greatest to least probability)
        double low = 2;
        int count = pairs.size();
        while (count > 0) {
        	
        	Pair newAdd = pairs.get(0);
        	
        	for (int i = 0; i < pairs.size(); i++) {
        		if (pairs.get(i).getProb() < low) {
        			low = pairs.get(i).getProb();
        			newAdd = pairs.get(i);
        		}
        	}
        	
        	BinaryTree<Pair> n = new BinaryTree<Pair>();
        	n.makeRoot(newAdd);
        	S.add(n);
        	pairs.remove(newAdd);
        	count--;
        	low = 2;
        }
        
        //keeping track of the number of unique characters
        int numOfDiff = S.size();
        
        //first loop to follow the algorithm to build the Huffman tree
        while (S.isEmpty() == false) {
        	BinaryTree<Pair> A = new BinaryTree<Pair>();
        	BinaryTree<Pair> B = new BinaryTree<Pair>();
        	
        	//conditions to check which will be the next two pairs added to the tree
        	if (T.isEmpty()) {
        		A = S.remove(0);
        		B = S.remove(0);
        	}
        	else  if (T.isEmpty() == false){
        		if (S.get(0).getData().getProb() < T.get(0).getData().getProb()) {
        			A = S.remove(0);
        		}
        		else {
        			A = T.remove(0);
        		}
        	  
        		if (S.isEmpty()) {
        			B = T.remove(0);
        			
        			//creating a new tree with the new root probability value as the sum of
        			//it's children's probabilities
        			Pair p = new Pair('⁂', (A.getData().getProb() + B.getData().getProb()));
        			BinaryTree<Pair> P = new BinaryTree<Pair>();
        			P.makeRoot(p);
        			P.setLeft(A);
        			P.setRight(B);
        			
        			T.add(P);
        			break;
        		}
        	  
        		if (T.isEmpty()) {
        			B = S.remove(0);
        		}
        	  
        		else {
        			if (S.get(0).getData().getProb() < T.get(0).getData().getProb()) {
        				B = S.remove(0);
        			}
        			else {
        				B = T.remove(0);
        			}
        		}
        	}
        	
        	//creating a new tree with the new root probability value as the sum of
			//it's children's probabilities
        	Pair p = new Pair('⁂', (A.getData().getProb() + B.getData().getProb()));
        	BinaryTree<Pair> P = new BinaryTree<Pair>();
        	P.makeRoot(p);
        	P.setLeft(A);
        	P.setRight(B);
        	
        	T.add(P);
        }
        
        //finishes the tree if one singular tree does not exist after following the
        //beginning of the algorithm 
        if (T.size() > 1) {
    	  while (T.size() > 1) {
    		  BinaryTree<Pair> A = new BinaryTree<Pair>();
              BinaryTree<Pair> B = new BinaryTree<Pair>();
  
              A = T.remove(0);
              B = T.remove(0);
              
              //creating a new tree with the new root probability value as the sum of
  			  //it's children's probabilities
              Pair p = new Pair('⁂', (A.getData().getProb() + B.getData().getProb()));
              BinaryTree<Pair> P = new BinaryTree<Pair>();
              P.makeRoot(p);
              P.setLeft(A);
              P.setRight(B);
              
              T.add(P);
    	  }
      }
      
      //make an array of all of the huffman codes
      String[] codesWithNull = findEncoding(T.get(0));
      ArrayList codes = new ArrayList();
      for (int i = 0; i < codesWithNull.length; i++) {
    	  if (codesWithNull[i] != null) {
    		  codes.add(codesWithNull[i]);
    	  }
      }
      
      //sort the Huffman codes
      Collections.sort(codes);
      
      //perform a traversal of the binary tree to make an array list of each letter
      ArrayList letters = new ArrayList();
      Queue<BinaryTree<Pair>> q = new LinkedList<BinaryTree<Pair>>();
      q.add(T.get(0));
      while(!q.isEmpty()){
          BinaryTree<Pair> tmp = q.remove();
			String x = tmp.getData().getValue() + "           " + tmp.getData().getProb();
			letters.add(x);
          if(tmp.getLeft()!=null)
              q.add(tmp.getLeft());
          if(tmp.getRight()!=null)
              q.add(tmp.getRight());
      }
      
      //code to remove the arbitrary root characters used to build the binary
      //tree from the list of different letters 
      ArrayList toRemove = new ArrayList();
      for (int i = 0; i < letters.size(); i++) {
    	  if (letters.get(i).toString().charAt(0) == '⁂') {
    		  toRemove.add(letters.get(i));
    	  }
      }
      for (int i = 0; i < toRemove.size(); i++) {
    	  letters.remove(toRemove.get(i));
      }
   
      //Print the Huffman codes for each different character in the input
      //string to Huffman.txt
      PrintWriter output1 = new PrintWriter("Huffman.txt");
      output1.println("Symobol     Probability     Huffman Code\n");
      for (int i = 0; i < codes.size(); i++) {
    	  output1.println(letters.get(i) + "          " + codes.get(i));
      }
      
      output1.close();
      
      //separate each word in the input string
      String[] eachWord = text.split(" ");
      
      //an string to hold the final encoded text string
      String eachLetterEncoded = "";
      
      //Loop to build the final encoded text string
      //for each word
      for (int i = 0; i < eachWord.length; i++) {
    	  //for each letter in each word
    	  for (int j = 0; j < eachWord[i].length(); j++) {
    		  //find the code to represent the letter
    		  for (int k = 0; k < codes.size(); k++) {
    			  char check = eachWord[i].charAt(j);
    			  if(check == letters.get(k).toString().charAt(0)) {
    				  eachLetterEncoded = eachLetterEncoded + codes.get(k) + " ";
    			  }
    		  }
    	  }
    	  //add an extra space to signify the end of a word
    	  eachLetterEncoded = eachLetterEncoded + " ";
      }
      
      //Print the final encoded string to Encoded.txt
      System.out.println("Codes generated. Printing codes to Huffman.txt\n" +
    		  "Printing encoded text to Encoded.txt\n\n* * * * *\n");
      PrintWriter output2 = new PrintWriter("Encoded.txt");
      output2.print(eachLetterEncoded);
      output2.close();
    }


    public static void decode()throws IOException{
        // initialize Scanner to capture user input
        Scanner sc = new Scanner(System.in);

        // capture file information from user and read file
        System.out.print("Enter the filename to read from/decode: ");
        String f = sc.nextLine();

        // create File object and build text String
        File file = new File(f);
        Scanner input = new Scanner(file).useDelimiter("\\Z");
        String text = "";
        text += input.next();
        
        // close input file
        input.close();

        // capture file information from user and read file
        System.out.print("Enter the filename of document containing Huffman codes: ");
        f = sc.nextLine();

        // create File object and build text String
        file = new File(f);
        input = new Scanner(file).useDelimiter("\\Z");
        String codes = input.next();

        // close input file
        input.close();
        
        //Your work starts here********************************************
        ArrayList chars = new ArrayList();
        ArrayList charCodes = new ArrayList();
        
        Scanner ls = new Scanner(codes);
        // consume/discard header row and blank line
        ls.nextLine();
        ls.nextLine();
        while(ls.hasNextLine()) {
        	char c = ls.next().charAt(0);
        	ls.next(); // consume/discard probability
        	String s = ls.next();
        	// put the character and code somewhere useful
        	chars.add(c);
        	charCodes.add(s);
        }
       
        //An array of each word in codes
        String[] wordCodes = text.split("  ");
        
        //String to hold the final decoded message
        String message = "";
  
        //this loop builds the final message string
        //for each coded word
        for (int i = 0; i < wordCodes.length; i++) {
        	String[] letterCodes = wordCodes[i].split(" ");
        	//for each coded letter in each word
        	for (int j = 0; j < letterCodes.length; j++) {
        		//find the corresponding character
        		for (int k = 0; k < charCodes.size(); k++) {
        			if (letterCodes[j].equals(charCodes.get(k))) {
        				message = message + chars.get(k);
        			}
        		}
        	}
        	//add a space when the word is complete
        	message = message + " ";
        }
        
        System.out.println("Printing decoded text to Decoded.txt");
        PrintWriter output = new PrintWriter("Decoded.txt");
        output.print(message);
        output.close();
    }
    

    // the findEncoding helper method returns a String Array containing
    // Huffman codes for all characters in the Huffman Tree (characters not
    // present are represented by nulls)
    // this method was provided by Srini (Dr. Srini Sampalli). Two versions are below, one for Pairtree and one for BinaryTree
    private static String[] findEncoding(BinaryTree<Pair> bt){
        String[] result = new String[256];
        findEncoding(bt, result, "");
        return result;
    }

    
    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix){
        // test is node/tree is a leaf
        if (bt.getLeft()==null && bt.getRight()==null){
            a[bt.getData().getValue()] = prefix;
        }
        // recursive calls
        else{
            findEncoding(bt.getLeft(), a, prefix+"0");
            findEncoding(bt.getRight(), a, prefix+"1");
        }
    }
}
