import java.io.*;
import java.util.HashSet;
import java.util.Random;
class dataGen{
/**
     * Generates a list of n random, non-repeating, integers and assigns them to the values field.
     */
    public static String randomDistinct(int n) {
        /* Used to track what ints have been used */
        HashSet<Integer> isUsed = new HashSet<>();
        String newList = "";
        Random random = new Random();

        /* Loops 1024 times and assigns a new unique value each time. */
        for (int i = 0; i < n; i++) {
            int newValue = -1;

            /* Ensures that there is no duplicate values */
            do {
                newValue = random.nextInt(999999) + 1;
            } while (isUsed.contains(newValue));

            /* Adds the value to the known list and also to the generated list. */
            newList+= newValue + ",";
            isUsed.add(newValue);
        }
        newList = newList.substring(0, newList.length()-1);
        /* Sorts and assigns the values generated to the field. */
        return newList;
    }
    public static void main(String [] args){
        File file;
        PrintWriter writer;
        try{
        file = new File("generated_data.txt");
        writer = new PrintWriter(file);
         //number of values to send passed to arg
        writer.println(randomDistinct(Integer.parseInt(args[0])));
        writer.println("Bye.");
        writer.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        catch (Exception e){
            System.err.println("Incorrect Usage. Usage:\n java dataGen.java <# of desired datapoints>");
            return;
        }
        System.err.println("Generated " +args[0] +" datapoints and stored them in 'generated_data.txt'." );
       
    }
}