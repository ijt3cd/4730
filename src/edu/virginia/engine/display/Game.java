package edu.virginia.engine.display;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.virginia.engine.controller.GamePad;
import net.java.games.input.*;

/**
 * Highest level class for creating a game in Java.
 * 
 */
public class Game extends DisplayObject implements ActionListener, KeyListener {

	/* Frames per second this game runs at */
	private int FRAMES_PER_SEC = 60;

	/* The main JFrame that holds this game */
	private JFrame mainFrame;

	/* Timer that this game runs on */
	private Timer gameTimer;

	/* The JPanel for this game */
	private GameScenePanel scenePanel;

	/* Connected Game Controllers */
	private ArrayList<GamePad> controllers;
	private Font displayFont;

	protected static final int HORIZONTAL_MOVEMENT_DELTA = 6;
	protected static final double JUMP_UP_DELTA = 7.75;
	protected static final double HORIZONTAL_MOVEMENT_DECAY = 0.6;
	protected static final double GHOST_EXTENSION = 50;

	public Game(String gameId, int width, int height) {
		super(gameId);

		setUpMainFrame(gameId, width, height);

		setScenePanel(new GameScenePanel(this));

		/* Use an absolute layout */
		scenePanel.setLayout(null);

		/* Search for and add any controllers that are connected */
		controllers = new ArrayList<GamePad>();
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();
		for (int i = 0; i < cs.length; i++) {
			Controller controller = cs[i];
			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD
					|| controller.getType() == Controller.Type.WHEEL
					|| controller.getType() == Controller.Type.FINGERSTICK) {
				System.out.println("Found Controller: " + controller.getName() + ", " + controller.getType());
				controllers.add(new GamePad(controller));
			}
		}
		try {
			displayFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/ARCADECLASSIC.TTF"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(displayFont);
		} catch (Exception e) {
			// Handle exception
		}
	}

	public void setFramesPerSecond(int fps) {
		if (fps > 0)
			this.FRAMES_PER_SEC = fps;
	}

	public void setUpMainFrame(String gameId, int width, int height) {
		this.mainFrame = new JFrame();
		getMainFrame().setTitle(gameId);
		getMainFrame().setResizable(true);
		getMainFrame().setVisible(true);
		getMainFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getMainFrame().setBounds(0, 0, width, height);
		getMainFrame().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		getMainFrame().addKeyListener(this);
	}

	/**
	 * Starts the game
	 */
	public void start() {
		if (gameTimer == null) {
			gameTimer = new Timer(1000 / FRAMES_PER_SEC, this);
			gameTimer.start();
		} else {
			gameTimer.start();
		}
	}

	/**
	 * Stops the animation.
	 */
	public void stop() {
		pause();
		gameTimer = null;
	}

	public void pause() {
		if (gameTimer != null) {
			gameTimer.stop();
		}
	}

	public void exitGame() {
		stop();
		this.mainFrame.setVisible(false);
		this.mainFrame.dispose();
	}

	/**
	 * Close the window
	 */
	public void closeGame() {
		this.stop();
		if (this.getMainFrame() != null) {
			this.getMainFrame().setVisible(false);
			this.getMainFrame().dispose();
		}
	}

	/**
	 * Called once per frame. updates the game, redraws the screen, etc. May
	 * need to optimize this if games get too slow.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		repaintGame();
	}

	/**
	 * Forces a repaint
	 */
	public void repaint() {
		repaintGame();
	}

	public void repaintGame() {
		if (getScenePanel() != null) {
			getScenePanel().validate();
			getScenePanel().repaint();
		}
	}

	protected void nextFrame(Graphics g) {

		try {
			/* Poll for any wireless controller buttons that are pressed down */
			pollControllers();

			/* Update all objects on the stage */
			this.update(pressedKeys, controllers);

			/* Draw everything on the screen */
			this.draw(g);
		} catch (Exception e) {
			System.out.println("Exception in nextFrame of game. Stopping game (no frames will be drawn anymore");
			stop();
			e.printStackTrace();
		}
	}

	/**
	 * Searches all known controllers (ps3, etc.) and adds any pressed buttons
	 * to pressed keys
	 */
	private void pollControllers() {
		for (GamePad controller : controllers) {
			controller.update();
		}
	}

	@Override
	public void draw(Graphics g) {
		/* Start with no transparency */
		((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		try {
			g.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/ARCADECLASSIC.TTF")).deriveFont(30f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.draw(g);
	}

	public JFrame getMainFrame() {
		return this.mainFrame;
	}

	public void setScenePanel(GameScenePanel scenePanel) {
		this.scenePanel = scenePanel;
		this.getMainFrame().add(this.scenePanel);
		getMainFrame().setFocusable(true);
		getMainFrame().requestFocusInWindow();
	}

	public GameScenePanel getScenePanel() {
		return scenePanel;
	}

	ArrayList<String> pressedKeys = new ArrayList<String>();

	@Override
	public void keyPressed(KeyEvent e) {
		if (!pressedKeys.contains(KeyEvent.getKeyText(e.getKeyCode())))
			pressedKeys.add(KeyEvent.getKeyText(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (pressedKeys.contains(KeyEvent.getKeyText(e.getKeyCode())))
			pressedKeys.remove(KeyEvent.getKeyText(e.getKeyCode()));

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}