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

	private int xPos;
	private int yPos;
	private int pivotPointX;
	private int pivotPointY;
	private double scaleX;
	private double scaleY;
	private double rotation;
	private float alpha;
	private DisplayObject parent;
	private DisplayObject platform;
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
	private List<DisplayObject> collidableObjects;

	private boolean onFloor;

	/**
	 * Constructors: can pass in the id OR the id and image's file path and
	 * position OR the id and a buffered image and position
	 */
	public DisplayObject(String id) {
		super();
		this.setId(id);
		this.setVisible(true);
		this.setxPos(0);
		this.setyPos(0);
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
		this.setxPos(0);
		this.setyPos(0);
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

	public void setPlatform(DisplayObject plat) {
		this.platform = plat;
	}

	public DisplayObject getPlatform() {
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

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public int getPivotPointX() {
		return pivotPointX;
	}

	public void setOnFloor(boolean b) {
		onFloor = b;
	}

	public boolean isOnFloor() {
		return this.onFloor;
	}

	public void addCollidable(DisplayObject obj) {
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
				if (!this.onFloor) {
					this.accelerationY = (float) 0.25;
				}
				for (DisplayObject each : collidableObjects) {
					if (this.collideFromBottom(each)) {
						this.velocityY = 0;
					}
					if (this.collideFromLeft(each)) {
						//System.out.println("collide from left");
						//System.out.println(this.getHitbox() + " " + each.getHitbox());
						this.velocityX = -1.0f;
					}
					if (this.collideFromRight(each)) {
						//System.out.println("collide from right");
						//System.out.println(this.getHitbox() + " " + each.getHitbox());
						this.velocityX = 1.0f;
					}
				}
				this.setPositionX((float) (this.getPositionX() + this.velocityX));
				this.setPositionY((float) (this.getPositionY() + this.velocityY));
				this.velocityX /= 2;
				this.velocityY += accelerationY;
				this.checkBoundaries();
				this.xPos = (int) this.positionX;
				this.yPos = (int) this.positionY;
			}
		}
	}

	public boolean checkCollision(DisplayObject other) {
		return this.getHitbox().intersects(other.getHitbox());
	}

	private void checkBoundaries() {
		if (this.positionX < 0) {
			this.positionX = 2;
			this.velocityX = (float) (-this.velocityX * .5);
		}
		if (this.positionY < 0) {
			this.positionY = 2;
			this.velocityY = (float) (-this.velocityY * .5);
		}
		/*
		 * if(this.positionY + this.getUnscaledHeight()*this.scaleY > 290){
		 * this.positionY = (float)(290 - this.getUnscaledHeight()*this.scaleY -
		 * 2); this.velocityY = 0; this.accelerationY = 0; this.onFloor = true;
		 * }
		 */
		if (this.positionX + this.getUnscaledWidth() * this.scaleX > 500) {
			this.positionX = (float) (500 - this.getUnscaledWidth() * this.scaleX - 2);
			this.velocityX = (float) (-this.velocityY * .5);
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

				g2d.drawRect(0, 0, rect.width, rect.height);
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
		g2d.translate(this.getxPos(), this.getyPos());
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
		g2d.translate(-this.getxPos(), -this.getyPos());

	}

	public int getGlobalX() {
		return this.xPos;
	}

	public int getGlobalY() {
		return this.yPos;
	}

	public Rectangle getHitbox() {
		return new Rectangle(this.getGlobalX(), this.getGlobalY(), (int) (this.getUnscaledWidth() * this.getScaleX()),
				(int) (this.getUnscaledHeight() * this.getScaleY()));
	}

	public Rectangle getReducedHitbox() {
		return new Rectangle(this.getGlobalX(), this.getGlobalY(),
				(int) (this.getUnscaledWidth() * this.getScaleX() * .75),
				(int) (this.getUnscaledHeight() * this.getScaleY() * .75));
	}

	public boolean collidesWith(DisplayObject other) {
		if (!isCollidable || !other.isCollidable) {
			return false;
		}
		if (this.getHitbox().intersects(other.getHitbox())) {
			this.dispatchEvent(new CollisionEvent(CollisionEvent.COLLIDE_TYPE, this));
			return true;
		}
		return false;
	}

	public boolean collideFromBottom(DisplayObject other) {
		if (collidesWith(other)) {
			Rectangle myRectangle = this.getHitbox();
			Rectangle otherRectangle = other.getHitbox();
			return myRectangle.y < otherRectangle.y + otherRectangle.getHeight() && myRectangle.y > otherRectangle.y;
		} else {
			return false;
		}
	}

	public boolean collideFromLeft(DisplayObject other) {
		if (collidesWith(other)) {
			Rectangle myRectangle = this.getHitbox();
			Rectangle otherRectangle = other.getHitbox();
			return myRectangle.x + myRectangle.getWidth() > otherRectangle.x && otherRectangle.x > myRectangle.x;
		} else {
			return false;
		}
	}

	public boolean collideFromRight(DisplayObject other) {
		if (collidesWith(other)) {
			Rectangle myRectangle = this.getHitbox();
			Rectangle otherRectangle = other.getHitbox();
			return otherRectangle.x + otherRectangle.getWidth() > myRectangle.x && myRectangle.x > otherRectangle.x;
		} else {
			return false;
		}
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
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