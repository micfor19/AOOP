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
	private int width = 0;
	private int height = 0;

	private boolean legalPause = false;
	private boolean defaultSound = true;
	   
	// Enumeration for the states of the game.
	static enum GameState {
	   INITIALIZED, PLAYING, PAUSED, GAMEOVER, DESTROYED
	}
	static GameState state;   // current state of the game
	
	// Enumeration for directions in a 2D environment
	static enum Direction	{
	   UP, DOWN, LEFT, RIGHT
	}
	static Direction direction;
	
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
	         // Use File to read from disk
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
	   	   
	private GameCanvas canvas;
	
	// Declare menubar
	static JMenuBar GameMenu;
	
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
	   canvas.setPreferredSize(new Dimension(width, height));
	   add(canvas, BorderLayout.CENTER);   // center of default BorderLayout
	      
	   // Start the game.
	   gameStart();  
	}
	   
	// ------ All the game related codes here ------
	   
	// Initialize all the game objects, run only once.
	public void gameInit() { 
	   legalPause = false;
	   setupMenuBar();
	   SoundEffect.init();
		    
	   walls = new ArrayList<>();
	   crates = new ArrayList<>();
	   targets = new ArrayList<>();
	   spaces = new ArrayList<>();

	   int x = OFFSET;
	   int y = OFFSET;

	   Wall wall;
	   Crate c;
	   Target t;
	   Floor f;
		   
	   // Initialize world
	   for (int i = 0; i < level.length(); i++) {

	      char item = level.charAt(i);

	      switch (item) {

	             case '\n':
	                 y += SPACE;

	                 if (this.width < x) {
	                     this.width = x;
	                 }

	                 x = OFFSET;
	                 break;

	             case '#':
	                 wall = new Wall(x, y);
	                 walls.add(wall);
	                 x += SPACE;
	                 break;

	             case '$':
	                 c = new Crate(x, y);
	                 f = new Floor(x, y);
	                 crates.add(c);
	                 spaces.add(f);
	                 x += SPACE;
	                 break;

	             case '*':
	                 t = new Target(x, y);
	                 targets.add(t);
	                 x += SPACE;
	                 break;

	             case '@':
	                 worker = new Worker(x, y);
	                 f = new Floor(x, y);
	                 spaces.add(f);
	                 x += SPACE;
	                 break;

	             case ' ':
	              	f = new Floor(x, y);
	               	spaces.add(f);
	                x += SPACE;
	                break;

	             default:
	                break;
	      }
	      height = y;
	   }
	   state = GameState.INITIALIZED;
	}
	   
	// check status of the game
	public void gameUpdate() { 
	   isCompleted();
	}
	   
	// Run the game loop here.
	private void gameLoop() {

	   if (state == GameState.INITIALIZED)
	      state = GameState.PLAYING;
	   
	   // Game loop
	   long beginTime, timeTaken, timeLeft;  // in msec
	   while (state != GameState.GAMEOVER) {
	      beginTime = System.nanoTime();
	      if (state == GameState.PLAYING) {   // not paused
	         // check status of the game
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
	private void gameDraw(Graphics g) {
	   ArrayList<GameObject> world = new ArrayList<>();
	   switch (state) {
	      case INITIALIZED:
	         // start drawing in 'PLAYING' state
	         break;
	      case PLAYING:
	         g.setColor(new Color(250, 240, 170));
	         g.fillRect(0, 0, this.getWidth(), this.getHeight());

	         // Order of drawing is critical for correct visualization
	         world.addAll(walls);
	         world.addAll(targets);
	         world.addAll(spaces);
	         world.addAll(crates);
	         world.add(worker);
	             
	         for (int i = 0; i < world.size(); i++) {

	            GameObject item = world.get(i);

	            if (item instanceof Worker || item instanceof Crate) {
	                     
	               g.drawImage(item.getImage(), item.getX() + 2, item.getY() + 2, this);
	            } else {
	                     
	               g.drawImage(item.getImage(), item.getX(), item.getY(), this);
	            }
		 }
	         break;
	      case PAUSED:
	         // Just an info text
	         g.setColor(new Color(0, 0, 0));
		 g.drawString("Paused, resume with (P)", 25, 20);
	         break;
	      case GAMEOVER:
	         g.setColor(new Color(0, 0, 0));
	         if (level != level3) {	
		    g.drawString("Completed - next Level (Y)?", 25, 20);
		 } else { 
		    g.drawString("All levels passed, restart (R)?", 25, 20);
		 }
	         break;
	      default:
	         break;
	   }
	      
	}
	   
	// Process a key-pressed event. Update the current state.
	public void gameKeyPressed(int keyCode) {
	   if (state == GameState.GAMEOVER) {
	      // different key handling outside of the game
	      if (keyCode == KeyEvent.VK_Y)	{
	         if (level == level1) {	
		    level = level2;
		 } else if (level == level2) {
		    level = level3;
		 }	 
		 gameInit();
		 gameStart();					
	      }
	      if (keyCode == KeyEvent.VK_R)	{
		 level = level1;
		 gameInit();
		 gameStart();
	      }
              return;
           }
	   switch (keyCode) {
	   // WASD keyboard layout
	      case KeyEvent.VK_W:
	         direction = Direction.UP;
	         if (checkWallCollision(worker)) {
               	    return;
                 }
	         if (checkBoxCollision()) {
                    return;
                 }
	         worker.move(0, -SPACE);
	         if (defaultSound) SoundEffect.STEP.play();
	         else defaultSound = true;
	         break;
	      case KeyEvent.VK_S:
	         direction = Direction.DOWN;
	         if (checkWallCollision(worker)) {
                    return;
                 }
	         if (checkBoxCollision()) {
                    return;
                 }
	         worker.move(0, SPACE);
	         if (defaultSound) SoundEffect.STEP.play();
	         else defaultSound = true;
	         break;
	      case KeyEvent.VK_A:
	         direction = Direction.LEFT;
	         if (checkWallCollision(worker)) {
                    return;
                 }
	         if (checkBoxCollision()) {
                    return;
                 }
	         worker.move(-SPACE, 0);
	         if (defaultSound) SoundEffect.STEP.play();
	         else defaultSound = true;
	         break;
	      case KeyEvent.VK_D:
	         direction = Direction.RIGHT;
	         if (checkWallCollision(worker)) {
                    return;
                 }
	         if (checkBoxCollision()) {
                    return;
                 }
	         worker.move(SPACE, 0);
	         if (defaultSound) SoundEffect.STEP.play();
	         else defaultSound = true;
	         break;
	      // special functionalities
	      case KeyEvent.VK_P:
	         if (state == GameState.PLAYING)	{
	            state = GameState.PAUSED;
	            legalPause = true;
	         } else if (legalPause)	{
	            state = GameState.PLAYING;
	            legalPause = false;
	         }
	      case KeyEvent.VK_M:
	       	 if (SoundEffect.volume == SoundEffect.Volume.MUTE) {
	            SoundEffect.volume = SoundEffect.Volume.LOW;
		 } else	{	
		    SoundEffect.volume = SoundEffect.Volume.MUTE;
		 }
	      default:
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

	      super.paintComponent(g);   // paint background
	      setBackground(Color.LIGHT_GRAY);  // may use an image for background
	   
	      // Draw the game objects
	      gameDraw(g);
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
	
	// Helper function to setup the menubar
	private void setupMenuBar() {
	   JMenu menu;         // a menu in the menu-bar
	   JMenuItem menuItem; // a regular menu-item in a menu
	      
	   GameMenu = new JMenuBar();
	      
	   // First Menu - "Game"
	   menu = new JMenu("Game");
	   menu.setMnemonic(KeyEvent.VK_G);
	   GameMenu.add(menu);
	 
	   menuItem = new JMenuItem("New", KeyEvent.VK_N);
	   menu.add(menuItem);
	   menuItem.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	         // Stop the current game if needed
	         if (state == GameState.PLAYING || state == GameState.PAUSED) {
	            state = GameState.GAMEOVER;
	         }
	         gameInit();
	         gameStart();
	      }
	   });
	      
	   menuItem = new JMenuItem("Pause", KeyEvent.VK_P);
	   menu.add(menuItem);
	   menuItem.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	       	 if (state == GameState.PLAYING)	{
		    state = GameState.PAUSED;
		    legalPause = true;
		 } else if (legalPause)	{
		    state = GameState.PLAYING;
		    legalPause = false;
		 }
	      }
	   });
	      
	 
	   // Help Menu
	   menu = new JMenu("Help");
	   menu.setMnemonic(KeyEvent.VK_H);
	   GameMenu.add(menu);

	   menuItem = new JMenuItem("Help Contents", KeyEvent.VK_H);
	   menu.add(menuItem);
	   menuItem.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	         String msg = "Push the boxes on the red circles!\n"
	           	  + "W/A/S/D to change direction\n"
	                  + "P to pause/resume \n"
	                  + "M to mute/unmute sound \n";
	         JOptionPane.showMessageDialog(GameMain.this, 
	                msg, "Instructions", JOptionPane.PLAIN_MESSAGE);
	      }
	   });

	   menuItem = new JMenuItem("About");
	   menu.add(menuItem);
	   menuItem.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	         JOptionPane.showMessageDialog(GameMain.this, 
	            "The super-fun SOKOBAN game for learning to program a game framework",
	            "About", JOptionPane.PLAIN_MESSAGE);
	      }
	   });
	}
	
	// ------ Additional game related functions here ------	
	private boolean checkWallCollision(GameObject item)	{
	   switch (direction) {
	        
	      case UP:	                
	         for (int i = 0; i < walls.size(); i++) {	                    
	            Wall wall = walls.get(i);	                    
	            if (item.isTopCollision(wall)) {
	               SoundEffect.WALL.play();
	               return true;
	            }
	         }	                
	         return false;
	                
	      case DOWN:	                
	         for (int i = 0; i < walls.size(); i++) {	                    
	            Wall wall = walls.get(i);	                    
	            if (item.isBottomCollision(wall)) {
	               SoundEffect.WALL.play();
	               return true;
	            }
	         }	                
	         return false;
	                
	      case LEFT:
		 for (int i = 0; i < walls.size(); i++) {
		    Wall wall = walls.get(i);
		    if (item.isLeftCollision(wall)) {
		       SoundEffect.WALL.play();
		       return true;
		    }
	 	 } 
		 return false;

	      case RIGHT:
		 for (int i = 0; i < walls.size(); i++) {
		    Wall wall = walls.get(i);
		    if (item.isRightCollision(wall)) {
		       SoundEffect.WALL.play();
		       return true;
		    }
		 }		                
		 return false;

	      default:
		 break;
	   }	        
	   return false;
	}
	
	private boolean checkBoxCollision() {
	   switch (direction) {
	            
	      case LEFT:	                
	         for (int i = 0; i < crates.size(); i++) {
	            Crate crate = crates.get(i);
				
	            if (worker.isLeftCollision(crate)) {
	               for (int j = 0; j < crates.size(); j++) {	                            
	                  Crate item = crates.get(j);	 
					
	                  if (!crate.equals(item)) {	                                
	                     if (crate.isLeftCollision(item)) {
	                        return true;
	                     }
	                  }	                            
	                  if (checkWallCollision(crate)) {
	                     return true;
	                  }
	               }
	               defaultSound = false;
	               crate.move(-SPACE, 0);
	               SoundEffect.PUSH.play();
	            }
	         }	                
	         return false;
	                
	      case RIGHT:	                
	         for (int i = 0; i < crates.size(); i++) {
	            Crate crate = crates.get(i);
	                    
	            if (worker.isRightCollision(crate)) {
	               for (int j = 0; j < crates.size(); j++) {	                            
	                  Crate item = crates.get(j);
	                            
	                  if (!crate.equals(item)) {	                                
	                     if (crate.isRightCollision(item)) {
	                        return true;
	                     }
	                  }	                            
	                  if (checkWallCollision(crate)) {
	                     return true;
	                  }
	               }
	               defaultSound = false;
	               crate.move(SPACE, 0);
	               SoundEffect.PUSH.play();
	            }
	         }
	         return false;
	               
	      case UP:	                
	         for (int i = 0; i < crates.size(); i++) {
	            Crate crate = crates.get(i);
	                    
	            if (worker.isTopCollision(crate)) {
	               for (int j = 0; j < crates.size(); j++) {	                            
	                  Crate item = crates.get(j);
	                            
	                  if (!crate.equals(item)) {	                                
	                     if (crate.isTopCollision(item)) {
	                        return true;
	                     }
	                  }	                            
	                  if (checkWallCollision(crate)) {
	                     return true;
	                  }
	               }
	               defaultSound = false;
	               crate.move(0, -SPACE);
	               SoundEffect.PUSH.play();
	            }
	         }
	         return false;
	                
	      case DOWN:	                
	         for (int i = 0; i < crates.size(); i++) {
	            Crate crate = crates.get(i);
	                    
	            if (worker.isBottomCollision(crate)) {
	               for (int j = 0; j < crates.size(); j++) {	                            
	                  Crate item = crates.get(j);
	                            
	                  if (!crate.equals(item)) {	                                
	                     if (crate.isBottomCollision(item)) {
	                        return true;
	                     }
	                  }	                            
	                  if (checkWallCollision(crate)) {
	                     return true;
	                  }
	               }
	               defaultSound = false;
	               crate.move(0, SPACE);
	               SoundEffect.PUSH.play();
	            }
	         }	                
	         break;
	                
	      default:
	         break;
	   }
	   return false;
	}
	
	public void isCompleted() {

	   int nOfBoxes = crates.size();
	   int finishedBoxes = 0;

	   for (int i = 0; i < nOfBoxes; i++) {	            
	      Crate crate = crates.get(i);			
	      for (int j = 0; j < nOfBoxes; j++) {	                
	         Target target =  targets.get(j);	                
	         if (crate.getX() == target.getX() && crate.getY() == target.getY()) {	                    
	            finishedBoxes += 1;
	         }
	      }
	   }

	   if (finishedBoxes == nOfBoxes) {
	      if (level != level3) {
	         SoundEffect.SUCCESS.play();
	      } else {
	         SoundEffect.FINISH.play();	         	
	      }
	      state = GameState.GAMEOVER;
	      repaint();
	   }
	}
	   
	public static void main(String[] args) {
	   // Use the event dispatch thread to build the UI for thread-safety.
	   SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	         JFrame frame = new JFrame(TITLE);
	         // Set the content-pane of the JFrame to an instance of main JPanel
	         frame.setContentPane(new GameMain());  // main JPanel as content pane
	         frame.setJMenuBar(GameMenu);          // menu-bar 
	         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	         frame.pack();
	         frame.setLocationRelativeTo(null); // center the application window
	         frame.setVisible(true);            // show it
	         }
	   });
	}
}
