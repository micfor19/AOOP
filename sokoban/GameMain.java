package sokoban;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
// main class for the game   
	
	   // Define constants for the game
	   static final String TITLE = "Sokoban";        // title of the game
	   static final int CANVAS_WIDTH = 800;    // width and height of the drawing canvas
	   static final int CANVAS_HEIGHT = 600;
	   static final int UPDATES_PER_SEC = 4;    // number of game update per second
	   static final long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;  // nanoseconds
	   
	// Enumeration for the states of the game.
	   static enum GameState {
	      INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED
	   }
	   
	   static GameState state;   // current state of the game
	   
	   private GameCanvas canvas;
	   
	   public GameMain()	{
		   // Initialize the game objects
	      gameInit();
	   
	      // UI components
	      canvas = new GameCanvas();
	      canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
	      add(canvas);   // center of default BorderLayout
		      
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
