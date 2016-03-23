package edu.virginia.engine.display;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.EventDispatcher;

public class DisplayObject extends EventDispatcher {

	// All the fields
	private String id;
	private int xPos;
	private int yPos;
	private int xPivot;
	private int yPivot;
	private double scaleX;
	private double scaleY;
	private double rotation;
	private float alpha;
	private float dx;
	private float dy;
	private boolean visible;
	private boolean physics;
	private boolean tweening;
	private Rectangle hitbox;
	private DisplayObject parent;
	private BufferedImage displayImage;
	private List<DisplayObject> collidableObjects;

	// Three constructors. Id | Id and imageFile | Id, imageFile and coordinates
	public DisplayObject(String id) {
		this.id = id;
		this.xPos = 0;
		this.yPos = 0;
		this.xPivot = 0;
		this.yPivot = 0;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
		this.rotation = 0.0;
		this.alpha = 1.0f;
		this.dx = 0;
		this.dy = 0;
		this.visible = true;
		this.physics = false;
		this.tweening = false;
		this.hitbox = new Rectangle(0, 0);
		this.parent = null;
		this.displayImage = null;
		collidableObjects = new ArrayList<>();
	}

	public DisplayObject(String id, String imageFileName) {
		this.setDisplayImage(imageFileName);
		this.id = id;
		this.xPos = 0;
		this.yPos = 0;
		this.xPivot = this.getUnscaledWidth() / 2;
		this.yPivot = this.getUnscaledHeight() / 2;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
		this.rotation = 0.0;
		this.alpha = 1.0f;
		this.dx = 0;
		this.dy = 0;
		this.visible = true;
		this.physics = false;
		this.tweening = false;
		this.hitbox = new Rectangle(0, 0);
		this.parent = null;
		collidableObjects = new ArrayList<>();
	}

	public DisplayObject(String id, String imageFileName, int xPos, int yPos) {
		this.setDisplayImage(imageFileName);
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.xPivot = this.getUnscaledWidth() / 2;
		this.yPivot = this.getUnscaledHeight() / 2;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
		this.rotation = 0.0;
		this.alpha = 1.0f;
		this.dx = 0;
		this.dy = 0;
		this.visible = true;
		this.physics = false;
		this.tweening = false;
		this.hitbox = new Rectangle(xPos, yPos);
		this.parent = null;
		collidableObjects = new ArrayList<>();
	}

	// Getters and setters for every field
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getxPos() {
		return this.xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return this.yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getxPivot() {
		return this.xPivot;
	}

	public void setxPivot(int xPivot) {
		this.xPivot = xPivot;
	}

	public int getyPivot() {
		return this.yPivot;
	}

	public void setyPivot(int yPivot) {
		this.yPivot = yPivot;
	}

	public double getScaleX() {
		return this.scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return this.scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public double getRotation() {
		return this.rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public float getAlpha() {
		return this.alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getDx() {
		return this.dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return this.dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isPhysics() {
		return this.physics;
	}

	public void setPhysics(boolean physics) {
		this.physics = physics;
	}

	public boolean isTweening() {
		return this.tweening;
	}

	public void setTweening(boolean tweening) {
		this.tweening = tweening;
	}

	public Rectangle getHitbox() {
		return this.hitbox;
	}

	public void setHitbox(Rectangle hitbox) {
		this.hitbox = hitbox;
	}

	public DisplayObject getParent() {
		return this.parent;
	}

	public void setParent(DisplayObject parent) {
		this.parent = parent;
	}

	public int getUnscaledWidth() {
		if (this.displayImage == null)
			return 0;
		return this.displayImage.getWidth();
	}

	public int getUnscaledHeight() {
		if (this.displayImage == null)
			return 0;
		return this.displayImage.getHeight();
	}

	public boolean collidesWith(DisplayObject obj) {
		Rectangle globalRectangle1 = this.getHitbox();
		Rectangle globalRectangle2 = obj.getHitbox();
		return globalRectangle1.intersects(globalRectangle2);
	}

	public void addCollidableObject(DisplayObject obj) {
		collidableObjects.add(obj);
	}

	public BufferedImage getDisplayImage() {
		return this.displayImage;
	}

	protected void setDisplayImage(String imageName) {
		if (imageName == null) {
			return;
		}
		this.displayImage = readImage(imageName);
		if (this.displayImage == null) {
			System.err.println("[DisplayObject.setImage] ERROR: " + imageName + " does not exist!");
		}
		this.xPivot = this.getUnscaledWidth() / 2;
		this.yPivot = this.getUnscaledHeight() / 2;
	}

	public BufferedImage readImage(String imageName) {
		BufferedImage image = null;
		try {
			String file = ("resources" + File.separator + imageName);
			image = ImageIO.read(new File(file));
		} catch (IOException e) {
			System.out.println("[Error in DisplayObject.java:readImage] Could not read image " + imageName);
			e.printStackTrace();
		}
		return image;
	}

	public void setDisplayImage(BufferedImage image) {
		if (image == null)
			return;
		this.displayImage = image;
		this.xPivot = this.getUnscaledWidth() / 2;
		this.yPivot = this.getUnscaledHeight() / 2;
	}

	// sets hitbox to match sprite location
	public void resetHitbox() {
		this.hitbox.setLocation(this.xPos, this.yPos);
		this.hitbox.setBounds(this.xPos, this.yPos, this.getUnscaledWidth(), this.getUnscaledHeight());
	}

	// updates hitbox location.
	protected void update(ArrayList<String> pressedKeys) {
		if (this.hitbox != null) {
			this.resetHitbox();
		}
		for (DisplayObject each : collidableObjects) {
			if (this.collidesWith(each)) {
				dispatchEvent(new Event(Event.COLLIDE, this));
			}
		}
	}

	// applies translations, rotations, scaling, transparency and visibility to
	// image
	public void draw(Graphics g) {
		if (this.displayImage != null) {
			Graphics2D g2d = (Graphics2D) g;
			if (this.isVisible()) {
				applyTransformations(g2d);
				g2d.drawImage(this.displayImage, 0, 0, (int) (getUnscaledWidth()), (int) (getUnscaledHeight()), null);
				reverseTransformations(g2d);
			}
		}
	}

	protected void applyTransformations(Graphics2D g2d) {
		g2d.translate(this.xPos, this.yPos);
		g2d.rotate(this.rotation, this.xPivot * this.scaleX, this.yPivot * this.scaleY);
		g2d.scale(this.scaleX, this.scaleY);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));
	}

	protected void reverseTransformations(Graphics2D g2d) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.scale(1 / this.scaleX, 1 / this.scaleY);
		g2d.rotate(-this.rotation, this.xPivot * this.scaleX, this.yPivot * this.scaleY);
		g2d.translate(-this.xPos, -this.yPos);
	}

}