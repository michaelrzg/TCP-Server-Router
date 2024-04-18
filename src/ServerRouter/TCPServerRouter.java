package ServerRouter;


    import java.net.*;
   import java.io.*;
    import java.util.Scanner;

    public class TCPServerRouter {
       public static void main(String[] args) throws IOException {
         Socket clientSocket = null; // socket for the thread
         Object [][] RoutingTable = new Object [10][2]; // routing table
           File routerData = new File("src/ServerRouter/routerData");// create routing table
           PrintWriter pw = new PrintWriter(new FileOutputStream(routerData,true));  //this way the edits from serverrouter will never overwrite another entry already in routing table
           int SockNum = 5555; // port number
			Boolean Running = true;
			int ind = 0; // indext in the routing table

			//Accepting connections
         ServerSocket serverSocket = null; // server socket for accepting connections
         try {
            serverSocket = new ServerSocket(5555);
            System.out.println("ServerRouter is Listening on port: 5555.");
         }
             catch (IOException e) {
               System.err.println("Could not listen on port: 5555.");
               System.exit(1);
            }
			// Creating threads with accepted connections
			while (Running == true)
			{
			try {
				clientSocket = serverSocket.accept();
                String [] ip = (clientSocket.getRemoteSocketAddress() + "").split(":");
                pw.println(ip[0].substring(1));  //this line updates the routing table with a new line holding the clients ip in slot one and a delimiter eg. [clients ip] , [slot for servers ip]
                pw.flush();
                RoutingTable[ind][0] = ip[0].substring(1);
                RoutingTable[ind][1]=clientSocket;
				SThread t = new SThread(RoutingTable, clientSocket, ind); // creates a thread with a random port
				t.start(); // starts the thread
				ind++; // increments the index
            System.out.println("ServerRouter connected with Client/Server: " + clientSocket.getInetAddress().getHostAddress());
         }
             catch (IOException e) {
               System.err.println("Client/Server failed to connect.");
               System.exit(1);
            }
			}//end while

			//closing connections
		   clientSocket.close();
         serverSocket.close();

      }
   }
