package sokoban;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.io.*;
import javax.sound.sampled.*;

public class GameMain extends JPanel {	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
// main class for the game   
	
	   // Define constants for the game
	   static final String TITLE = "Sokoban";        // title of the game
	   static final int UPDATES_PER_SEC = 4;    // number of game update per second
	   static final long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;  // nanoseconds
	   static final int OFFSET = 0;
	   static final int SPACE = 32;
	
	   // Define basic game variables
	   private int w = 0;
	   private int h = 0;

	   private boolean legalPause = false;
	   private boolean defaultSound = true;
	   
	// Enumeration for the states of the game.
	   static enum GameState {
	      INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED
	   }
	// Enumeration for directions in a 2D environment
	   static enum CollisionCheck	{
		   UP, DOWN, LEFT, RIGHT
	   }
	   static CollisionCheck direction;
	
	// Enumeration for sound effects
	   static enum SoundEffect {
		   // customizable list of sounds for each game
		   PUSH("d:/workspace/GameProject/freeze.wav"),
		   STEP("d:/workspace/GameProject/koopa-stomp.wav"),
		   SUCCESS("d:/workspace/GameProject/itemget.wav"),
		   FINISH("d:/workspace/GameProject/itemreel.wav"),
		   WALL("d:/workspace/GameProject/hit.wav");
		   		   
		// Nested class for specifying volume
		   public static enum Volume {
		      MUTE, LOW, MEDIUM, HIGH
		   }
		   
		   public static Volume volume = Volume.LOW;
		   
		   // Each sound effect has its own clip, loaded with its own sound file.
		   private Clip clip;
		   
		   // Constructor to construct each element of the enum with its own sound file.
		   SoundEffect(String soundFileName) {
		      try {
		         // Use URL (instead of File) to read from disk and JAR.
		         //URL url = this.getClass().getClassLoader().getResource(soundFileName);
		    	  File f = new File(soundFileName);
		         // Set up an audio input stream piped from the sound file.
		         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f.toURI().toURL());
		         // Get a clip resource.
		         clip = AudioSystem.getClip();
		         // Open audio clip and load samples from the audio input stream.
		         clip.open(audioInputStream);
		      } catch (UnsupportedAudioFileException e) {
		         e.printStackTrace();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (LineUnavailableException e) {
		         e.printStackTrace();
		      }
		   }
		   
		   // Play or Re-play the sound effect from the beginning, by rewinding.
		   public void play() {
		      if (volume != Volume.MUTE) {
		         if (clip.isRunning())
		            clip.stop();   // Stop the player if it is still running
		         clip.setFramePosition(0); // rewind to the beginning
		         clip.start();     // Start playing
		      }
		   }
		   
		   // Optional static method to pre-load all the sound files.
		   static void init() {
		      values(); // calls the constructor for all the elements
		   }
	   }
	   
	   static GameState state;   // current state of the game
	   
	   private GameCanvas canvas;
	
	   // Declare menubar
	   static JMenuBar menuBar;
	
	   // ------ All the game related variables here ------
	
	   // Game elements
	   private Worker worker;
	   private ArrayList<Wall> walls;
	   private ArrayList<Crate> crates;
	   private ArrayList<Target> targets;
	   private ArrayList<Floor> spaces;
	
	   // Game Levels
	   private String level1
	   //* Level 1
	   = "                    \n"
	   + "                ### \n"
	   + "                  # \n"
	   + "     #######      # \n"
	   + "     #     #      # \n"
	   + "     #* $ @#      # \n"
	   + "     #     #        \n"
	   + "     #######        \n"
	   + "                    \n"
	   + "                    \n"
	   + "                    \n";
	   private String level2 
	   = "     ########       \n"
	   + "    ##@  #  ##  ####\n"
	   + "    #    $   #     #\n"
	   + "    ##  ###$ #    # \n"
	   + "    #    #   #  ##  \n"
	   + "    #    #   #  ####\n"
	   + "    ###$   ###      \n"
	   + "    ##     **#      \n"
	   + "    #      *##      \n"
	   + "    #########       \n"
	   + "                    \n";
	   private String level3
	   //*	Level 2
           = "    ######      ####\n"
           + "    ##   #         #\n"
           + "    ##$  #        ##\n"
           + "  ####  $##        #\n"
           + "  ##  $ $ #     ####\n"
	   + "#### # ## #   ######\n"
	   + "##   # ## #####  **#\n"
	   + "## $  $          **#\n"
	   + "###### ### #@##  **#\n"
	   + "    ##     #########\n"
	   + "    ########        \n";
	
	   private String level = level1;	// default level
	   
	   public GameMain()	{
		   // Initialize the game objects
	      gameInit();
	   
	      // UI components
	      canvas = new GameCanvas();
	      canvas.setPreferredSize(new Dimension(w, h));
	      add(canvas, BorderLayout.CENTER);   // center of default BorderLayout
		      
	   // Start the game.
	      gameStart();  
	   }
	   
	// ------ All the game related codes here ------
	   
	   // Initialize all the game objects, run only once.
	   public void gameInit() { 
		   state = GameState.INITIALIZED;
	   }
	   
	// Update the state and position of all the game objects,
	   // detect collisions and provide responses.
	   public void gameUpdate() { 
	   
	   }
	   
	// Run the game loop here.
	   private void gameLoop() {
	      // Regenerate the game objects for a new game
	      // ......
	      state = GameState.PLAYING;
	   
	      // Game loop
	      long beginTime, timeTaken, timeLeft;  // in msec
	      while (state != GameState.GAMEOVER) {
	         beginTime = System.nanoTime();
	         if (state == GameState.PLAYING) {   // not paused
	            // Update the state and position of all the game objects,
	            // detect collisions and provide responses.
	            gameUpdate();
	         }
	         // Refresh the display
	         repaint();
	         // Delay timer to provide the necessary delay to meet the target rate
	         timeTaken = System.nanoTime() - beginTime;
	         timeLeft = (UPDATE_PERIOD_NSEC - timeTaken) / 1000000L;  // in milliseconds
	         if (timeLeft < 10) timeLeft = 10;   // set a minimum
	         try {
	            // Provides the necessary delay and also yields control so that other thread can do work.
	            Thread.sleep(timeLeft);
	         } catch (InterruptedException ex) { }
	      }
	   }
	   
	// To start and re-start the game.
	   public void gameStart() { 
	      // Create a new thread
	      Thread gameThread =  new Thread() {
	         // Override run() to provide the running behavior of this thread.
	         @Override
	         public void run() {
	            gameLoop();
	         }
	      };
	      // Start the thread. start() calls run(), which in turn calls gameLoop().
	      gameThread.start();
	   }
	   
	// Refresh the display. Called back via repaint(), which invoke the paintComponent().
	   private void gameDraw(Graphics2D g2d) {
	      switch (state) {
	         case INITIALIZED:
	            // ......
	            break;
	         case PLAYING:
	            // ......
	            break;
	         case PAUSED:
	            // ......
	            break;
	         case GAMEOVER:
	            // ......
	            break;
		default:
			break;
	      }
	      // ...... 
	   }
	   
	   // Process a key-pressed event. Update the current state.
	   public void gameKeyPressed(int keyCode) {
	      switch (keyCode) {
	         case KeyEvent.VK_UP:
	            // ......
	            break;
	         case KeyEvent.VK_DOWN:
	            // ......
	            break;
	         case KeyEvent.VK_LEFT:
	            // ......
	            break;
	         case KeyEvent.VK_RIGHT:
	            // ......
	            break;
	      }
	   }
	   
	// Custom drawing panel, written as an inner class.
	   class GameCanvas extends JPanel implements KeyListener {
	      /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// Constructor
	      public GameCanvas() {
	         setFocusable(true);  // so that can receive key-events
	         requestFocus();
	         addKeyListener(this);
	      }
	   
	      // Override paintComponent to do custom drawing.
	      // Called back by repaint().
	      @Override
	      public void paintComponent(Graphics g) {
	         Graphics2D g2d = (Graphics2D)g;
	         super.paintComponent(g2d);   // paint background
	         setBackground(Color.BLACK);  // may use an image for background
	   
	         // Draw the game objects
	         gameDraw(g2d);
	      }
	      
	      // KeyEvent handlers
	      @Override
	      public void keyPressed(KeyEvent e) {
	         gameKeyPressed(e.getKeyCode());
	      }
	      
	      @Override
	      public void keyReleased(KeyEvent e) { }
	   
	      @Override
	      public void keyTyped(KeyEvent e) { }
	   }
	   
	   public static void main(String[] args) {
		      // Use the event dispatch thread to build the UI for thread-safety.
		      SwingUtilities.invokeLater(new Runnable() {
		         @Override
		         public void run() {
		            JFrame frame = new JFrame(TITLE);
		            // Set the content-pane of the JFrame to an instance of main JPanel
		            frame.setContentPane(new GameMain());  // main JPanel as content pane
		            //frame.setJMenuBar(menuBar);          // menu-bar (if defined)
		            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		            frame.pack();
		            frame.setLocationRelativeTo(null); // center the application window
		            frame.setVisible(true);            // show it
		         }
		      });
		   }
}
