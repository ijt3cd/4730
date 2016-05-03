package edu.virginia.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import edu.virginia.engine.controller.GamePad;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.events.DeathEvent;
import edu.virginia.engine.events.IEventListener;
import edu.virginia.engine.events.LevelCompleteEvent;
import edu.virginia.engine.events.PickedUpEvent;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

public class LevelManager extends Game {

	public static int width = 30 * 22;
	public static int height = 30 * 22;
	
	private static final int INTERVAL = 10;

	SoundManager sm = new SoundManager();

	/* Create a sprite object for our game. We'll use mario */
	DisplayObjectContainer game = new DisplayObjectContainer("game");
	AnimatedSprite link = new AnimatedSprite("Link", "LinkSprites.png", 120, 130);
	AnimatedSprite ghost = new AnimatedSprite("ghost", "ghost_sheet.png", 32, 32);
	QuestManager questManager = new QuestManager();
	ArrayList<Sprite> sprites;
	ArrayList<String> levels;
	ArrayList<Rectangle> platformHitboxes;
	ArrayList<Rectangle> spikeHitboxes;
	ArrayList<double[]> locationTracker;
	ArrayList<double[]> nextGhost;
	int currIndex = 0;
	boolean record;
	float startingX;
	float startingY;
	Tween ringGrabbed;
	Tween ringFade;
	int deathCount = 0;
	int animationType = 0;
	int level = 0;
	float ghostOffset = 0;
	boolean draw;
	boolean onGhost;
	boolean reversePowered;
	Map map;

	Rectangle door;
	Sprite doorSprite;
	Rectangle button;
	Sprite buttonSprite;
	Rectangle goal;
	Rectangle reversePower;
	Sprite reversePowerSprite;

	public LevelManager() {
		super("Ghost Game", width, height);
		sm.playMusic(new File("resources/Background.wav"));
		ArrayList<IEventListener> soundListeners = new ArrayList<IEventListener>();
		soundListeners.add(sm);
		listeners.put(LevelCompleteEvent.LEVEL_COMPLETE, soundListeners);
		listeners.put(DeathEvent.DEAD_EVENT, soundListeners);
		listeners.put(PickedUpEvent.COIN_PICKED_UP, soundListeners);
		levels = new ArrayList<String>();

		
		levels.add("resources/level8.tmx");
		levels.add("resources/level1.tmx");
		levels.add("resources/level10.tmx");
		levels.add("resources/level3.tmx");
		levels.add("resources/level9.tmx");
		levels.add("resources/level2.tmx");
		levels.add("resources/leveltest.tmx");
		levels.add("resources/level5.tmx");
		levels.add("resources/level6.tmx");
		

		levels.add("resources/big_level2.tmx");
		levels.add("resources/big_level1.tmx");
		levels.add("resources/victory.tmx");

		ghost.setVisible(false);
		this.initializeLevel();
		ghost.addAnimation("run_right", 0, 2, 75000000, 1, 2);
		ghost.addAnimation("run_left", 0, 2, 75000000, 1, 1);
		link.setScaleX(.3);
		link.setScaleY(.3);
		link.setHasPhysics(true);
		link.addAnimation("run_right", 0, 9, 75000000, 1, 7);
		link.addAnimation("run_left", 0, 9, 75000000, 1, 5);
		locationTracker = new ArrayList<double[]>();
		nextGhost = new ArrayList<double[]>();
		record = true;
		draw = true;
		reversePowered = false;

	}

	/**
	 * Engine will automatically call this update method once per frame and pass
	 * to us the set of keys (as strings) that are currently being pressed down
	 */
	@Override
	public void update(ArrayList<String> pressedKeys, ArrayList<GamePad> gamePads) {
		super.update(pressedKeys, gamePads);
		int w = getMainFrame().getWidth();
		int h = getMainFrame().getHeight();
		int square = Math.min(w, h);
		animationType = 0;
		if (link != null && goal != null) {
			if (link.getHitbox().intersects(goal)||pressedKeys.contains("P")) {
				draw = false;
				ghost.setVisible(false);
				reversePowered = false;
				deathCount = 0;
				sprites.clear();
				platformHitboxes.clear();
				spikeHitboxes.clear();
				nextGhost.clear();
				locationTracker.clear();
				button = null;
				door = null;
				doorSprite = null;
				buttonSprite = null;
				goal = null;
				reversePower = null;
				reversePowerSprite = null;
				link.clearCollidables();
				link.setVelocityX(0);
				link.setVelocityY(0);
				link.setHasPhysics(false);
				link.setAccelerationY(0);
				game.removeAll();
				this.dispatchEvent(new LevelCompleteEvent(LevelCompleteEvent.LEVEL_COMPLETE, this));
				this.initializeLevel();
			}
		}
		// checks whether button is being touched by ghost, removes door
		if (link != null && platformHitboxes != null && game != null && ghost != null && button != null
				&& door != null) {
			if (!game.getChildren().contains(doorSprite)) {
				game.addChild(doorSprite);

			}
			if (!platformHitboxes.contains(door)) {
				platformHitboxes.add(door);
				// System.out.println("??");
			}
			if (!link.getCollidableObjects().contains(door)) {
				link.addCollidable(door);
				// System.out.println("???");
			}
			if ((ghost.getHitbox().intersects(button) && ghost.isVisible()) || link.getHitbox().intersects(button)) {
				if (link.getPlatform() != null) {
					if (link.getPlatform().equals(door)) {
						link.setPlatform(null);
					}
				}
				game.removeChild(doorSprite);
				platformHitboxes.remove(door);
				link.getCollidableObjects().remove(door);
				// System.out.println("z");

			}
		}

		if (link != null && platformHitboxes != null && game != null && reversePower != null && !reversePowered) {
			if (link.getHitbox().intersects(reversePower)) {
				this.dispatchEvent(new PickedUpEvent(PickedUpEvent.COIN_PICKED_UP, this));
				game.removeChild(reversePowerSprite);
				reversePowered = true;
			}
		}

		// game.setScaleX((double)square/width);
		// game.setScaleY((double)square/height);

		// game.setScaleX(square/width);
		// if(game != null)
		// game.setScaleY((double)square/height);

		// game.update(pressedKeys);
		//
		
		
		if (link != null && link.hasPhysics()) {
			
			// attempt at fixing some xVel physics
			if (!(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_A))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT)))) {
				link.setVelocityX((float) (link.getVelocityX() * HORIZONTAL_MOVEMENT_DECAY));
			}
			// Gravity logic is dependent on being on a platform, ghost is no
			// longer considered a platform, this line allows jumping off the
			// ghost
			if (onGhost)
				link.setAccelerationY(0.0f);
			if ((pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_W))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP))
					|| (!gamePads.isEmpty() && gamePads.get(0).isButtonPressed(GamePad.A)))
					&& (link.getPlatform() != null || onGhost) && link.getAccelerationY() == 0) {
				link.setVelocityY((float) -JUMP_UP_DELTA);
				onGhost = false;
				ghostOffset = 0;
			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_A))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))
					|| (!gamePads.isEmpty() && gamePads.get(0).isDPadPressed(GamePad.DPAD_LEFT))) {
				link.setVelocityX(-HORIZONTAL_MOVEMENT_DELTA);
				if (onGhost) {
					ghostOffset += -HORIZONTAL_MOVEMENT_DELTA;
					if (ghostOffset < -GHOST_EXTENSION) {
						onGhost = false;
						ghostOffset = 0;
					}
				}
				animationType = 1;
				if (link.setAnimation("run_left"))
					link.play();
				link.setPlaying(true);

			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))
					|| (!gamePads.isEmpty() && gamePads.get(0).isDPadPressed(GamePad.DPAD_RIGHT))) {
				link.setVelocityX(HORIZONTAL_MOVEMENT_DELTA);
				if (onGhost) {
					ghostOffset += HORIZONTAL_MOVEMENT_DELTA;
					if (ghostOffset > GHOST_EXTENSION) {
						onGhost = false;
						ghostOffset = 0;
					}
				}
				animationType = 2;
				if (link.setAnimation("run_right"))
					link.play();
				link.setPlaying(true);
			}
		}
		/*
		 * Grabs the position at each frame to give to the ghost
		 */
		if (record) {
			double[] ghostData = new double[3];
			ghostData[0] = link.getPositionX();
			ghostData[1] = link.getPositionY();
			ghostData[2] = animationType;
			nextGhost.add(ghostData);
		}
		/*
		 * Updates the ghosts position
		 */
		if (ghost != null && ghost.isVisible() && locationTracker != null) {
			int idx = currIndex / INTERVAL;
			if (locationTracker.size() > idx) {
				int exactIndex = idx;
				if (reversePowered) {
					exactIndex = locationTracker.size() - 1 - idx;
				}
				ghost.setPositionX((float) locationTracker.get(exactIndex)[0]);
				ghost.setPositionY((float) locationTracker.get(exactIndex)[1]);
				int ani = (int) locationTracker.get(exactIndex)[2];
				if (ani == 1) {
					if (ghost.setAnimation("run_left")) {
						ghost.play();
					}
				} else if (ani == 2) {
					if (ghost.setAnimation("run_right")) {
						ghost.play();
					}
				}
				if(pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_B))) {
					currIndex++;
				} else {
					currIndex += INTERVAL;
				}
			} else {
				if (onGhost) {
					onGhost = false;
				}
				currIndex = 0;

			}
		}
		/*
		 * Checks if the player has landed on the ghost Still need to make this
		 * collision easier for the player to force
		 */
		if (link != null && ghost != null && ghost.isVisible() && !onGhost) {
			if (link.checkPlatformCollision(ghost.getNextHitbox())) {
				onGhost = true;
			}
		}
		/*
		 * Checks to see if the player is still on the platform that he last
		 * landed on
		 */
		if (link != null && link.getPlatform() != null) {
			if (!link.checkStillOnPlatform(link.getPlatform())) {
				link.setPlatform(null);
			}
		}

		// checks whether button is being touched by link.
		// if (link != null && platformHitboxes != null && game != null ){
		// if(!game.getChildren().contains(doorSprite)){
		// game.addChild(doorSprite);
		// }
		// if(!platformHitboxes.contains(door)){
		// platformHitboxes.add(door);
		// }
		// if(!link.getCollidableObjects().contains(door)){
		// link.addCollidable(door);
		// }
		// if (link.getHitbox().intersects(button)) {
		// game.removeChild(doorSprite);
		// platformHitboxes.remove(door);
		// link.getCollidableObjects().remove(door);
		//
		// }
		// }

		/*
		 * Checks all platforms in the world to see if the player has landed on
		 * one
		 */
		if (link != null && platformHitboxes != null && link.getPlatform() == null) {

			for (Rectangle platform : platformHitboxes) {
				if (link.checkPlatformCollision(platform)) {
					link.setPlatform(platform);

					break;
				}

			}

		}
		/*
		 * Checks if the player has collided with an obstacle If a collision is
		 * present, the player is set back at the spawn point along with a ghost
		 */
		if (spikeHitboxes != null) {
			for (Rectangle spike : spikeHitboxes) {
				if (link != null && spike != null && locationTracker != null) {
					if (link.collidesWith(spike)) {
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
						onGhost = false;
						this.dispatchEvent(new DeathEvent(DeathEvent.DEAD_EVENT, this));
						break;
					}
				}
			}
		}
		if (TweenJuggler.getInstance() != null)
			TweenJuggler.nextFrame();

		if (link != null && (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_R))
				|| (!gamePads.isEmpty() && gamePads.get(0).isButtonPressed(GamePad.BUTTON_SELECT)))) {

			locationTracker.clear();
			nextGhost.clear();
			currIndex = 0;
			ghost.setVisible(false);
			onGhost = false;
			ghostOffset = 0;
			link.setPositionX(startingX);
			link.setPositionY(startingY);
			link.setVelocityX(0);
			link.setVelocityY(0);
			record = true;
			deathCount = 0;
			reversePowered = false;
			if (reversePowerSprite != null)
				game.addChild(reversePowerSprite);
		}
		/*
		 * Resets current ghost loop for convenience
		 */
		if (link != null && (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_Y))
				|| (!gamePads.isEmpty() && gamePads.get(0).isButtonPressed(GamePad.Y)))) {
			currIndex = 0;
			onGhost = false;
			ghostOffset = 0;
			record = true;
		}
		/*
		 * Checks if the player has collided with any of the platforms Any
		 * collisions will knock the player off of the ghost
		 */
		if (link != null) {
			boolean bumped = link.checkCollidables();
			if (onGhost && bumped) {
				onGhost = false;
				ghostOffset = 0;
			}
		}
		/*
		 * Locks the player onto the ghost unless this update loop contained a
		 * user input or the player hit another platform while riding the ghost
		 */
		if (onGhost) {
			link.setPositionX(ghost.getPositionX() + ghostOffset);
			link.setPositionY((float) (ghost.getPositionY() - (link.getUnscaledHeight() * link.getScaleY())));
			link.setVelocityX(0);
			link.setVelocityY(0);
		}
		if (link != null)
			link.update(pressedKeys, gamePads);
		if (ghost != null)
			ghost.updateImage();
		if (doorSprite != null)
			doorSprite.update(pressedKeys, gamePads);
		if (buttonSprite != null)
			buttonSprite.update(pressedKeys, gamePads);
		if (game != null) {
			// game.update(pressedKeys);
		}

	}

	public void initializeLevel() {
		getMainFrame().setBounds(0, 0, width, height); // Fixing weird size bug.
		sprites = new ArrayList<Sprite>();
		if (levels.get(level).equals("resources/victory.tmx"))
			link.setVisible(false);
		TMXMapReader mapReader = new TMXMapReader();
		try {
			map = mapReader.readMap(levels.get(level));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		level++;
		getMainFrame().setSize(map.getWidth() * map.getTileWidth(), map.getHeight() * map.getTileHeight());
		TweenJuggler.getInstance();

		// spikes = new ArrayList<Sprite>();

		// SoundManager.playMusic(bgm);
		spikeHitboxes = new ArrayList<Rectangle>();

		platformHitboxes = new ArrayList<Rectangle>();
		boolean[][] platformIndicators = new boolean[map.getHeight()][map.getWidth()];
		boolean[][] spikeIndicators = new boolean[map.getHeight()][map.getWidth()];
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
								spikeIndicators[i][j] = true;
							} else if (l.getName().equals("Spawn")) {
								startingX = j * t.getWidth();
								startingY = i * t.getHeight();
							} else if (l.getName().equals("Door")) {
								door = new Rectangle(j * map.getTileWidth(), i * map.getTileHeight(),
										map.getTileWidth(), map.getTileHeight());
								doorSprite = s;
								platformHitboxes.add(door);
								link.addCollidable(door);
							} else if (l.getName().equals("Button")) {
								button = new Rectangle(j * map.getTileWidth(), i * map.getTileHeight(),
										map.getTileWidth(), map.getTileHeight());
								buttonSprite = s;
								// platformHitboxes.add(button);
							} else if (l.getName().equals("Goal")) {
								goal = new Rectangle(j * map.getTileWidth(), i * map.getTileHeight(),
										map.getTileWidth(), map.getTileHeight());
							} else if (l.getName().equals("PowerUps")) {
								reversePower = new Rectangle(j * map.getTileWidth(), i * map.getTileHeight(),
										map.getTileWidth(), map.getTileHeight());
								reversePowerSprite = s;
							}
						}
					}
				}
			}
		}
		/*
		 * Two passes, grabbing horizontal tile groups then vertical tile groups
		 * for both the platforms and spikes(obstacles) to group adjacent tiles
		 * into one larger hitbox
		 */
		for (int i = 0; i < map.getHeight() - 1; i++) {
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
			if (platformIndicators[map.getHeight() - 1][i]) {
				int length = 0;
				while ((i + length < map.getWidth()) && platformIndicators[map.getHeight() - 1][i + length]) {
					length += 1;
				}
				Rectangle r = new Rectangle(i * map.getTileWidth(), (map.getWidth() - 1) * map.getTileHeight(),
						length * map.getTileWidth(), map.getTileHeight());
				platformHitboxes.add(r);
				link.addCollidable(r);
				i = i + length;
			}
		}
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				if (platformIndicators[j][i]) {
					int length = 0;
					while ((j + length) < map.getHeight() && platformIndicators[j + length][i]) {
						platformIndicators[j + length][i] = false;
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
		for (int i = 0; i < map.getHeight() - 1; i++) {
			for (int j = 0; j < map.getWidth(); j++) {
				if (spikeIndicators[i][j] && ((j + 1 != map.getWidth() && spikeIndicators[i][j + 1])
						|| (i + 1 == map.getWidth() || !spikeIndicators[i + 1][j]))) {
					int length = 0;
					while (i == map.getHeight() - 1
							|| (j + length) < map.getWidth() && spikeIndicators[i][j + length]) {
						if (!spikeIndicators[i + 1][j + length])
							spikeIndicators[i][j + length] = false;
						length += 1;
					}
					Rectangle r = new Rectangle((j * map.getTileWidth()) + 8, (i * map.getTileHeight()) + 8,
							(length * map.getTileWidth()) - 8, (map.getTileHeight() - 8));
					spikeHitboxes.add(r);
					j = j + length;
					continue;
				}
			}
		}
		for (int i = 0; i < map.getWidth(); i++) {
			if (spikeIndicators[map.getHeight() - 1][i]) {
				int length = 0;
				while ((i + length < map.getWidth()) && spikeIndicators[map.getHeight() - 1][i + length]) {
					length += 1;
				}
				Rectangle r = new Rectangle(i * map.getTileWidth(), (map.getWidth() - 1) * map.getTileHeight(),
						length * map.getTileWidth(), map.getTileHeight());
				spikeHitboxes.add(r);
				i = i + length;
			}
		}
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				if (spikeIndicators[j][i]) {
					int length = 0;
					while ((j + length) < map.getHeight() && spikeIndicators[j + length][i]) {
						spikeIndicators[j + length][i] = false;
						length += 1;
					}
					Rectangle r = new Rectangle(i * map.getTileWidth() + 8, j * map.getTileHeight() + 8,
							map.getTileWidth() - 8, length * map.getTileHeight() - 8);
					spikeHitboxes.add(r);
					j = j + length;
					continue;
				}
			}
		}
		ghost.setHasPhysics(true);
		link.setPositionX(startingX);
		link.setPositionY(startingY);
		for (Sprite s : sprites) {
			game.addChild(s);
		}
		game.addChild(ghost);
		game.addChild(link);
		link.setHasPhysics(true);
		record = true;
		draw = true;
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

			//
			//
			game.draw(g);
			//
			//
			// g.drawString("PAR: 3", 450, 110);
			// g.drawString("Death Count: " + deathCount, 450, 90);
			//
			//

			// if (sprites != null) {
			// for (Sprite s : sprites) {
			// s.draw(g);
			// }
			// }
			// if (ghost != null)
			// ghost.draw(g);
			// if (link != null)
			// link.draw(g);
			g.drawString("Try to hit par!", width / 2 + 60, 50);
			g.drawString("Death Count: " + deathCount, width / 2 + 60, 30);

		}
	}

	public static void main(String[] args) {
		LevelManager game = new LevelManager();

		game.start();

	}

}
