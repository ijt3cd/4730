package edu.virginia.game;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenParam;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.events.PlatformLandingEvent;
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

	public static int width = 525;
	public static int height = 525;

	SoundManager sm = new SoundManager();
	File bgm = new File("resources/brm.wav");

	/* Create a sprite object for our game. We'll use mario */
	AnimatedSprite link = new AnimatedSprite("Link", "LinkSprites.png", 120, 130);
	AnimatedSprite ghost = new AnimatedSprite("ghost", "GhostSprites.png", 32, 48);
	Sprite platformOne = new Sprite("Platform1", "PlatformSprite.png");
	Sprite ring = new Sprite("Ring", "Ring.png");
	Sprite floor = new Sprite("Floor", "PlatformSprite.png");
	Sprite spike1 = new Sprite("Spike1", "SpikeSprite.png");
	Sprite spike2 = new Sprite("Spike2", "SpikeSprite.png");
	Sprite spike3 = new Sprite("Spike3", "SpikeSprite.png");
	Sprite spike4 = new Sprite("Spike4", "SpikeSprite.png");
	Sprite spike5 = new Sprite("Spike5", "SpikeSprite.png");
	Sprite spike6 = new Sprite("Spike6", "SpikeSprite.png");
	Sprite spike7 = new Sprite("Spike7", "SpikeSprite.png");
	Sprite spike8 = new Sprite("Spike8", "SpikeSprite.png");
	Sprite spike9 = new Sprite("Spike9", "SpikeSprite.png");
	Sprite spike10 = new Sprite("Spike10", "SpikeSprite.png");
	Sprite spike11 = new Sprite("Spike11", "SpikeSprite.png");
	Sprite spike12 = new Sprite("Spike12", "SpikeSprite.png");
	QuestManager questManager = new QuestManager();
	ArrayList<Sprite> sprites;
	ArrayList<Sprite> platforms;
	ArrayList<Sprite> spikes;
	ArrayList<double[]> locationTracker;
	ArrayList<double[]> nextGhost;
	int currIndex = 0;
	boolean record;
	boolean pickedUp;
	Tween ringGrabbed;
	Tween ringFade;
	int deathCount = 0;
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
		
		for(MapLayer m : map.getLayers()){
			if(m instanceof TileLayer){
				TileLayer l = (TileLayer) m;
				for(int i = 0; i < map.getHeight(); i++){
					for(int j = 0; j < map.getWidth(); j++){
						Tile t = l.getTileAt(j, i);
						if(t != null){
							Sprite s = new Sprite(""+t.getId());
							s.setImage((BufferedImage) t.getImage());
							s.setPositionX(j*t.getWidth());
							s.setPositionY(i*t.getHeight());
							sprites.add(s);
						}
					}
				}
			}

		}  
		ghost.setVisible(false);
		ghost.setHasPhysics(true);
		ghost.setScaleX(1.875);
		ghost.setScaleY(1.355);
		ghost.addAnimation("float", 0, 2, 75000000, 1, 0);
		link.setPositionX(0);
		link.setPositionY(height - 70);
		link.setPositionX(0);
		link.setPositionY(height - 70);
		link.setScaleX(.5);
		link.setScaleY(.5);
		link.setHasPhysics(true);
		link.addAnimation("run_right", 0, 9, 75000000, 1, 7);
		link.addAnimation("run_left", 0, 9, 75000000, 1, 5);
		ring.setScaleX(0.05);
		ring.setScaleY(0.05);
		ring.setPositionX(850);
		ring.setPositionY(550);
		platformOne.setScaleX(.5);
		platformOne.setScaleY(.5);
		platformOne.setPositionX(250);
		platformOne.setPositionY(500);
		spike1.setPositionX(500);
		spike1.setPositionY(480);
		spike1.setScaleX(.5);
		spike1.setScaleY(.5);
		spike2.setPositionX(500);
		spike2.setPositionY(510);
		spike2.setScaleX(.5);
		spike2.setScaleY(.5);
		spike3.setPositionX(500);
		spike3.setPositionY(540);
		spike3.setScaleX(.5);
		spike3.setScaleY(.5);
		spike4.setPositionX(500);
		spike4.setPositionY(570);
		spike4.setScaleX(.5);
		spike4.setScaleY(.5);
		spike5.setPositionX(700);
		spike5.setPositionY(360);
		spike5.setScaleX(.5);
		spike5.setScaleY(.5);
		spike6.setPositionX(700);
		spike6.setPositionY(390);
		spike6.setScaleX(.5);
		spike6.setScaleY(.5);
		spike7.setPositionX(700);
		spike7.setPositionY(420);
		spike7.setScaleX(.5);
		spike7.setScaleY(.5);
		spike8.setPositionX(700);
		spike8.setPositionY(450);
		spike8.setScaleX(.5);
		spike8.setScaleY(.5);
		spike9.setPositionX(700);
		spike9.setPositionY(480);
		spike9.setScaleX(.5);
		spike9.setScaleY(.5);
		spike10.setPositionX(700);
		spike10.setPositionY(510);
		spike10.setScaleX(.5);
		spike10.setScaleY(.5);
		spike11.setPositionX(700);
		spike11.setPositionY(540);
		spike11.setScaleX(.5);
		spike11.setScaleY(.5);
		spike12.setPositionX(700);
		spike12.setPositionY(570);
		spike12.setScaleX(.5);
		spike12.setScaleY(.5);
		platformOne.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
		link.addCollidable(platformOne);
		floor.setScaleX(5);
		floor.setScaleY(.2);
		floor.setPositionX(0);
		floor.setPositionY(height - 12);
		floor.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
		ghost.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
		platforms = new ArrayList<Sprite>();
		platforms.add(platformOne);
		platforms.add(floor);
		platforms.add(ghost);
		spikes = new ArrayList<Sprite>();
		spikes.add(spike1);
		spikes.add(spike2);
		spikes.add(spike3);
		spikes.add(spike4);
		spikes.add(spike5);
		spikes.add(spike6);
		spikes.add(spike7);
		spikes.add(spike8);
		spikes.add(spike9);
		spikes.add(spike10);
		spikes.add(spike11);
		spikes.add(spike12);
		pickedUp = false;
		locationTracker = new ArrayList<double[]>();
		nextGhost = new ArrayList<double[]>();
		TweenParam centerX = new TweenParam(TweenableParams.X, ring.getPositionX(), 500, 1000);
		TweenParam centerY = new TweenParam(TweenableParams.Y, ring.getPositionY(), 300, 1000);
		TweenParam scaleX = new TweenParam(TweenableParams.SCALE_X, ring.getScaleX(), ring.getScaleX() * 2, 1000);
		TweenParam scaleY = new TweenParam(TweenableParams.SCALE_Y, ring.getScaleY(), ring.getScaleY() * 2, 1000);
		TweenParam fadeOut = new TweenParam(TweenableParams.ALPHA, ring.getAlpha(), 0.0, 1500);
		ringGrabbed = new Tween(ring);
		ringGrabbed.addParam(centerX);
		ringGrabbed.addParam(centerY);
		ringGrabbed.addParam(scaleX);
		ringGrabbed.addParam(scaleY);
		ringGrabbed.addParam(fadeOut);
		record = true;
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
				link.setVelocityX((float) (link.getVelocityX() * 0.90));
			}

			if ((pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_W))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_SPACE))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_UP)))
					&& (!(link.getPlatform() == null) || link.getVelocityY() == 0) && link.getAccelerationY() == 0) {
				if (link.getPlatform() != null) {
					link.setVelocityY((float) (link.getPlatform().getVelocityY() - 5.75));
				} else {
					link.setVelocityY((float) -5.75);
				}
			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_A))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_LEFT))) {
				if (link.getPlatform() != null) {
					link.setVelocityX(link.getPlatform().getVelocityX() - 5);
				} else {
					link.setVelocityX(-5);
				}
				if (link.setAnimation("run_left"))
					link.play();
				link.setPlaying(true);

			} else if (pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_D))
					|| pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_RIGHT))) {
				if (link.getPlatform() != null) {
					link.setVelocityX(link.getPlatform().getVelocityX() + 5);
				} else {
					link.setVelocityX(5);
				}
				if (link.setAnimation("run_right"))
					link.play();
				link.setPlaying(true);
			} else if (link.getPlatform() != null) {
				link.setVelocityX(link.getPlatform().getVelocityX());
				link.setVelocityY(link.getPlatform().getVelocityY());
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
		if (link != null && platforms != null) {
			for (Sprite platform : platforms) {
				if (link != null && platform != null && platform.isVisible() && questManager != null) {
					if (link.checkPlatformCollision(platform) && link.getPlatform() != platform) {
						platform.dispatchEvent(
								new PlatformLandingEvent(PlatformLandingEvent.PLATFORM_LANDED_ON, platform, link));
						platform.addEventListener(questManager, PlatformLandingEvent.PLATFORM_FALLEN_OFF);
						platform.removeEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
					} else if (!link.checkStillOnPlatform(platform) && link.getPlatform() == platform) {
						platform.dispatchEvent(
								new PlatformLandingEvent(PlatformLandingEvent.PLATFORM_FALLEN_OFF, platform, link));
						platform.addEventListener(questManager, PlatformLandingEvent.PLATFORM_LANDED_ON);
						platform.removeEventListener(questManager, PlatformLandingEvent.PLATFORM_FALLEN_OFF);
					}
				}
			}
		}
		if (ghost != null && ghost.isVisible() && locationTracker != null) {
			if (locationTracker.size() > currIndex) {
				ghost.setPositionX((int) locationTracker.get(currIndex)[0]);
				ghost.setPositionY((int) locationTracker.get(currIndex)[1]);
				ghost.setVelocityX((float) locationTracker.get(currIndex)[2]);
				ghost.setVelocityY((float) locationTracker.get(currIndex)[3]);
				currIndex++;
			} else {
				currIndex = 0;
				if (link.getPlatform() == ghost) {
					link.setPlatform(null);
				}
			}
		}
		if (spikes != null) {
			for (Sprite spike : spikes) {
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
						link.setPositionX(0);
						link.setPositionY(height - 70);
						link.setVelocityX(0);
						link.setVelocityY(0);
						record = true;
						deathCount += 1;
						break;
					}
				}
			}
		}
		if (!pickedUp) {
			if (ring != null && link != null) {
				if (link.collidesWith(ring) && ringGrabbed != null) {
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
			link.setPositionX(0);
			link.setPositionY(height - 70);
			link.setVelocityX(0);
			link.setVelocityY(0);
			if (link.getPlatform() == ghost) {
				link.setPlatform(null);
			}
			record = true;
			deathCount = 0;
		}
		if (link != null && pressedKeys.contains(KeyEvent.getKeyText(KeyEvent.VK_Y))) {

			currIndex = 0;
			record = true;
			if (link.getPlatform() == ghost) {
				link.setPlatform(null);
			}
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
		super.draw(g);
		/*
		 * Same, just check for null in case a frame gets thrown in before Mario
		 * is initialized
		 */

/*		if (platformOne != null)
			platformOne.draw(g);
		if (ghost != null)
			ghost.draw(g);

		if (link != null)
			link.draw(g);
		if (ring != null) {
			ring.draw(g);
		}
		if (spikes != null) {
			for (Sprite spike : spikes) {
				if (spike != null) {
					spike.draw(g);
				}
			}
		}
		g.drawString("PAR: 3", 450, 110);
		g.drawString("Death Count: " + deathCount, 450, 90); */
		if(sprites != null){
			for(Sprite s : sprites){
				s.draw(g);
			}
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
