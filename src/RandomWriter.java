import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class RandomWriter
{
   private static final String USAGE = "Usage: java RandomWriter "
         + "<Seed size> <Length> <Optional: 1 " + "or More Files>";


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
       * build our pattern
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
          * Add that character to the ArrayList associated with the key Then
          * append that character to the key, and remove the first character of
          * the key
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
    * @return the modified Hashmap
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
    * @param patterns list of phrases and letters that follow that phrase
    * @return
    */
   private static String generatePhrase(int length,
         HashMap<String, ArrayList<Character>> patterns)
   {

      String[] keys;
      StringBuilder key;
      char value;

      ArrayList<Character> values = new ArrayList<Character>();
      Random randomizer = new Random(System.currentTimeMillis());

      StringBuilder phrase = new StringBuilder();
      int printed = 0;

      Set<String> keysSet = patterns.keySet();
      keys = keysSet.toArray(new String[keysSet.size()]);


      key = new StringBuilder(keys[randomizer.nextInt(keys.length)]);

      phrase.append(key);
      printed += key.length();
      while (printed <= length)
      {
         values = patterns.get(key.toString());
         while(values==null)
         {
            key = new StringBuilder(keys[randomizer.nextInt(keys.length)]);
            
            values = patterns.get(key.toString());
            if(values != null)
            {
               phrase.append(key);
               printed += key.length();
            }
         }
         value = values.get(randomizer.nextInt(values.size()));
         
         phrase.append(value);

         key.deleteCharAt(0);
         key.append(value);

         // Update how many characters have been printed.
         printed += 1;
      }


      return phrase.toString();
   }


   /**
    * @param args
    */
   public static void main(String[] args)
   {
      InputStreamReader[] fileArray;
      HashMap<String, ArrayList<Character>> patterns;


      PrintStream output = System.out;
      int sampleSize = 0;
      int length = 0;

      // Check # of args(args.length >= 2)
      if (args.length < 2)
      {
         System.err.println("Not enough arguments");
         System.out.println(USAGE);
         System.exit(1);
      }


      // Check first arg for a valid int
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


      // Check second arg for a valid int
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


      // If more than 2 args, make sure its a valid file
      // If so, store in array
      if (args.length > 2)
      {
         fileArray = new FileReader[args.length - 2];
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
      else
      {
         fileArray = new InputStreamReader[1];
         fileArray[0] = new InputStreamReader(System.in);
      }

      try
      {
         patterns = learnPatterns(sampleSize, fileArray);
         output.print(generatePhrase(length, patterns));
      }
      catch (IOException e)
      {
         System.out.println("Unable to read from provided stream.");
         System.exit(1);
      }
   }


}
