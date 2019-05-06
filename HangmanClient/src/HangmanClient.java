import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.Font;
import javax.swing.JApplet;

class HangmanClient {
	public static void main(String[] args) {

		String host = JOptionPane.showInputDialog("Server hostname/IP");
		int port = 32000;

		try (Socket socket = new Socket(host, port)) {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String ans = in.readLine();

			GameBoard.movie = ans;

			GameBoard hm_os = new GameBoard();

			while (GameBoard.check == 0) {
				System.out.print(""); // oh god why
			}

			if (GameBoard.check == 1) {
				out.println("win");
				out.flush();
			}

			else if (GameBoard.check == 2) {
				out.println("lose");
				out.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

class GameBoard extends JFrame {

	final int MAX_INCORRECT;

	final String HANGMAN_IMAGE_DIRECTORY;

	final String HANGMAN_IMAGE_TYPE;

	final String HANGMAN_IMAGE_BASE_NAME;

	final String LETTER_IMAGE_DIRECTORY;

	final String LETTER_IMAGE_TYPE;

	LetterRack gameRack;

	Hangman gameHangman;

	int numIncorrect;

	JLabel correct;

	JLabel background;

	String password;

	static String movie;

	static int check = 0;

	private StringBuilder passwordHidden;

	public GameBoard() {

		MAX_INCORRECT = 6;

		HANGMAN_IMAGE_DIRECTORY = LETTER_IMAGE_DIRECTORY = "images/";
		HANGMAN_IMAGE_TYPE = LETTER_IMAGE_TYPE = ".png";
		HANGMAN_IMAGE_BASE_NAME = "hangman";

		setTitle("OS_Hangman");
		setLayout(new BorderLayout(5, 5));
		setSize(500, 650);

		initialize();
	}

	private void initialize() {
		check = 0;
		numIncorrect = 0;

		correct = new JLabel("Movie: ", SwingConstants.CENTER);
		Font font = new Font("Courier", Font.BOLD, 20);
		correct.setFont(font);

		password = new String();
		passwordHidden = new StringBuilder();

		getPassword();

		addTextPanel();
		addLetterRack();
		addHangman();

		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void addTextPanel() {
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(1, 1));

		textPanel.add(correct);

		add(textPanel, BorderLayout.NORTH);

	}

	private void addLetterRack() {
		gameRack = new LetterRack(password, LETTER_IMAGE_DIRECTORY, LETTER_IMAGE_TYPE);
		gameRack.attachListeners(new TileListener());
		add(gameRack, BorderLayout.SOUTH);
	}

	private void addHangman() {
		JPanel hangmanPanel = new JPanel();
		gameHangman = new Hangman(HANGMAN_IMAGE_BASE_NAME, HANGMAN_IMAGE_DIRECTORY, HANGMAN_IMAGE_TYPE);
		hangmanPanel.add(gameHangman);
		add(hangmanPanel, BorderLayout.CENTER);
	}

	public void getPassword() {

		password = movie;

		passwordHidden.append(password.replaceAll(".", "*"));
		correct.setText(correct.getText() + passwordHidden.toString());
	}

	class TileListener implements MouseListener {
		@Override
		public void mousePressed(MouseEvent e) {
			Object source = e.getSource();
			if (source instanceof LetterTile) {
				char c = ' ';
				int index = 0;
				boolean updated = false;

				LetterTile tilePressed = (LetterTile) source;
				c = tilePressed.guess();

				while ((index = password.toLowerCase().indexOf(c, index)) != -1) {
					passwordHidden.setCharAt(index, password.charAt(index));
					index++;
					updated = true;
				}

				if (updated) {
					correct.setText("Movie: " + passwordHidden.toString());

					if (passwordHidden.toString().equals(password)) {
						gameRack.removeListeners();
						gameHangman.winImage();
						check = 1;
						System.out.print("win");

					}
				}

				else {
					++numIncorrect;

					if (numIncorrect >= MAX_INCORRECT) {
						check = 2;
						gameHangman.loseImage();
						gameRack.removeListeners();
						System.out.print("lose");

					}

					else
						gameHangman.nextImage(numIncorrect);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
}
