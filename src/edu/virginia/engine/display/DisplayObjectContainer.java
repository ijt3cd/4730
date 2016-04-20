package edu.virginia.engine.display;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class DisplayObjectContainer extends DisplayObject {

	private ArrayList<DisplayObject> children;

	public DisplayObjectContainer(String id) {
		super(id);
		setChildren(new ArrayList<DisplayObject>());
	}

	public DisplayObjectContainer(String id, String imageFileName) {
		super(id, imageFileName);
		setChildren(new ArrayList<DisplayObject>());
	}

	public ArrayList<DisplayObject> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<DisplayObject> children) {
		this.children = children;
	}

	public boolean contains(DisplayObject child) {
		return children.contains(child);
	}

	public DisplayObject findByID(String id) {
		int itr = children.size();
		for (int i = 0; i < itr; i++) {
			if (children.get(i).getId().equals(id))
				return children.get(i);
		}
		return null;
	}

	public void addChild(DisplayObject child) {
		if (!this.contains(child)) {
			this.children.add(child);
			child.setParent(this);
		}
	}

	public void addChildAtIndex(int index, DisplayObject child) {
		this.children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(DisplayObject child) {
		this.children.remove(child);
		child.setParent(null);
	}

	public void removeByIndex(int index) {
		DisplayObject child = this.children.remove(index);
		child.setParent(null);
	}

	public void removeAll() {
		while (!this.children.isEmpty()) {
			DisplayObject child = this.children.remove(0);
			child.setParent(null);
		}

	}

	@Override
	public void update(ArrayList<String> pressedKeys) {
		super.update(pressedKeys);
//		for (int i = 0; i < children.size(); i++) {
//			children.get(i).setAlpha(this.getAlpha());
//			children.get(i).setScaleX(this.getScaleX());
//			children.get(i).setScaleY(this.getScaleY());
//	
//		}
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		if (this.isVisible()) {
			Graphics2D g2d = (Graphics2D) g;
			this.applyTransformations(g2d);
			for (int i = 0; i < this.children.size(); i++) {
				this.children.get(i).draw(g);
			}
			this.reverseTransformations(g2d);
		}
	}
}
