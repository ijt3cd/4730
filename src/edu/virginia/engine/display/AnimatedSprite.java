package edu.virginia.engine.display;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class AnimatedSprite extends Sprite {
	private BufferedImage spriteSheet;
	private BufferedImage currentSprite;
	private int spriteHeight;
	private int spriteWidth;
	private long animationSpeed;
	private boolean repeat;
	private boolean playing;
	private int animationRow;
	private int startIndex;
	private int currentIndex;
	private int endIndex;
	private long startTime;
	private String currentAnimation;
	private HashMap<String, int[]> animations;

	public AnimatedSprite(String id) {
		super(id);
	}

	public AnimatedSprite(String id, String imageFileName, int width, int height) {
		super(id, imageFileName);
		animations = new HashMap<String, int[]>();
		spriteSheet = this.getDisplayImage();
		spriteHeight = height;
		spriteWidth = width;
		currentSprite = spriteSheet.getSubimage(0, 0, spriteWidth, spriteHeight);
		this.setImage(currentSprite);
	}

	public BufferedImage getSpriteSheet() {
		return this.spriteSheet;
	}

	public void landOnPlatform(Rectangle platform) {
		this.setPositionY((float) (platform.y - this.getUnscaledHeight() * this.getScaleY() + 10));
		this.setVelocityY(0);
		this.setAccelerationY(0);
		this.setPlatform(platform);
	}

	public void addAnimation(String animation, int start, int end, int speed, int repeat, int row) {
		int[] arr = new int[5];
		arr[0] = start;
		arr[1] = end;
		arr[2] = speed;
		arr[3] = repeat;
		arr[4] = row;
		animations.put(animation, arr);
	}

	public boolean setAnimation(String animation) {
		if (animations.containsKey(animation)) {
			if (currentAnimation == animation) {
				return false;
			}
			currentAnimation = animation;
			return true;
		}
		return false;
	}

	public void play() {
		playing = true;
		int arr[] = animations.get(currentAnimation);
		currentIndex = startIndex = arr[0];
		endIndex = arr[1];
		animationSpeed = (long) arr[2];
		repeat = (arr[3] == 1);
		animationRow = arr[4];
		currentSprite = spriteSheet.getSubimage(0, animationRow * spriteHeight, spriteWidth, spriteHeight);
		startTime = System.nanoTime();
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public long getAnimationSpeed() {
		return animationSpeed;
	}

	public void setAnimationSpeed(int animationSpeed) {
		this.animationSpeed = animationSpeed;
	}

	@Override
	public void update(ArrayList<String> pressedKeys) {
		if (!playing) {
			this.stopAnimation();
		}
		if (System.nanoTime() - startTime > animationSpeed && playing) {
			startTime = System.nanoTime();
			currentIndex++;
			playing = false;
		}
		if (currentIndex > endIndex) {
			if (repeat) {
				currentIndex = startIndex;
			} else
				currentIndex = endIndex;
		}
		currentSprite = spriteSheet.getSubimage(currentIndex * spriteWidth, animationRow * spriteHeight, spriteWidth,
				spriteHeight);
		this.setImage(currentSprite);
		super.update(pressedKeys);
	}

	private void stopAnimation() {
		currentIndex = this.startIndex;

	}

	public boolean checkStillOnPlatform(Rectangle platform) {
		Rectangle spriteRec = this.getNextHitbox();
		boolean above = Math.abs(spriteRec.getMaxY() - platform.getY()) < 5;
		boolean checkLeft = (spriteRec.getX() - platform.getX() > -6);
		boolean checkRight = (spriteRec.getMaxX() - platform.getMaxX() < 6);
		return above && checkLeft && checkRight;
	}

	public boolean checkPlatformCollision(Rectangle platform) {
		Rectangle spriteRec = this.getNextHitbox();
		Rectangle currentRec = this.getHitbox();
		boolean intersection = spriteRec.intersects(platform) || currentRec.intersects(platform);
		boolean movingDown = this.getVelocityY() >= 0;
		boolean halfOnPlatformRight = (spriteRec.getMaxX()
				- platform.getMaxX() < ((this.getUnscaledWidth() * this.getScaleX()) / 2.0));
		boolean halfOnPlatformLeft = (platform.getX()
				- spriteRec.getX() < ((this.getUnscaledWidth() * this.getScaleX()) / 2.0));
		boolean abovePlatform = (spriteRec.getY() < platform.getY() - 10);
		boolean fullLanding = spriteRec.getMaxY() > platform.getY() + 10;
		return intersection && movingDown && halfOnPlatformRight && halfOnPlatformLeft && abovePlatform && fullLanding;
	}

}
