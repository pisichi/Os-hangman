

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        String host = "127.0.0.1";
        int port = 32000;
        try (Socket socket = new Socket(host, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
			String movie  = null;



            while (!"exit".equalsIgnoreCase(movie)) {

				
			   String ans = in.readLine();



                
				if (ans.length() > 5)
				{out.println("win");
				}


				else {
				out.println("lose");
				}
				
                
                System.out.println("Server replied " + ans);
				
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}