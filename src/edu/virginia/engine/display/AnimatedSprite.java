package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import edu.virginia.engine.util.GameClock;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import java.io.*;

public class AnimatedSprite extends Sprite {

	private BufferedImage imageSheet;
	private boolean playing;
	private double currentFrame;
	private int startFrame;
	private int endFrame;
	private double animationSpeed;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;
	private File sheetXML;
	private float gravity;

	public File getSheetXML() {
		return sheetXML;
	}

	public void setSheetXML(File sheetXML) {
		this.sheetXML = sheetXML;
	}

	public BufferedImage getImageSheet() {
		return imageSheet;
	}

	public void setImageSheet(BufferedImage imageSheet) {
		this.imageSheet = imageSheet;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public double getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(double currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getStartFrame() {
		return startFrame;
	}

	public void setStartFrame(int startFrame) {
		this.startFrame = startFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	public double getAnimationSpeed() {
		return animationSpeed;
	}

	public void setAnimationSpeed(double animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

	public AnimatedSprite(String id, String sheetFileName, String xmlPath) throws ParserConfigurationException {
		super(id, sheetFileName);
		this.sheetXML = new File(xmlPath);
		this.factory = DocumentBuilderFactory.newInstance();
		this.builder = factory.newDocumentBuilder();
		try {
			this.document = this.builder.parse(this.sheetXML);
			this.document.getDocumentElement().normalize();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.playing = true;
		this.currentFrame = 0;
		this.startFrame = 0;
		this.endFrame = 0;
		this.animationSpeed = .05;
		this.imageSheet = super.getDisplayImage();
		this.gravity = 0;
	}

	public AnimatedSprite(String id) throws ParserConfigurationException {
		super(id);
		this.sheetXML = new File("resources/sprites.xml");
		this.factory = DocumentBuilderFactory.newInstance();
		this.builder = factory.newDocumentBuilder();
		try {
			this.document = this.builder.parse(this.sheetXML);
			this.document.getDocumentElement().normalize();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.playing = true;
		this.currentFrame = 0;
		this.startFrame = 0;
		this.endFrame = 0;
		this.animationSpeed = .05;
		this.gravity = 0;
	}

	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
		if (super.isPhysics()) {
			// position updates based on speed
			this.setxPos(this.getxPos() + (int) this.getDx());
			this.setyPos(this.getyPos() + (int) this.getDy());
			// gravity
			super.setDy(this.getDy() + this.gravity);
			// friction
			super.setDx(this.getDx() * 0.95f);
			for(DisplayObject each : collidableObjects) {
				if(this.collidesWith(each)) {
					this.setxPos(this.getxPos() - (int) this.getDx());
					this.setyPos(this.getyPos() - (int) this.getDy());
					break;
				}
			}
			// maximum horizontal speeds
			if (super.getDx() > 4.0f) {
				super.setDx(4.0f);
			}
			if (super.getDx() < -4.0f) {
				super.setDx(-4.0f);
			}
		}
	}

	public void draw(Graphics g) {
		super.draw(g);
	}

	public void walk() {
		NodeList n = this.document.getElementsByTagName("Run");
		this.startFrame = 0;
		this.endFrame = ((Element) n.item(0)).getElementsByTagName("SubTexture").getLength();
		this.currentFrame = (((this.currentFrame + this.animationSpeed) % this.endFrame));

		Element e2 = (Element) ((Element) n.item(0)).getElementsByTagName("SubTexture").item((int) this.currentFrame);
		super.setDisplayImage(this.getImageSheet().getSubimage(Integer.parseInt(e2.getAttribute("x")),
				Integer.parseInt(e2.getAttribute("y")), Integer.parseInt(e2.getAttribute("width")),
				Integer.parseInt(e2.getAttribute("height"))));

	}

	public void stop() {
		NodeList n = this.document.getElementsByTagName("Stop");
		this.startFrame = 0;
		this.endFrame = ((Element) n.item(0)).getElementsByTagName("SubTexture").getLength();
		this.currentFrame = (this.currentFrame + 1) % this.endFrame;

		Element e2 = (Element) ((Element) n.item(0)).getElementsByTagName("SubTexture").item((int) this.currentFrame);
		super.setDisplayImage(this.getImageSheet().getSubimage(Integer.parseInt(e2.getAttribute("x")),
				Integer.parseInt(e2.getAttribute("y")), Integer.parseInt(e2.getAttribute("width")),
				Integer.parseInt(e2.getAttribute("height"))));

	}

	public void jump() {
		NodeList n = this.document.getElementsByTagName("Jump");
		this.startFrame = 0;
		this.endFrame = ((Element) n.item(0)).getElementsByTagName("SubTexture").getLength();
		this.currentFrame = (this.currentFrame + 1) % this.endFrame;

		Element e2 = (Element) ((Element) n.item(0)).getElementsByTagName("SubTexture").item((int) this.currentFrame);
		super.setDisplayImage(this.getImageSheet().getSubimage(Integer.parseInt(e2.getAttribute("x")),
				Integer.parseInt(e2.getAttribute("y")), Integer.parseInt(e2.getAttribute("width")),
				Integer.parseInt(e2.getAttribute("height"))));

	}

}
