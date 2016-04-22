package edu.virginia.engine.display;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.virginia.engine.events.CollisionEvent;
import edu.virginia.engine.events.EventDispatcher;
import edu.virginia.game.LevelManager;

/**
 * A very basic display object for a java based gaming engine
 * 
 */
public class DisplayObject extends EventDispatcher {

	/* All DisplayObject have a unique id */
	private String id;

	/* The image that is displayed by this object */
	private BufferedImage displayImage;

	private boolean visible;
	private int pivotPointX;
	private int pivotPointY;
	private double scaleX;
	private double scaleY;
	private double rotation;
	private float alpha;
	private DisplayObject parent;
	private Rectangle platform;
	private boolean hasPhysics;
	private int mass;
	private float positionX;
	private float positionY;
	private float velocityX;
	private float velocityY;
	private float accelerationX;
	private float accelerationY;
	private long lastUpdate;
	private boolean isCollidable;
	private List<Rectangle> collidableObjects;

	/**
	 * Constructors: can pass in the id OR the id and image's file path and
	 * position OR the id and a buffered image and position
	 */
	public DisplayObject(String id) {
		super();
		this.setId(id);
		this.setVisible(true);
		this.setPivotPointX(0);
		this.setPivotPointY(0);
		this.setScaleX(1.0);
		this.setScaleY(1.0);
		this.setRotation(0);
		this.setAlpha(1.0f);
		this.isCollidable = true;
		this.lastUpdate = System.nanoTime();
		this.collidableObjects = new ArrayList<>();
	}

	public DisplayObject(String id, String fileName) {
		super();
		this.setId(id);
		this.setImage(fileName);
		this.setVisible(true);
		this.setPivotPointX(0);
		this.setPivotPointY(0);
		this.setScaleX(1.0);
		this.setScaleY(1.0);
		this.setRotation(0);
		this.setAlpha(1.0f);
		this.isCollidable = true;
		this.lastUpdate = System.nanoTime();
		this.collidableObjects = new ArrayList<>();
	}

	public void setPlatform(Rectangle plat) {
		this.platform = plat;
		if(plat != null){
			this.positionY = (float) (plat.y - (this.getUnscaledHeight() * this.scaleY));
			this.velocityY = 0;
			this.accelerationY = 0;
		}
	}

	public Rectangle getPlatform() {
		return this.platform;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public DisplayObject getParent() {
		return parent;
	}

	public void setParent(DisplayObject parent) {
		this.parent = parent;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getPivotPointX() {
		return pivotPointX;
	}

	public void addCollidable(Rectangle obj) {
		collidableObjects.add(obj);
	}

	public void setPivotPointX(int pivotPointX) {
		this.pivotPointX = pivotPointX;
	}

	public int getPivotPointY() {
		return pivotPointY;
	}

	public void setPivotPointY(int pivotPointY) {
		this.pivotPointY = pivotPointY;
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double d) {
		this.rotation = d;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * Returns the unscaled width and height of this display object
	 */
	public int getUnscaledWidth() {
		if (displayImage == null)
			return 0;
		return displayImage.getWidth();
	}

	public int getUnscaledHeight() {
		if (displayImage == null)
			return 0;
		return displayImage.getHeight();
	}

	public BufferedImage getDisplayImage() {
		return this.displayImage;
	}

	protected void setImage(String imageName) {
		if (imageName == null) {
			return;
		}
		displayImage = readImage(imageName);
		if (displayImage == null) {
			System.err.println("[DisplayObject.setImage] ERROR: " + imageName + " does not exist!");
		}
	}

	/**
	 * Helper function that simply reads an image from the given image name
	 * (looks in resources\\) and returns the bufferedimage for that filename
	 */
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

	public void setImage(BufferedImage image) {
		if (image == null)
			return;
		displayImage = image;
	}

	/**
	 * Invoked on every frame before drawing. Used to update this display
	 * objects state before the draw occurs. Should be overridden if necessary
	 * to update objects appropriately.
	 */
	protected void update(ArrayList<String> pressedKeys) {
		if (this.hasPhysics) {
			if (System.nanoTime() - this.lastUpdate > 500000) {
				if ((this.platform == null)) {
					this.accelerationY = (float) 0.55;
				}
				this.setPositionX((float) (this.getPositionX() + this.velocityX));
				this.setPositionY((float) (this.getPositionY() + this.velocityY));
				this.velocityY += accelerationY;
				this.checkBoundaries();
			}
		}
	}

	public boolean checkCollision(DisplayObject other) {
		return this.getHitbox().intersects(other.getHitbox());
	}
	public List<Rectangle> getCollidableObjects() {
		return collidableObjects;
	}

	public void setCollidableObjects(List<Rectangle> collidableObjects) {
		this.collidableObjects = collidableObjects;
	}

	public boolean checkCollidables(){
		for (Rectangle each : collidableObjects) {
			if (this.collidesWith(each) && (this.platform == null || (this.platform.x != each.x || this.platform.y != each.y))) {
				if (!this.collideFromBottom(each) && this.collideFromLeft(each)) {
					this.velocityX = -1.0f;
					return true;
				}
				else if(!this.collideFromBottom(each) && this.collideFromRight(each)){
					this.velocityX = 1.0f;
					return true;
				}
				this.velocityY = Math.max(0, velocityY);
				return true;
			}
		}
		return false;
	}

	private void checkBoundaries() {
		if (this.positionX < 0) {
			this.positionX = 2;
			this.velocityX = 0;
		}
		if (this.positionY < 0) {
			this.positionY = 2;
			this.velocityY = 0;
		}
		if (this.positionX > LevelManager.width - this.getUnscaledWidth() / 2) {
			this.positionX = LevelManager.width - this.getUnscaledWidth() / 2 - 2;
			this.velocityX = 0;
		}
		if (this.positionY > LevelManager.height) {
			this.positionY = LevelManager.height - 2;
			this.velocityY = 0;
		}

	}

	/**
	 * Draws this image. This should be overloaded if a display object should
	 * draw to the screen differently. This method is automatically invoked on
	 * every frame.
	 */
	public void draw(Graphics g) {

		if (displayImage != null) {

			/*
			 * Get the graphics and apply this objects transformations
			 * (rotation, etc.)
			 */
			Graphics2D g2d = (Graphics2D) g;
			applyTransformations(g2d);

			/*
			 * Actually draw the image, perform the pivot point translation here
			 */
			if (this.isVisible()) {
				g2d.drawImage(displayImage, -this.getPivotPointX(), -this.getPivotPointY(),
						(int) (getUnscaledWidth() * this.getScaleX()), (int) (getUnscaledHeight() * this.getScaleY()),
						null);
				Rectangle rect = getHitbox();

				// g2d.drawRect(0, 0, rect.width, rect.height);
			}
			/*
			 * undo the transformations so this doesn't affect other display
			 * objects
			 */
			reverseTransformations(g2d);
		}
	}

	/**
	 * Applies transformations for this display object to the given graphics
	 * object
	 */
	protected void applyTransformations(Graphics2D g2d) {
		g2d.translate(this.getPositionX(), this.getPositionY());
		g2d.rotate(this.getRotation());
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getAlpha()));
	}

	/**
	 * Reverses transformations for this display object to the given graphics
	 * object
	 */
	protected void reverseTransformations(Graphics2D g2d) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.rotate(-this.getRotation());
		g2d.translate(-this.getPositionX(), -this.getPositionY());

	}

	public float getGlobalX() {
		return this.positionX;
	}

	public float getGlobalY() {
		return this.positionY;
	}

	public Rectangle getHitbox() {
		return new Rectangle((int) this.positionX, (int) this.positionY,
				(int) (this.getUnscaledWidth() * this.getScaleX()),
				(int) (this.getUnscaledHeight() * this.getScaleY()));
	}
	public Rectangle getNextHitbox() {
		return new Rectangle((int) (this.positionX + this.velocityX), (int) (this.positionY + 2*this.velocityY),
				(int) (this.getUnscaledWidth() * this.getScaleX()),
				(int) (this.getUnscaledHeight() * this.getScaleY()));
	}
	public Rectangle getInBetweenHitbox() {
		return new Rectangle((int) (this.positionX + this.velocityX), (int) (this.positionY + 1.5*this.velocityY),
				(int) (this.getUnscaledWidth() * this.getScaleX()),
				(int) (this.getUnscaledHeight() * this.getScaleY()));
	}
	public Rectangle getReducedHitbox() {
		return new Rectangle((int) this.positionX, (int) this.positionY,
				(int) (this.getUnscaledWidth() * this.getScaleX()),
				(int) (this.getUnscaledHeight() * this.getScaleY()));
	}

	public boolean collidesWith(Rectangle other) {
		if (!isCollidable) {
			return false;
		}
		if (this.getHitbox().intersects(other)) {
			this.dispatchEvent(new CollisionEvent(CollisionEvent.COLLIDE_TYPE, this));
			return true;
		}
		return false;
	}

	public boolean collideFromBottom(Rectangle each) {
		Rectangle myRectangle = this.getHitbox();
		Rectangle otherRectangle = each;
		boolean withinRange = (myRectangle.x + 16) > otherRectangle.x && (myRectangle.getMaxX() - 16) < otherRectangle.getMaxX();
		return myRectangle.y < otherRectangle.y + otherRectangle.getHeight() && myRectangle.y > otherRectangle.y && withinRange;
	} 
	public boolean collideFromLeft(Rectangle each) {
		Rectangle myRectangle = this.getHitbox();
		Rectangle otherRectangle = each;
		return myRectangle.x + myRectangle.getWidth() > otherRectangle.x && otherRectangle.x > myRectangle.x;
	}

	public boolean collideFromRight(Rectangle each) {
		Rectangle myRectangle = this.getHitbox();
		Rectangle otherRectangle = each;
		return otherRectangle.x + otherRectangle.getWidth() > myRectangle.x && myRectangle.x > otherRectangle.x;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}
	public void clearCollidables(){
		this.collidableObjects.clear();
	}
	public float getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}

	public float getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(float velocityY) {
		this.velocityY = velocityY;
	}

	public float getAccelerationX() {
		return accelerationX;
	}

	public void setAccelerationX(float accelerationX) {
		this.accelerationX = accelerationX;
	}

	public float getAccelerationY() {
		return accelerationY;
	}

	public void setAccelerationY(float accelerationY) {
		this.accelerationY = accelerationY;
	}

	public boolean hasPhysics() {
		return hasPhysics;
	}

	public void setHasPhysics(boolean hasPhysics) {
		this.hasPhysics = hasPhysics;
	}

	public float getPositionY() {
		return positionY;
	}

	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}

	public float getPositionX() {
		return positionX;
	}

	public void setPositionX(float positionX) {
		this.positionX = positionX;
	}

	public boolean isCollidable() {
		return isCollidable;
	}

	public void setCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}
}