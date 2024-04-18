package ServerRouter;

import java.io.*;
import java.net.*;

public class SThread extends Thread {
    private final Object[][] RTable; // routing table
    private final PrintWriter out; // writers (for writing back to the machine and to destination)
    private OutputStream outToClient; // Transmits the data to the destination.
    private final BufferedReader in; // reader (for reading from the machine connected to)
    private Socket outSocket; // socket for communicating with a destination
    private final InputStream inFromClient; // Receives data from the source.


    // Constructor
    public SThread(Object[][] routerTable, Socket toClient, int index) throws IOException {
        out = new PrintWriter(toClient.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
        RTable = routerTable;
        outSocket = toClient;
        inFromClient = toClient.getInputStream();
    }

    // Run method (will run for each machine that connects to the ServerRouter)
    public void run() {
        try {
            // Initial sends/receives
            // communication strings
            String destination = in.readLine(); // initial read (the destination for writing)
            System.out.println("Forwarding to " + destination);
            out.println("Connected to the router."); // confirmation of connection
            // waits 10 seconds to let the routing table fill with all machines' information
            try {
                sleep(4000);
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted");
            }

            // loops through the routing table to find the destination
            for (int i = 0; i < 10; i++) {
                if (destination.equals((String) RTable[i][0])) {
                    outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
                    System.out.println("Found destination: " + destination);
                    outToClient = outSocket.getOutputStream(); // assigns a writer
                }
            }

            // Communication loop
            byte[] buffer = new byte[8192];
            int count;

            while ((count = inFromClient.read(buffer)) > 0) {
                if (outToClient != null) {
                    outToClient.write(buffer, 0, count);
                }
            }

            outToClient.flush();
            inFromClient.close();
            outToClient.close();
            if (!outSocket.isClosed()) {
                outSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Could not listen to socket.");
        }
    }
}
