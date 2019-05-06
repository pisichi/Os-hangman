
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Random;

public class HangmanServer {
	public static void main(String[] args) {

		ServerSocket server = null;
		try {
			server = new ServerSocket(32000);
			server.setReuseAddress(true);
			System.out.println("Server starts at port " + server.getLocalPort());

			// The main thread is just accepting new connections
			while (true) {
				Socket client = server.accept();
				System.out.println("New client connected " + client.getInetAddress().getHostAddress());
				ClientHandler clientSock = new ClientHandler(client);

				// The background thread will handle each client separately
				new Thread(clientSock).start();
			}
		} catch (IOException e) {
			// e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					// e.printStackTrace();
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
				String[] movies = { "glass", "goosebumps", "avengers", "samson", "hellboy", "replicas", "downsizing",
						"shazam", "iceman", "overlord", "axl", "dumbo", "us", "wildlife", "intruders", "viral", "peppermint", "bumblebee",
						"kursk", "aquaman", "dinosaur", "alita", "malicious", "piranha", "bikeman", "halloween",
						"slither", "headhunters", "amnesiac", "mcqueen", "reset", "visions", "courageous", "venom",
						"igor", "megamind", "drive", "patrick", "blindspotting", "kin", "searching", "hereditary",
						"unless", "chuck", "alpha", "monstrum", "redline", "seuls", "antigang", "incredibles",
						"winchester", "yojimbo", "combustion", "antman", "skyscraper", "submergence", "jungle",
						"deadpool", "restless", "dreamgirls", "taxi", "zoom", "fireworks", "feast", "haunt", "beirut",
						"inkheart", "hannibal", "automata", "paddington", "devil", "rampage", "amour", "next", "arahan",
						"slumber", "gringo", "defiance", "triangle", "anon", "leatherface", "paradox", "colossal",
						"renegades", "jackals", "julieta" };
				String movie = movies[(int) (Math.random() * movies.length)];
				while (true) {
					String ans = movie;
					out.println(ans);
					out.flush();
					line = in.readLine();
					if (line != null)
						System.out.printf("Sent from the client: %s\n", line);
				}
			} catch (IOException e) {
				System.err.println("Server Error: " + e.getMessage());
				e.printStackTrace(System.err);
			} finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null)
						in.close();
					clientSocket.close();
				} catch (IOException e) {
					System.err.println("Server Error: " + e.getMessage());
					e.printStackTrace(System.err);
				}
			}
		}
	}
}