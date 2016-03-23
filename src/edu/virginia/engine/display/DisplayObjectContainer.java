package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class DisplayObjectContainer extends DisplayObject {

	// Only one new field for container
	private ArrayList<DisplayObject> children;

	// 3 Constructors. mimics DisplayObject constructors, but also initializes
	// child arrayList
	public DisplayObjectContainer(String id) {
		super(id);
		this.children = new ArrayList<DisplayObject>();
	}

	public DisplayObjectContainer(String id, String imageFileName) {
		super(id, imageFileName);
		this.children = new ArrayList<DisplayObject>();
	}

	public DisplayObjectContainer(String id, String imageFileName, int xPos, int yPos) {
		super(id, imageFileName, xPos, yPos);
		this.children = new ArrayList<DisplayObject>();
	}

	// A bunch of methods for altering the contents of the child arrayList
	public void addChild(DisplayObject child) {
		this.children.add(child);
		child.setParent(this);
	}

	public void addChildAtIndex(int index, DisplayObject child) {
		this.children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(DisplayObject child) {
		this.children.remove(child);
	}

	public void removeChildByIndex(int index) {
		this.children.remove(index);
	}

	public void removeAll() {
		this.children.clear();
	}

	// Methods getting information about the contents of the child arrayList
	public boolean contains(DisplayObject child) {
		return this.children.contains(child);
	}

	public DisplayObject get(int index) {
		return this.children.get(index);
	}

	public DisplayObject get(String id) {
		for (DisplayObject child : this.children) {
			if (child.getId().equals(id)) {
				return child;
			}
		}
		return null;
	}

	public ArrayList<DisplayObject> getChildren() {
		return this.children;
	}

	// Draw method overriden to draw children as well. Children are drawn last,
	// and thus appear in front.
	public void draw(Graphics g) {
		if (this.isVisible()) {
			super.draw(g);
			if (!this.children.isEmpty()) {
				Graphics2D g2d = (Graphics2D) g;
				super.applyTransformations(g2d);
				for (DisplayObject child : this.children) {
					child.setAlpha(child.getParent().getAlpha());
					child.draw(g);
				}
				super.reverseTransformations(g2d);
			}
		}
	}

}
