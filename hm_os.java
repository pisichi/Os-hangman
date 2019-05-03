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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

class hm_os 
{
    public static void main(String[] args) 
    {  
      ///////////////////////////client part

	  String host = "127.0.0.1";
      int port = 32000;

	  try (Socket socket = new Socket(host, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 String ans = in.readLine();

             GameBoard.movie = ans;
             System.out.println("Server replied " + ans);
			
			 GameBoard hm_os = new GameBoard();

			 while(GameBoard.check == 0){
				 System.out.print(""); // oh god why
			 }

			 if (GameBoard.check == 1){out.println("win");out.flush();}

			 else if (GameBoard.check == 2){out.println("lose");out.flush();}

			 
             
				
        } catch (IOException e) {
            e.printStackTrace();
        }
        



       
    }
}

 class GameBoard extends JFrame 
{

    /**
     * The maximum number of guesses before game over.
     */
     final int MAX_INCORRECT;
    
    
    /**
     * The directory of the images of the hangman.
     */
    final String HANGMAN_IMAGE_DIRECTORY;
    
    /**
     * The type of the images of the hangman.
     */
     final String HANGMAN_IMAGE_TYPE;
    
    /**
     * The base (common) name of the images of the hangman (e.g. "hangman" for
     * "hangman_0.png, hangman_1.png, ...")
     */
    final String HANGMAN_IMAGE_BASE_NAME;
    
    /**
     * The directory of the images of the letters.
     */
     final String LETTER_IMAGE_DIRECTORY;
    
    /**
     * The type of the images of the letters.
     */
     final String LETTER_IMAGE_TYPE;
    
    /**
     * The letter rack containing a the letters to be guessed.
     */
     LetterRack gameRack;
    
    /**
     * The hangman image placeholder.
     */
     Hangman gameHangman;
    
    /**
     * The number of incorrect guesses.
     */
     int numIncorrect;
    
    /**
     * Display the password hidden as *'s, revealing each letter as it is
     * guessed
     */
     JLabel correct;
    
    /**
     * Displays the number of incorrect guesses.
     */
     JLabel incorrect;



     JLabel background;
    
    /**
     * The password to be guessed by the player.
     */
     String password;

	 static String movie;

	 static int check = 0;
    
    /**
     * StringBuilder used to hide the password, revealing letters as they are
     * guessed by the player.
     */
    private StringBuilder passwordHidden;
    
    /**
     * The default constructor.
     */

   


    public GameBoard()
    {

        MAX_INCORRECT = 6;

        
        // The default directory for the sample images is images/ and the 
        //     default image type is .png; ensure this directory is
        //     created in the project folder if the sample images are used.
        HANGMAN_IMAGE_DIRECTORY = LETTER_IMAGE_DIRECTORY = "images/";
        HANGMAN_IMAGE_TYPE = LETTER_IMAGE_TYPE = ".png";
        HANGMAN_IMAGE_BASE_NAME = "hangman";
        
        setTitle("Phantom Hangman");
        setSize(500, 600);

        

        
        
        
        initialize();
    }
    
    /**
     * Initializes all elements of the GameBoard that must be refreshed upon
     * the start of a new game.
     */
    private void initialize()
    {   
        
        numIncorrect = 0;
        
        correct = new JLabel("Word: ");
        incorrect = new JLabel("Incorrect: " + numIncorrect);
        password = new String();
        passwordHidden = new StringBuilder();
        
        
        getPassword();
        //addBackground();
        addTextPanel();
        addLetterRack();
        addHangman();
        
        
        setVisible(true);
    }
    

    /**
     * Adds the correct and incorrect labels to the top of the GameBoard
     */
    private void addTextPanel()
    {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(1,2));
        textPanel.add(correct);
        //textPanel.add(incorrect);
        // use BorderLayout to set the components of the gameboard in
        //     "visually agreeable" locations
        add(textPanel, BorderLayout.NORTH);
        
    }


    void addBackground()
    {
        setLayout(new BorderLayout());
        ImageIcon img = new ImageIcon("bg.png");
        background = new JLabel("",img,JLabel.CENTER);
        background.setBounds(0,0,200,200);
        add(background,BorderLayout.CENTER);
        //background.setLayout(new FlowLayout());
  
    }
    





    
    /**
     * Adds the LetterRack to the bottom of the GameBoard and attaches
     * the LetterTile TileListeners to the LetterTiles.
     */
    private void addLetterRack()
    {
        gameRack = new LetterRack(password, 
                LETTER_IMAGE_DIRECTORY, 
                LETTER_IMAGE_TYPE);
        gameRack.attachListeners(new TileListener());
        add(gameRack, BorderLayout.SOUTH);
    }
    



    /**
     * Adds a panel that contains the hangman images to the middle of the
     * GameBoard.
     */
    private void addHangman()
    {
        JPanel hangmanPanel = new JPanel();
        gameHangman = new Hangman(HANGMAN_IMAGE_BASE_NAME,
                HANGMAN_IMAGE_DIRECTORY,
                HANGMAN_IMAGE_TYPE);
        hangmanPanel.add(gameHangman);
        add(hangmanPanel, BorderLayout.CENTER);
    }






    
    public void getPassword()
    {  
        
	    password = movie;

        passwordHidden.append(password.replaceAll(".", "*"));
        correct.setText(correct.getText() + passwordHidden.toString());
    }
    





    class TileListener implements MouseListener 
    {
        @Override
        public void mousePressed(MouseEvent e) 
        {
            Object source = e.getSource();
            if(source instanceof LetterTile)
            {
                char c = ' ';
                int index = 0;
                boolean updated = false;
                
                // cast the source of the click to a LetterTile object
                LetterTile tilePressed = (LetterTile) source;
                c = tilePressed.guess();
                
                // reveal each instance of the character if it appears in the
                //     the password
                while ((index = password.toLowerCase().indexOf(c, index)) != -1)
                {
                    passwordHidden.setCharAt(index, password.charAt(index));
                    index++;
                    updated = true;
                }
                
                // if the guess was correct, update the GameBoard and check
                //     for a win
                if (updated)
                {
                    correct.setText("Word: " + passwordHidden.toString());
                    
                    if (passwordHidden.toString().equals(password))
                    {
                        gameRack.removeListeners();
                        gameHangman.winImage();	 
                        check = 1;
						System.out.print("win");
                    
                    }
                }
                
                // otherwise, add an incorrect guess and check for a loss
                else
                {
                    incorrect.setText("Incorrect: " + ++numIncorrect);
                    
                    if (numIncorrect >= MAX_INCORRECT)
                    {   
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
        public void mouseClicked(MouseEvent e) {}  

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}
        
        @Override
        public void mouseExited(MouseEvent e) {}
    }
}





 





