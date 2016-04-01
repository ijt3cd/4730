package edu.virginia.lab1test;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenParam;
import edu.virginia.engine.display.TweenTransition;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.events.TweenEvent;
import edu.virginia.engine.sound.SoundManager;

/**
 * Example game that utilizes our engine. We can create a simple prototype game
 * with just a couple lines of code although, for now, it won't be a very fun
 * game :)
 */
public class LabOneGame extends Game {
	/* Create a sprite object for our game. We'll use mario */
	AnimatedSprite link = new AnimatedSprite("Link", "LinkSprites.png", 120, 130);
	AnimatedSprite ghost = new AnimatedSprite("ghost", "GhostSprites.png", 32, 48);
	Sprite platformOne = new Sprite("Platform1", "PlatformSprite.png");
	Sprite floor = new Sprite("Floor", "PlatformSprite.png");
	QuestManager questManager = new QuestManager();
	ArrayList<Sprite> platforms;
	ArrayList<Point> locationTracker;
	int currIndex;
	int lastIndex;
	boolean record;
	Tween coinGrabbed;

	/**
	 * Constructor. See constructor in Game.java for details on the parameters
	 * given
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public LabOneGame() {
		super("Lab One Test Game", 500, 300);
		ghost.setVisible(false);
		ghost.setHasPhysics(true);
		ghost.setScaleX(1.875);
		ghost.setScaleY(1.355);
		ghost.addAnimation("float", 0, 2, 75000000, 1, 0);
		link.setPositionX(0);
		link.setPositionY(230);
		link.setxPos(0);
		link.setyPos(230);
		link.setScaleX(.5);
		link.setScaleY(.5);
		link.setHasPhysics(true);
		link.addAnimation("run_right", 0, 9, 75000000, 1, 7);
		link.addAnimation("run_left", 0, 9, 75000000, 1, 5);
		platformOne.setScaleX(.5);
		platformOne.setScaleY(.2);
		platformOne.setxPos(200);
		platformOne.setyPos(180);
		platformOne.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
		link.addCollidable(platformOne);
		floor.setScaleX(5);
		floor.setScaleY(.2);
		floor.setVisible(false);
		floor.setxPos(0);
		floor.setyPos(280);
		floor.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
		ghost.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
		platforms = new ArrayList<Sprite>();
		platforms.add(platformOne);
		platforms.add(floor);
		platforms.add(ghost);
		locationTracker = new ArrayList<Point>();
		currIndex = 0;
		lastIndex = 0;
		record = false;
	}

	/**
	 * Engine will automatically call this update method once per frame and pass
	 * to us the set of keys (as strings) that are currently being pressed down
	 */
	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		if (link != null && link.hasPhysics()) {
			if ((pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_W))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP))) && link.isOnFloor()) {
				link.setVelocityY((float) -5.75);
				link.setOnFloor(false);
			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_A))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))) {
				link.setVelocityX(-5);
				if (link.setAnimation("run_left"))
					link.play();
				link.setPlaying(true);

			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))) {
				link.setVelocityX(5);
				if (link.setAnimation("run_right"))
					link.play();
				link.setPlaying(true);
			}
			if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_N))) {
				record = true;
			}
			if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_M))) {
				if (record) {
					lastIndex = currIndex;
					record = false;
					if (!ghost.isVisible()) {
						ghost.setVisible(true);
					}
					currIndex = 0;
				}
			}
		}
		if (record) {
			if (currIndex < 1000) {
				locationTracker.add(new Point(link.getxPos(), link.getyPos()));
				currIndex++;
			} else {
				record = false;
			}
		}
		if (link != null && platformOne != null && questManager != null) {
			if (link.checkPlatformCollision(platformOne)) {
				platformOne.dispatchEvent(
						new PlatformLandingEvent(PlatformLandingEvent.PLATFORM_LANDED_ON, platformOne, link));
				platformOne.addEventListener(questManager, PlatformLandingEvent.PLATFORM_FALLEN_OFF);
				platformOne.removeEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
			}
		}
		if (link != null && floor != null && questManager != null) {
			if (link.checkPlatformCollision(floor)) {
				floor.dispatchEvent(new PlatformLandingEvent(PlatformLandingEvent.PLATFORM_LANDED_ON, floor, link));
				floor.addEventListener(questManager, PlatformLandingEvent.PLATFORM_FALLEN_OFF);
				floor.removeEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
			}
		}
		if (link != null && ghost != null && ghost.isVisible() && questManager != null) {
			if (link.checkPlatformCollision(ghost)) {
				ghost.dispatchEvent(new PlatformLandingEvent(PlatformLandingEvent.PLATFORM_LANDED_ON, ghost, link));
				ghost.addEventListener(questManager, PlatformLandingEvent.PLATFORM_FALLEN_OFF);
				ghost.removeEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
			}
		}
		if (link != null && platforms != null) {
			for (int i = 0; i < platforms.size(); i++) {
				if (link.getHitbox().intersects(platforms.get(i).getReducedHitbox())) {
					Rectangle spriteRec = link.getHitbox();
					Rectangle platformRec = platforms.get(i).getHitbox();
					boolean halfOnPlatformRight = (spriteRec.getMaxX()
							- platformRec.getMaxX() < ((this.getUnscaledWidth() * this.getScaleX()) / 2.0));
					boolean halfOnPlatformLeft = (platformRec.getX()
							- spriteRec.getX() < ((this.getUnscaledWidth() * this.getScaleX()) / 2.0));
					if (link.getHitbox().getMaxY() < platforms.get(i).getReducedHitbox().getMaxY()
							&& (halfOnPlatformRight || halfOnPlatformLeft))
						break;
				}
				if (i == platforms.size() - 1) {
					link.setOnFloor(false);
				}
			}
		}
		if (ghost != null && ghost.isVisible()) {
			ghost.setxPos(locationTracker.get(currIndex).x);
			ghost.setyPos(locationTracker.get(currIndex).y);
			currIndex++;
			if (currIndex >= lastIndex) {
				currIndex = 0;
				ghost.setVisible(false);
			}
		}
		/*
		 * Make sure mario is not null. Sometimes Swing can auto cause an extra
		 * frame to go before everything is initialized
		 */
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
		super.draw(g);
		/*
		 * Same, just check for null in case a frame gets thrown in before Mario
		 * is initialized
		 */
		if (link != null)
			link.draw(g);
		if (platformOne != null)
			platformOne.draw(g);
		if (ghost != null)
			ghost.draw(g);
	}

	/**
	 * Quick main class that simply creates an instance of our game and starts
	 * the timer that calls update() and draw() every frame
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static void main(String[] args) {
		LabOneGame game = new LabOneGame();
		game.start();

	}
}
