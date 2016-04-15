package edu.virginia.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

/**
 * Example game that utilizes our engine. We can create a simple prototype game
 * with just a couple lines of code although, for now, it won't be a very fun
 * game :)
 */
public class GhostGame extends Game {

	private static final int HORIZONTAL_MOVEMENT_DELTA = 3;
	private static final double JUMP_UP_DELTA = 7.75;
	private static final double HORIZONTAL_MOVEMENT_DECAY = 0.8;
	public static int width = 1050;
	public static int height = 1050;

	SoundManager sm = new SoundManager();
	File bgm = new File("resources/brm.wav");

	/* Create a sprite object for our game. We'll use mario */
	AnimatedSprite link = new AnimatedSprite("Link", "LinkSprites.png", 120, 130);
	AnimatedSprite ghost = new AnimatedSprite("ghost", "GhostSprites.png", 32, 48);
	Sprite ring = new Sprite("Ring", "Ring.png");
	QuestManager questManager = new QuestManager();
	ArrayList<Sprite> sprites;
	ArrayList<Rectangle> platformHitboxes;
	ArrayList<Sprite> spikes;
	ArrayList<double[]> locationTracker;
	ArrayList<double[]> nextGhost;
	int currIndex = 0;
	boolean record;
	boolean pickedUp;
	float startingX;
	float startingY;
	Tween ringGrabbed;
	Tween ringFade;
	int deathCount = 0;
	boolean draw;
	boolean onGhost;
	Map map;

	/**
	 * Constructor. See constructor in Game.java for details on the parameters
	 * given
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public GhostGame() {
		super("Ghost Game", width, height);
		getMainFrame().setBounds(0, 0, width, height); // Fixing weird size bug.
		sprites = new ArrayList<Sprite>();
		TMXMapReader mapReader = new TMXMapReader();
		try {
			map = mapReader.readMap("resources/level3.tmx");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TweenJuggler.getInstance();
		SoundManager.playMusic(bgm);
		spikes = new ArrayList<Sprite>();
		platformHitboxes = new ArrayList<Rectangle>();
		boolean[][] platformIndicators = new boolean[map.getHeight()][map.getWidth()];
		for (MapLayer m : map.getLayers()) {
			if (m instanceof TileLayer) {
				TileLayer l = (TileLayer) m;
				for (int i = 0; i < map.getHeight(); i++) {
					for (int j = 0; j < map.getWidth(); j++) {
						Tile t = l.getTileAt(j, i);
						if (t != null) {
							Sprite s = new Sprite("" + t.getId());
							s.setImage((BufferedImage) t.getImage());
							s.setPositionX(j * t.getWidth());
							s.setPositionY(i * t.getHeight());
							sprites.add(s);
							if (l.getName().equals("Platforms")) {
								platformIndicators[i][j] = true;
							} else if (l.getName().equals("Spikes")) {
								spikes.add(s);
							} else if (l.getName().equals("Spawn")) {
								startingX = j * t.getWidth();
								startingY = i * t.getHeight();
							}
						}
					}
				}
			}
		}
		// Two passes, grabbing horizontal tile groups then vertical tile groups
		for (int i = 0; i < map.getHeight(); i++) {
			for (int j = 0; j < map.getWidth(); j++) {
				if (platformIndicators[i][j] && ((j + 1 != map.getWidth() && platformIndicators[i][j + 1])
						|| (i + 1 == map.getWidth() || !platformIndicators[i + 1][j]))) {
					int length = 0;
					while (i == map.getHeight() - 1
							|| (j + length) < map.getWidth() && platformIndicators[i][j + length]) {
						if (!platformIndicators[i + 1][j + length])
							platformIndicators[i][j + length] = false;
						length += 1;
					}
					Rectangle r = new Rectangle(j * map.getTileWidth(), i * map.getTileHeight(),
							length * map.getTileWidth(), map.getTileHeight());
					platformHitboxes.add(r);
					link.addCollidable(r);
					j = j + length;
					continue;
				}
			}
		}
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				if (platformIndicators[j][i]) {
					int length = 0;
					while((j+length) < map.getHeight() && platformIndicators[j+length][i]){
						platformIndicators[j+length][i] = false;
						length += 1;
					}
					Rectangle r = new Rectangle(i * map.getTileWidth(), j * map.getTileHeight(), map.getTileWidth(),
							length * map.getTileHeight());
					platformHitboxes.add(r);
					link.addCollidable(r);
					j = j + length;
					continue;
				}
			}
		}
		ghost.setVisible(false);
		ghost.setHasPhysics(true);
		ghost.addAnimation("float", 0, 2, 75000000, 1, 0);
		link.setPositionX(startingX);
		link.setPositionY(startingY);
		link.setScaleX(.3);
		link.setScaleY(.3);
		link.setHasPhysics(true);
		link.addAnimation("run_right", 0, 9, 75000000, 1, 7);
		link.addAnimation("run_left", 0, 9, 75000000, 1, 5);
		locationTracker = new ArrayList<double[]>();
		nextGhost = new ArrayList<double[]>();
		record = true;
		draw = true;
	}

	/**
	 * Engine will automatically call this update method once per frame and pass
	 * to us the set of keys (as strings) that are currently being pressed down
	 */
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		if (link != null && link.hasPhysics()) {

			// attempt at fixing some xVel physics
			if (!(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_A))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT)))) {
				if (onGhost)
					link.setVelocityX(ghost.getVelocityX());
				else
					link.setVelocityX((float) (link.getVelocityX() * HORIZONTAL_MOVEMENT_DECAY));
			}

			if ((pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_W))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP)))
					&& (!(link.getPlatform() == null) || link.getVelocityY() == 0) && link.getAccelerationY() == 0) {
				if (onGhost)
					link.setVelocityY((float) -JUMP_UP_DELTA + ghost.getVelocityY());
				else
					link.setVelocityY((float) -JUMP_UP_DELTA);
			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_A))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))) {
				if (onGhost)
					link.setVelocityX((float) -HORIZONTAL_MOVEMENT_DELTA + ghost.getVelocityX());
				else
					link.setVelocityX(-HORIZONTAL_MOVEMENT_DELTA);
				if (link.setAnimation("run_left"))
					link.play();
				link.setPlaying(true);

			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))) {
				if (onGhost)
					link.setVelocityX((float) HORIZONTAL_MOVEMENT_DELTA + ghost.getVelocityX());
				else
					link.setVelocityX(HORIZONTAL_MOVEMENT_DELTA);
				if (link.setAnimation("run_right"))
					link.play();
				link.setPlaying(true);
			} else if (link.getPlatform() != null) {
				// link.setVelocityX(link.getPlatform().getVelocityX());
				// link.setVelocityY(link.getPlatform().getVelocityY());
			}
		}
		if (record) {
			double[] ghostData = new double[4];
			ghostData[0] = link.getPositionX();
			ghostData[1] = link.getPositionY();
			ghostData[2] = link.getVelocityX();
			ghostData[3] = link.getVelocityY();
			nextGhost.add(ghostData);
		}
		if (ghost != null && ghost.isVisible() && locationTracker != null) {
			if (locationTracker.size() > currIndex) {
				ghost.setPositionX((int) locationTracker.get(currIndex)[0]);
				ghost.setPositionY((int) locationTracker.get(currIndex)[1]);
				currIndex++;
				if (locationTracker.size() > currIndex) {
					ghost.setVelocityX((float) locationTracker.get(currIndex)[2]);
					ghost.setVelocityY((float) locationTracker.get(currIndex)[3]);
				}
			} else {
				currIndex = 0;
			}
		}
		if (onGhost) {
			float yVelo = link.getVelocityY();
			link.setPlatform(ghost.getNextHitbox());
			link.setVelocityY(yVelo);
		}
		if (link != null && ghost != null && ghost.isVisible() && !onGhost) {
			if (link.checkPlatformCollision(ghost.getNextHitbox())) {
				link.setPlatform(ghost.getNextHitbox());
				onGhost = true;
			}
		}
		if (link != null && link.getPlatform() != null) {
			if (onGhost) {
				if (!link.checkStillOnPlatform(ghost.getNextHitbox())) {
					link.setPlatform(null);
					onGhost = !onGhost;
				}
			} else if (!link.checkStillOnPlatform(link.getPlatform())) {
				link.setPlatform(null);
			}
		}
		if (link != null && platformHitboxes != null && link.getPlatform() == null) {
			for (Rectangle platform : platformHitboxes) {
				if (link.checkPlatformCollision(platform)) {
					link.setPlatform(platform);
					break;
				}
			}
		}
		if (spikes != null) {
			for (Sprite spike : spikes) {
				if (link != null && spike != null && locationTracker != null) {
					if (link.collidesWith(spike.getHitbox())) {
						record = false;
						locationTracker.clear();
						for (double[] point : nextGhost) {
							locationTracker.add(point);
						}
						nextGhost.clear();
						currIndex = 0;

						ghost.setVisible(true);
						link.setPositionX(startingX);
						link.setPositionY(startingY);
						link.setVelocityX(0);
						link.setVelocityY(0);
						link.setAccelerationY(0);
						record = true;
						deathCount += 1;
						break;
					}
				}
			}
		}
		if (!pickedUp) {
			if (ring != null && link != null) {
				if (link.collidesWith(ring.getHitbox()) && ringGrabbed != null) {
					TweenJuggler.addTween(ringGrabbed);
					pickedUp = true;
				}
			}
		}
		if (TweenJuggler.getInstance() != null)
			TweenJuggler.nextFrame();
		/*
		 * Make sure mario is not null. Sometimes Swing can auto cause an extra
		 * frame to go before everything is initialized
		 */
		// attempting a reset button:
		if (link != null && pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_R))) {

			locationTracker.clear();
			nextGhost.clear();
			currIndex = 0;
			ghost.setVisible(false);
			link.setPositionX(startingX);
			link.setPositionY(startingY);
			link.setVelocityX(0);
			link.setVelocityY(0);
			/*
			 * if (link.getPlatform() == ghost) { link.setPlatform(null); }
			 */
			record = true;
			deathCount = 0;
		}
		if (link != null && pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_Y))) {

			currIndex = 0;
			record = true;
			/*
			 * if (link.getPlatform() == ghost) { link.setPlatform(null); }
			 */
		}

		if (link != null)
			link.update(pressedKeys);
	}

	/**
	 * Engine automatically invokes draw() every frame as well. If we want to
	 * make sure mario gets drawn to the screen, we need to make sure to
	 * override this method and call mario's draw method.
	 */
	@Override
	public void draw(Graphics g) {
		if (draw) {
			super.draw(g);
			/*
			 * Same, just check for null in case a frame gets thrown in before
			 * Mario is initialized
			 */
			if (sprites != null) {
				for (Sprite s : sprites) {
					s.draw(g);
				}
			}
			if (ghost != null)
				ghost.draw(g);
			if (link != null)
				link.draw(g);
			g.drawString("PAR: 3", 450, 110);
			g.drawString("Death Count: " + deathCount, 450, 90);

		}
	}

	/**
	 * Quick main class that simply creates an instance of our game and starts
	 * the timer that calls update() and draw() every frame
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static void main(String[] args) {
		GhostGame game = new GhostGame();
		game.start();

	}
}
