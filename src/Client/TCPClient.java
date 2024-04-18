package Client;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) throws IOException {

        // Variables for setting up connection and communication
        Socket socket = null; // socket to connect with ServerRouter
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        String routerName = "10.101.129.70"; // ServerRouter host name
        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
            socket = new Socket(routerName, SockNum);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        }

        // Variables for message passing
        File file = new File("src/Client/file.txt");

        PrintWriter writer = new PrintWriter(file);
        //number of values to send passed to arg
        writer.println(randomDistinct(10));
        writer.println("Bye.");
        writer.close();

        Scanner sc = new Scanner(file);
        String fromServer; // messages received from ServerRouter
        String fromUser; // messages sent to ServerRouter
        String address = "10.101.131.53"; // destination IP (Server)
        long t0, t1, t;
        long startTime= System.currentTimeMillis();
        // Communication process (initial sends/receives
        out.println(address);// initial send (IP of the destination Server)
        fromServer = in.readLine();//initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromServer);

        //Variables for MP4 send
        File vfile = new File("src/Client/file.txt");       //creates file for mp4
        byte[] buffer = new byte[8192];                 //creates a byte buffer for video bytes

        InputStream dis = socket.getInputStream();
        OutputStream dos = socket.getOutputStream();

        int sentCount = 0;
        FileInputStream fis = new FileInputStream(vfile);

        //write file to dos
        while ((sentCount = fis.read(buffer)) != -1) {
            dos.write(buffer, 0, sentCount);
        }

        if (dos != null) {
            dos.flush();
        }


        //finally closes object
        if (dis != null) {
            dis.close();
        }
        if(dos != null){
            dos.close();
        }
        if(fis != null) {
            fis.close();
        }
        long totalTime = System.currentTimeMillis()-startTime;
        System.out.println("Runtime: " + totalTime);

        // closing connections
        out.close();
        in.close();

    }

    /**
     * Generates a list of 1024 random, non-repeating, integers and assigns them to the values field.
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
}
