package Server;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Vector;

public class TCPServer {
    public static Integer[] merge(Integer[] output, Integer[] first, Integer[] second) {
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < first.length && j < second.length) {

            if (first[i] > second[j]) {
                output[k] = second[j];
                j++;
            } else {
                output[k] = first[i];
                i++;
            }
            k++;

        }

        if (i == first.length) {
            while (j < second.length) {
                output[k] = second[j];
                j++;
                k++;
            }
        } else {
            while (i < first.length) {
                output[k] = first[i];
                i++;
                k++;
            }
        }
        return output;
    }

    public static void mergeAll(Integer[] output, Vector<Integer[]> input) {
        //counters array acts as int bit map to keep track of each subarrays index
        int[] counters = new int[input.size()];
        //2 vars to keep track of current smallest val and its index in the given array
        //i used these values so it will be clear if either is causing a bug
        int smallest = 9999;
        int index = -1;
        //outer loop counts index of output array
        for (int i = 0; i < output.length; i++) {
            //inner loop works through each subarray in the vector
            for (int j = 0; j < input.size(); j++) {
                //first check to see if the index will cause an exceptoin (array is out of novel values)
                if (counters[j] == input.get(j).length) {
                    continue;
                }
                //else check if given arrays value is smaller than running smallest
                else if (input.get(j)[counters[j]] < smallest) {
                    //update running smallest and its index
                    smallest = input.get(j)[counters[j]];
                    index = j;
                }
            }
            //finally update the current index of output array with correct smallest value
            output[i] = smallest;
            counters[index]++;
            //reset index and smallest counters
            index = -1;
            smallest = Integer.MAX_VALUE;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //set variable number of threads :


        final int NUM_OF_THREADS = 8;


        // Variables for setting up connection and communication
        Socket Socket = null; // socket to connect with ServerRouter
        PrintWriter out = null; // for writing to ServerRouter
        BufferedReader in = null; // for reading form ServerRouter
        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress(); // Server machine's IP
        String routerName = "10.101.129.70"; // ServerRouter host name
        int SockNum = 5555; // port number

        // Tries to connect to the ServerRouter
        try {
            Socket = new Socket(routerName, SockNum);
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        }

        // Variables for message passing
        String fromServer; // messages sent to ServerRouter
        String fromClient; // messages received from ServerRouter
        String address = "10.101.187.213"; // destination IP (Client)

        // Communication process (initial sends/receives)

        out.println(address);// initial send (IP of the destination Client)
        fromClient = in.readLine();// initial receive from router (verification of connection)
        System.out.println("ServerRouter: " + fromClient);
        // Communication while loop
        Vector <String> s = new Vector<>();
               while ((fromClient = in.readLine()) != null) {
                   s.add(fromClient);
                   //System.out.println("Client said: " + fromClient);
                   if (fromClient.equals("Bye.")) // exit statement
                       break;
               }
               fromClient=s.get(0);
        //array is now received by the server and is in the string fromClient, split into array
        String[] splitString = fromClient.split(",");
        //create a wrapper object array to store values
        Integer[] arrayValues = new Integer[splitString.length];
        //loop through and insert values into Integer object array to pass by reference
        // this is o(n) overhead, maybe find better way to copy over values
        for (int i = 0; i < arrayValues.length; i++) {
            arrayValues[i] = Integer.parseInt(splitString[i]);
        }
        //create thread pool
        Thread[] threadpool = new Thread[NUM_OF_THREADS];
        //assign work
        //creates a vector of integer arrays
        // start index is the first endpoint, will be a factor of n
        Vector<Integer[]> workDiv = new Vector<>();
        int startIndex = (arrayValues.length / NUM_OF_THREADS);
        if (startIndex % arrayValues.length != 0) {
            startIndex++;
        }
        //inc below stores the increment to move up at each itteration of while loop
        int inc = startIndex;
        int prev = 0;
        boolean f = true;
        long start=System.currentTimeMillis();
        //below is some logic to ensure the right # of threads gets to the while loop
        //assumes that >2 threads will make it to the while loop
        //if there are less values than threads, all work will be done by a single thread to avoid overhead of spliting and merging
        if (NUM_OF_THREADS == 1 || arrayValues.length<threadpool.length) {
            //create thread and pass it the full array
            threadpool[0] = new Thread(new mergeSort(arrayValues));
            //start thread
            threadpool[0].start();
            //a timeer to ensure thread finishes before we continue throgh to prints
            while(threadpool[0].isAlive());
        } else if (NUM_OF_THREADS == 2) {
            //since we have 2 threds, we need 2 subarrays
            //divide the input array (arrayValues) into 2 subarrays of each half
            Integer[] first = Arrays.copyOfRange(arrayValues, 0, arrayValues.length / 2);
            Integer[] second = Arrays.copyOfRange(arrayValues, (arrayValues.length / 2), arrayValues.length);
            //create threads for each half
            threadpool[0] = new Thread(new mergeSort(first));
            threadpool[1] = new Thread(new mergeSort(second));
            //start threads
            threadpool[0].start();
            threadpool[1].start();
            //give timeer to ensure they finish
            while(threadpool[0].isAlive() || threadpool[1].isAlive());
            //finally, merge the two
            merge(arrayValues, first, second);

        } else {
            //  atp 3 or more threads exist
            //loop goes through and splits work into as many threads as possible
            //adds the arrays into the vector arrayValues
            //if n/threads % 2 != 0, then the last array in the vector will be < inc value
            while (f) {
                //split work into 1/n parts
                Integer[] work = Arrays.copyOfRange(arrayValues, prev, startIndex);
                //insert work into vector
                workDiv.add(work);
                //update prev and start index to next values
                prev = startIndex;
                startIndex += inc;
                //check if the next itteration will cause condition to fail
                if (startIndex + inc >= arrayValues.length) {
                    //if so the below will create an array of all the remainng values that will not fill an array
                    Integer[] ww = Arrays.copyOfRange(arrayValues, prev, startIndex);
                    prev = startIndex;
                    //add the last full sized array to the vector
                    workDiv.add(ww);
                    //make the index to stop equal the last value of the array
                    startIndex = arrayValues.length;
                    //copy the last values that will not full an array into their own smaller arary and add
                    Integer[] w1 = Arrays.copyOfRange(arrayValues, prev, startIndex);
                    workDiv.add(w1);
                    //stop the loop
                    f = false;
                }
            }

            //activate all threads
            for (int i = 0; i < Math.min(threadpool.length, workDiv.size()) ; i++) {
                threadpool[i] = new Thread(new mergeSort(workDiv.get(i)));
                threadpool[i].start();
            }
            // small pause to wait for threads to return to us. (could make these threads synchronous)
            boolean [] alive=new boolean[Math.min(NUM_OF_THREADS, workDiv.size())];
            boolean allAlive=true;
            while (allAlive){
                for( int i=0;i< alive.length;i++){
                    if(!threadpool[i].isAlive()) {
                        alive[i] = true;
                    }
                }
                allAlive=false;
                for (boolean I : alive){
                    if(!I){
                        allAlive=true;
                        break;
                    }
                }

            }
            //now combine the arrays
            mergeAll(arrayValues, workDiv);
        }
        start = System.currentTimeMillis()-start;
        System.out.println("Total time: "+(start));

        // closing connections
        out.close();
        in.close();
        Socket.close();
    }

}
