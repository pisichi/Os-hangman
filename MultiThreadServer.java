
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Random; 


public class MultiThreadServer {
    public static void main(String[] args) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(32000);
            server.setReuseAddress(true);

            // The main thread is just accepting new connections
            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);

                // The background thread will handle each client separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
			            Scanner scanner = new Scanner(System.in);

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
				String[] movie = new String[3];

					movie[0] = "oasdkoasd";
					movie[1] = "asdasd";
					movie[2] = "totototo";

				    Random rand = new Random(); 


				while (true) {
			    int r = rand.nextInt(3);
                String ans = movie[r];
                out.println(ans);
                out.flush();

                    line = in.readLine();
					if(line!=null)
                    System.out.printf("Sent from the client: %s\n", line);

					
                }
            } catch (IOException e) {
                //e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null)
                        in.close();
                    clientSocket.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }
}