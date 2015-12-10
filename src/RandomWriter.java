import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class RandomWriter
{
   private static final String USAGE   = "Usage: java RandomWriter "
         + "<Seed size> <Length> <Optional: 1 or More Files>";
   private static final int    MINARGS = 2;


   /**
    * Learns the patterns of either books or from System.in
    * 
    * @param sampleSize
    *           the size of a phrase use as a key
    * @param fileArray
    *           an array of characters that can show up after a phrase
    * @return a hashmap of phrases for keys that hold what characters can appear
    *         after that phrase
    * @throws IOException
    *            is thrown when a file cannot be read
    */
   private static HashMap<String, ArrayList<Character>> learnPatterns(
         int sampleSize, InputStreamReader[] fileArray) throws IOException
   {
      char value;
      int valueInt;

      StringBuilder key = new StringBuilder();
      HashMap<String, ArrayList<Character>> patterns = new HashMap<String, ArrayList<Character>>();

      /*
       * For each book added as an argument, each of them need to be walked to
       * build our pattern; if it is system.in, it'll only run once.
       */
      for (InputStreamReader file : fileArray)
      {
         /*
          * Read in # of characters equal to the sampleSize into the string
          * builder to prime adding to the HashMap
          */
         for (int i = 0; i < sampleSize; i++)
         {
            key.append((char) file.read());
         }


         /*
          * While we're not at the end of file, keep reading in a character at a
          * time
          * 
          * Add that character to the ArrayList associated with the current key
          * Then append that character to the key, and remove the first
          * character of the key, creating a new key for the next pass.
          */
         while ((valueInt = file.read()) != -1)
         {

            value = (char) valueInt;
            addToArrayList(key.toString(), value, patterns);
            key.append(value);
            key.deleteCharAt(0);
         }

         /*
          * Empty the key if there's more than one book, so a new key is
          * generated at the start
          */
         key.delete(0, key.length());

      }

      return patterns;
   }


   /**
    * Adds an entry to an ArrayList that is stored as a value in a HashMap
    * 
    * @param key
    *           the key to the value to be modified
    * @param letter
    *           the value to add to the array
    * @param map
    *           the HashMap to be modified
    */
   private static void addToArrayList(String key, Character letter,
         HashMap<String, ArrayList<Character>> map)
   {
      // Get the ArrayList from the appropriate key
      ArrayList<Character> values = map.get(key);


      /*
       * If there's not an ArrayList already created for the associated key,
       * create one Then add the character to the ArrayList.
       */
      if (values == null)
      {
         values = new ArrayList<Character>();
         values.add(letter);
         map.put(key, values);
      }
      /*
       * Otherwise, we already have an ArrayList, so just add it to the
       * ArrayList
       */
      else
      {
         values.add(letter);
      }

   }


   /**
    * Generates text based on a learned pattern of letters.
    * 
    * @param length
    *           the size of the phrase to be generated
    * @param patterns
    *           database of phrases and letters that follow that phrase
    * @return the generated phrase
    */
   private static String generatePhrase(int length,
         HashMap<String, ArrayList<Character>> patterns)
   {
      String[] keyArray;
      StringBuilder key;

      ArrayList<Character> values;
      char value;

      int printed;
      Random randomizer;

      // What will be returned
      StringBuilder phrase;


      values = new ArrayList<Character>();
      randomizer = new Random();

      phrase = new StringBuilder();
      printed = 0;

      // Create an array of the keys for easier use of using Random
      keyArray = patterns.keySet()
            .toArray(new String[patterns.keySet().size()]);


      // Get the first key
      key = new StringBuilder(keyArray[randomizer.nextInt(keyArray.length)]);

      // Start the printout with the first key, and increase the number printed
      phrase.append(key);
      printed += key.length();


      // While we've not printed the amount requested
      while (printed <= length)
      {
         // We'll get the values stored at the key
         values = patterns.get(key.toString());

         /*
          * If we ever get key with no values, then we need to rerandomize the
          * program until we get something that has a next character
          */
         while (values == null)
         {
            key = new StringBuilder(
                  keyArray[randomizer.nextInt(keyArray.length)]);

            values = patterns.get(key.toString());
            if (values != null)
            {
               phrase.append(key);
               printed += key.length();
            }
         }

         /*
          * A value is randomly returned from collection of possible values
          * located in the ArrayList that our key held
          */
         value = values.get(randomizer.nextInt(values.size()));

         // The character is appended to the phrase to be returned
         phrase.append(value);

         /*
          * The key is then shifted, removing the first character and sticking
          * the new character at the end.
          */
         key.deleteCharAt(0);
         key.append(value);

         // Update how many characters have been printed.
         printed += 1;
      }


      return phrase.toString();
   }


   /**
    * Program that learns a pattern of phrases and typical letter than follows
    * those phrases
    * 
    * @param args
    *           the first argument is how large the sample size should be. the
    *           second argument is how long the printed phrase should be. any
    *           additional arguments are the .txt to read from.
    */
   public static void main(String[] args)
   {
      InputStreamReader[] fileArray;

      PrintStream output = System.out;
      int sampleSize = 0;
      int length = 0;

      // Check # of arguments to make sure we got the minimum required
      if (args.length < MINARGS)
      {
         System.err.println("Not enough arguments");
         System.out.println(USAGE);
         System.exit(1);
      }


      // Check the first argument for valid int
      try
      {
         sampleSize = Integer.parseInt(args[0]);
         if (sampleSize <= 0)
         {
            throw new NumberFormatException();
         }
      }
      catch (NumberFormatException e)
      {
         System.err.println("Seed size not a valid positive integer.");
         System.out.println(USAGE);
         System.exit(1);
      }


      // Check second argument for a valid int
      try
      {
         length = Integer.parseInt(args[1]);
         if (length <= 0)
         {
            throw new NumberFormatException();
         }
      }
      catch (NumberFormatException e)
      {
         System.err.println("Length not a valid positive integer.");
         System.out.println(USAGE);
         System.exit(1);
      }


      /*
       * If we have more than two arguments, we must make sure the files are
       * valid
       */
      if (args.length > MINARGS)
      {
         // Set up the array for the number of files being read.
         fileArray = new FileReader[args.length - MINARGS];
         /*
          * The loop starts at 2, to ignore the first two arguments which we've
          * already handled. We go until each file has been validated, and stick
          * it in an array.
          */
         for (int i = 2; i < args.length; i++)
         {
            try
            {
               fileArray[i - 2] = new FileReader(new File(args[i]));
            }
            catch (FileNotFoundException e)
            {
               System.err.println("File not found.");
               System.out.println(USAGE);
               System.exit(1);
            }
         }
      }
      /*
       * If there's no more arguments other than the two required, we read from
       * System in
       */
      else
      {
         fileArray = new InputStreamReader[] {
               new InputStreamReader(System.in) };
      }

      /*
       * If for some reason we can't read from a stream, we'll get an
       * IOException. This probably won't happen, but we have to catch it.
       */
      try
      {
         /*
          * Now that we have where we're reading from, we can process the
          * patterns, and print them.
          */
         output.print(
               generatePhrase(length, learnPatterns(sampleSize, fileArray)));
      }
      catch (IOException e)
      {
         System.out.println("Unable to read from provided stream.");
         System.exit(1);
      }
   }


}
