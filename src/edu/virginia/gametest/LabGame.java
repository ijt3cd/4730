package edu.virginia.gametest;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.Game;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.display.Tween;
import edu.virginia.engine.display.TweenJuggler;
import edu.virginia.engine.display.TweenableParams;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.TweenEvent;

public class LabGame extends Game {

	private static final float DELTA = 0.2f;
	QuestManager qm = new QuestManager();
	SoundManager sm = new SoundManager();
	File bgm = new File("resources/brm.wav");
	Event PickedUpEvent = new Event();

	private AnimatedSprite megaman;
	private AnimatedSprite ghost;
	private Sprite wall;
	// AnimatedSprite startWall = new AnimatedSprite("startWall", "wall.png",
	// "l");
	// ArrayList<AnimatedSprite> walls = new ArrayList<>();
	ArrayList<Sprite> spriteList;

	ArrayList<Point> locationTracker;
	int currIndex;

	public LabGame() throws ParserConfigurationException {
		super("Lab Test Game", 1000, 550);

		SoundManager.playMusic(bgm);

		megaman = new AnimatedSprite("megaman", "sprites.png", "resources/sprites.xml");
		megaman.setPhysics(true);
		megaman.stop();

		ghost = new AnimatedSprite("ghost", "sprites.png", "resources/sprites.xml");
		ghost.stop();
		ghost.setAlpha(0.5f);
		ghost.setVisible(false);

		wall = new Sprite("wall", "wall.png");
		wall.setxPos(100);
		wall.setyPos(100);

		megaman.addCollidableObject(wall);

		spriteList = new ArrayList<>();
		spriteList.add(megaman);
		spriteList.add(ghost);
		spriteList.add(wall);

		this.locationTracker = new ArrayList<Point>();
		this.currIndex = 0;
	}

	@Override
	public void update(ArrayList<String> pressedKeys) {

		super.update(pressedKeys);
		if (spriteList != null) {
			for (Sprite aSprite : spriteList) {
				aSprite.update(pressedKeys);
			}
		}

		if (pressedKeys.contains("K"))
			megaman.walk();

		if (pressedKeys.contains("J"))
			megaman.jump();

		if (pressedKeys.contains("A")) {
			megaman.setDx(megaman.getDx() - DELTA);
			if (megaman.collidesWith(wall)) {
				megaman.setDx(megaman.getDx() + DELTA);
			}
		}
		if (pressedKeys.contains("D")) {
			megaman.setDx(megaman.getDx() + DELTA);
			if (megaman.collidesWith(wall)) {
				megaman.setDx(megaman.getDx() - DELTA);
			}
		}
		if (pressedKeys.contains("W")) {
			megaman.setDy(megaman.getDy() - DELTA);
			if (megaman.collidesWith(wall)) {
				megaman.setDy(megaman.getDx() + DELTA);
			}
		}
		if (pressedKeys.contains("S")) {
			megaman.setDy(megaman.getDy() + DELTA);
			if (megaman.collidesWith(wall)) {
				megaman.setDy(megaman.getDy() - DELTA);
			}
		}

		if (pressedKeys.contains("N")) {
			if (currIndex < 300) {
				locationTracker.add(new Point(megaman.getxPos(), megaman.getyPos()));
				currIndex++;
			}
		}
		if (pressedKeys.contains("M")) {
			if (!spriteList.get(1).isVisible()) {
				spriteList.get(1).setVisible(true);
			}
			if (currIndex >= locationTracker.size()) {
				currIndex = 0;
			}

			spriteList.get(1).setxPos(locationTracker.get(currIndex).x);
			spriteList.get(1).setyPos(locationTracker.get(currIndex).y);
			currIndex++;

		}

	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);

		if (spriteList != null) {
			for (Sprite sprite : spriteList) {
				if (sprite != null) {
					sprite.draw(g);
				}
			}
		}

	}

	public static void main(String[] args) throws ParserConfigurationException {
		LabGame game = new LabGame();
		game.start();
	}
}
