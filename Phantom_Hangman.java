import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.BorderFactory;

class Phantom_Hangman 
{
    public static void main(String[] args) 
    {
        GameBoard newPhantomHangman = new GameBoard();
    }
}

 class GameBoard extends JFrame 
{
    /**
     * The width of the GameBoard.
     */
    private final int WIDTH;
    
    /**
     * The height of the GameBoard.
     */
    private final int HEIGHT;
    
    /**
     * The maximum number of guesses before game over.
     */
    private final int MAX_INCORRECT;
    
    /**
     * The maximum password length permitted.
     */
    private final int MAX_PASSWORD_LENGTH;
    
    /**
     * The directory of the images of the hangman.
     */
    private final String HANGMAN_IMAGE_DIRECTORY;
    
    /**
     * The type of the images of the hangman.
     */
    private final String HANGMAN_IMAGE_TYPE;
    
    /**
     * The base (common) name of the images of the hangman (e.g. "hangman" for
     * "hangman_0.png, hangman_1.png, ...")
     */
    private final String HANGMAN_IMAGE_BASE_NAME;
    
    /**
     * The directory of the images of the letters.
     */
    private final String LETTER_IMAGE_DIRECTORY;
    
    /**
     * The type of the images of the letters.
     */
    private final String LETTER_IMAGE_TYPE;
    
    /**
     * The letter rack containing a the letters to be guessed.
     */
    private LetterRack gameRack;
    
    /**
     * The hangman image placeholder.
     */
    private Hangman gameHangman;
    
    /**
     * The number of incorrect guesses.
     */
    private int numIncorrect;
    
    /**
     * Display the password hidden as *'s, revealing each letter as it is
     * guessed
     */
    private JLabel correct;
    
    /**
     * Displays the number of incorrect guesses.
     */
    private JLabel incorrect;
    
    /**
     * The password to be guessed by the player.
     */
    private String password;
    
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
        WIDTH = 500;
        HEIGHT = 500;
        MAX_INCORRECT = 6;
        MAX_PASSWORD_LENGTH = 10;
        
        // The default directory for the sample images is images/ and the 
        //     default image type is .png; ensure this directory is
        //     created in the project folder if the sample images are used.
        HANGMAN_IMAGE_DIRECTORY = LETTER_IMAGE_DIRECTORY = "images/";
        HANGMAN_IMAGE_TYPE = LETTER_IMAGE_TYPE = ".png";
        HANGMAN_IMAGE_BASE_NAME = "hangman";
        
        setTitle("Phantom Hangman");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        
        
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
        addTextPanel();
        addLetterRack();
        addHangman();
        
        // set board slightly above middle of screen to prevent dialogs
        //     from blocking images
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getSize().width / 2,
                dim.height / 2 - getSize().height / 2 - 200);
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
        textPanel.add(incorrect);
        // use BorderLayout to set the components of the gameboard in
        //     "visually agreeable" locations
        add(textPanel, BorderLayout.NORTH);
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






    
    /**
     * Retrieves the password from the player, constrained by the length and
     * content of the password.
     */

    private void getPassword()
    {

                password = "abc";

        passwordHidden.append(password.replaceAll(".", "*"));
        correct.setText(correct.getText() + passwordHidden.toString());
    }
    





    /**
     * Prompts the user for a new game when one game ends.
     */
    private void newGameDialog()
    {
        int dialogResult = JOptionPane.showConfirmDialog(null, 
                "The password was: " + password +
                "\nWould You Like to Start a New Game?",
                "Play Again?",
                JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION)
            initialize(); // re-initialize the GameBoard
        else
            System.exit(0);
    }





   
    /**
     * A custom MouseListener for the LetterTiles that detects when the user
     * "guesses" (clicks on) a LetterTile and updates the game accordingly.
     */
    private class TileListener implements MouseListener 
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
                        newGameDialog();
                    }
                }
                
                // otherwise, add an incorrect guess and check for a loss
                else
                {
                    incorrect.setText("Incorrect: " + ++numIncorrect);
                    
                    if (numIncorrect >= MAX_INCORRECT)
                    {
                        gameHangman.loseImage();
                        gameRack.removeListeners();
                        newGameDialog();
                    }
                    
                    else
                        gameHangman.nextImage(numIncorrect);
                }
            }
        }
        
        // These methods must be implemented in every MouseListener
        //     implementation, but since they are not used in this application, 
        //     they contain no code
        
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





 class LetterTile extends JLabel
{
    /**
     * The letter to be displayed on the tile
     */
    private final char IMAGE_LETTER;
    
    /**
     * The directory containing the letter images.
     */
    private final String IMAGE_DIRECTORY;
    
    /**
     * The type of image (.jpg, .png, etc. to include the period).
     */
    private final String IMAGE_TYPE;
    
    /**
     * The preferred width of the tiles.
     */
    private final int PREFERRED_WIDTH;
    
    /**
     * The preferred height of the tiles.
     */
    private final int PREFERRED_HEIGHT;
    
    /**
     * The current path of the current image.
     */
    private String path;
    
    /**
     * The current image being displayed.
     */
    private BufferedImage image;
    
    /**
     * A custom MouseListener from the GameBoard class to change the tiles on
     * a click.
     */
    private MouseListener tileListener;

    /**
     * The default constructor.
     */
    public LetterTile() { this('a', "images/", ".png"); }
    
    /**
     * Creates a new LetterTile given the letter to be displayed, the directory
     * of the letter image series, and the image type.
     * @param imageLetter The letter to be displayed on the tile.
     * @param imageDirectory The directory holding the letter images.
     * @param imageType The type of the letter images.
     */
    public LetterTile(char imageLetter, String imageDirectory, String imageType)
    {
        IMAGE_LETTER = imageLetter;
        IMAGE_DIRECTORY = imageDirectory;
        IMAGE_TYPE = imageType;
        
        PREFERRED_WIDTH = PREFERRED_HEIGHT = 50;
        
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        path = IMAGE_DIRECTORY + IMAGE_LETTER + IMAGE_TYPE;
        image = loadImage(path);
    }
    
    /**
     * Loads an image from a file.
     * @param imagePath The path to load an image from.
     * @return A BufferedImage object on success, exits on failure.
     */
    private BufferedImage loadImage(String imagePath)
    {
        BufferedImage img = null;

        try 
        {
            img = ImageIO.read(new File(imagePath));
        } 

        catch (IOException ex) 
        {
            System.err.println("loadImage(): Error: Image at "
                    + imagePath + " could not be found");
            System.exit(1);
        }

        return img;
    }
    
    /**
     * Changes the tile's appearance and removes the mouse listener to prevent
     * the tile from being clicked again.
     * @return The tile's letter.
     */
    public char guess() 
    { 
        loadNewImage("guessed");
        removeTileListener();
        return IMAGE_LETTER;
    }
    
    /**
     * Loads a new image in the hangman image series.
     * @param suffix The suffix of the image name.
     */
    private void loadNewImage(String suffix)
    {
        path = IMAGE_DIRECTORY + IMAGE_LETTER + "_" + suffix + IMAGE_TYPE;
        image = loadImage(path);
        repaint();  
    }
    
    /**
     * Add a TileListener to the tile.
     * @param l The MouseListener to attach as a TileListener
     */
    public void addTileListener(MouseListener l) 
    { 
        tileListener = l;
        addMouseListener(tileListener);
    }
    
    /**
     * Remove the tile's TileListener.
     */
    public void removeTileListener() { removeMouseListener(tileListener); }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.drawImage(image, 
                0, 
                0, 
                PREFERRED_WIDTH, 
                PREFERRED_HEIGHT, 
                null);
    }
}






class LetterRack extends JPanel
{
    /**
     * The number of columns present in the letter tile rack.
     */
    private final int RACK_COLS;
    
    /**
     * The number of columns present in the letter tile rack.
     */
    private final int RACK_ROWS;
    
    /**
     * The layout of the letter rack.
     */
    private final GridLayout LETTER_RACK_LAYOUT;
    
    /**
     * The capacity of the letter rack.
     */
    private final int CAPACITY;
    
    /**
     * The directory containing the letter images.
     */
    private final String IMAGE_DIRECTORY;
    
    /**
     * The type of image (.jpg, .png, etc. to include the period).
     */
    private final String IMAGE_TYPE;
    
    /**
     * The password chosen to be guessed.
     */
    private final String password;
    
    /**
     * An array of letters to be displayed on the GameBoard.
     */
    private final ArrayList<LetterTile> rack;
    
    /**
     * The default constructor.
     */
    public LetterRack()
    {
        this("password", "images/", ".png");
    }
    





    /**
     * Creates a new LetterRack given the password to be guessed, letter image
     * directory, and letter image type
     * @param inPassword The password to be guessed.
     * @param imageDirectory The directory of the letter images.
     * @param imageType The type of the letter images.
     */
    public LetterRack(String inPassword, String imageDirectory, 
            String imageType)
    {
        RACK_COLS = 8;
        RACK_ROWS = 2;
        LETTER_RACK_LAYOUT = new GridLayout(RACK_ROWS, RACK_COLS);
        LETTER_RACK_LAYOUT.setVgap(10);
        CAPACITY = RACK_ROWS * RACK_COLS;
        
        IMAGE_DIRECTORY = imageDirectory;
        IMAGE_TYPE = imageType;
        
        rack = new ArrayList<>();
        password = inPassword;
        
        // add a little padding to make sure the letter rack is centered
        setBorder(BorderFactory.createEmptyBorder(10, 17, 10, 10));
        setLayout(LETTER_RACK_LAYOUT);
        loadRack();
    }
    
    /**
     * Builds and loads the letter rack with letter tiles.
     */
    private void loadRack()
    {
        buildRack();
        for (LetterTile tile : rack)
            add(tile);
    }
    





    /**
     * Builds a letter rack from a blend of the password and random letters.
     */
    private void buildRack()
    {
        StringBuilder passwordBuilder = 
                new StringBuilder(password.toLowerCase());
        ArrayList<Character> tiles = new ArrayList<>(); // cannot use char
        Random rand = new Random();
        int i = 0, j = 0;
        
        // add the password letters to the rack
        while (passwordBuilder.length() > 0)
        {
            // want to make sure that no letters are repeated in tile rack
            if (!tiles.contains(passwordBuilder.charAt(0)))
            {
                tiles.add(passwordBuilder.charAt(0));
                i++;
            }
            passwordBuilder.deleteCharAt(0);
        }
        
        // add random values to fill the remainder of the rack
        for (; i < CAPACITY; i++)
        {
            Character c = 'a'; // 'a' is just a default value
            do
            {
                c = (char) (rand.nextInt(26) + 'a');
            } while (tiles.contains(c));
            tiles.add(c);
        }
        
        // grab random tiles from the ArrayList to display randomly on the
        //    GameBoard
        for (i = 0; i < CAPACITY; i++)
        {
            j = rand.nextInt(tiles.size());
            rack.add(new LetterTile(tiles.get(j), 
                    IMAGE_DIRECTORY, 
                    IMAGE_TYPE));
            tiles.remove(j);
        }
    }
    
    /**
     * Add a TileListener to each LetterTile in the LetterRack
     * @param l The TileListener to be added.
     */
    public void attachListeners(MouseListener l)
    {
        for (LetterTile tile : rack)
            tile.addTileListener(l);
    }
    
    /**
     * Remove all TileListeners from all LetterTiles.
     */
    public void removeListeners()
    {
        for (LetterTile tile : rack)
            tile.removeTileListener();
    }
}

class Hangman extends JLabel
{
    /**
     * The preferred width of the images.
     */
    private final int PREFERRED_WIDTH;
    
    /**
     * The preferred height of the images.
     */
    private final int PREFERRED_HEIGHT;
    
    /**
     * The base (common) name of the series of images to use (e.g. "hangman" in
     * "hangman_0.png, hangman_1.png, ..."
     */
    private final String IMAGE_BASE_NAME;
    
    /**
     * The directory containing the hangman images.
     */
    private final String IMAGE_DIRECTORY;
    
    /**
     * The type of image (.jpg, .png, etc. to include the period).
     */
    private final String IMAGE_TYPE;
    
    /**
     * The current path of the current image.
     */
    private String path;
    
    /**
     * The current image being displayed.
     */
    private BufferedImage image;
    
    /**
     * The default constructor.
     */
    public Hangman()
    {
        this("hangman", "images/", ".png");
    }
    
    /**
     * Creates a new Hangman given the image series' base name, the directory
     * of the hangman images, and the type of image.
     * @param imageBaseName The base name of the image series to be used.
     * @param imageDirectory The directory containing the hangman images.
     * @param imageType The type of the hangman images.
     */
    public Hangman(String imageBaseName, String imageDirectory, 
            String imageType)
    {
        PREFERRED_WIDTH = 440;
        PREFERRED_HEIGHT = 255;
        
        IMAGE_BASE_NAME = imageBaseName;
        IMAGE_DIRECTORY = imageDirectory;
        IMAGE_TYPE = imageType;
        
        // you must suffix all images with _(image number) for this to work
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        path = IMAGE_DIRECTORY + IMAGE_BASE_NAME + "_0" + IMAGE_TYPE;
        image = loadImage(path);
    }
    
    /**
     * Loads an image from a file.
     * @param imagePath The path to load an image from.
     * @return A BufferedImage object on success, exits on failure.
     */
    private BufferedImage loadImage(String imagePath)
    {
        BufferedImage img = null;

        try 
        {
            img = ImageIO.read(new File(imagePath));
        } 

        catch (IOException ex) 
        {
            System.err.println("loadImage(): Error: Image at "
                    + imagePath + " could not be found");
            System.exit(1);
        }
        
        return img;
    }
    
    /**
     * Load the next hangman image in the series.
     * @param imageNumber The number of the image to load.
     */
    public void nextImage(int imageNumber) 
    { 
        loadNewImage(String.valueOf(imageNumber));
    }
    
    /**
     * Display the losing image.
     */
    public void loseImage() { loadNewImage("lose"); }
    
    /**
     * Display the winning image.
     */
    public void winImage() { loadNewImage("win"); }
    
    /**
     * Loads a new image in the hangman image series.
     * @param suffix The suffix of the image name.
     */
    private void loadNewImage(String suffix)
    {
        path = IMAGE_DIRECTORY + IMAGE_BASE_NAME + "_" + suffix + IMAGE_TYPE;
        image = loadImage(path);
        repaint();  
    }
    
    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.drawImage(image, 
                0, 
                0, 
                PREFERRED_WIDTH, 
                PREFERRED_HEIGHT, 
                null);
    }
}
