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

	
	QuestManager qm = new QuestManager();
	SoundManager sm = new SoundManager();
	File bgm = new File("resources/brm.wav");
	Event PickedUpEvent = new Event();
//	AnimatedSprite startWall = new AnimatedSprite("startWall", "wall.png", "l");
//	ArrayList<AnimatedSprite> walls = new ArrayList<>();
	ArrayList<AnimatedSprite> spriteList;
	
	
	ArrayList<Point> locationTracker;
	int currIndex;

	
	public LabGame() throws ParserConfigurationException {
		super("Lab Test Game", 1000, 550);
		
		SoundManager.playMusic(bgm);
		
		AnimatedSprite megaman = new AnimatedSprite("megaman", "sprites.png", "resources/sprites.xml");
		megaman.setPhysics(true);
		megaman.stop();
		
		AnimatedSprite ghost = new AnimatedSprite("ghost", "sprites.png", "resources/sprites.xml");
		ghost.stop();
		ghost.setAlpha(0.5f);
		ghost.setVisible(false);
		
		
		
		spriteList = new ArrayList<AnimatedSprite>();
		spriteList.add(megaman);
		spriteList.add(ghost);
		
		
		
		this.locationTracker = new ArrayList<Point>();
		this.currIndex = 0;
	}

	@Override
	public void update(ArrayList<String> pressedKeys) {

		super.update(pressedKeys);
		if(spriteList!=null){
			for(AnimatedSprite aSprite: spriteList){
				aSprite.update(pressedKeys);
			}
		} 
		if(pressedKeys.contains("K"))
			spriteList.get(0).walk();
		
		if(pressedKeys.contains("J"))
			spriteList.get(0).jump();
		
		if(pressedKeys.contains("A")){
			spriteList.get(0).setDx(spriteList.get(0).getDx()-0.2f);
		}
		if(pressedKeys.contains("D")){
			spriteList.get(0).setDx(spriteList.get(0).getDx()+0.2f);
		}
		if(pressedKeys.contains("W")){
			spriteList.get(0).setDy(spriteList.get(0).getDy()-0.2f);
		}
		if(pressedKeys.contains("S")){
			spriteList.get(0).setDy(spriteList.get(0).getDy()+0.2f);
		}
		
		
		
		
		if(pressedKeys.contains("N")){
			if(currIndex<300){
				locationTracker.add(new Point(spriteList.get(0).getxPos(),spriteList.get(0).getyPos()));
				currIndex++;
			}
		}
		if(pressedKeys.contains("M")){
			if(!spriteList.get(1).isVisible()){
				spriteList.get(1).setVisible(true);
			}
			if(currIndex>=locationTracker.size()){
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

		if(spriteList != null){
			for(Sprite sprite : spriteList){
				if(sprite != null){
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
