package edu.virginia.engine.events;

import edu.virginia.engine.display.Tween;

public class TweenEvent extends Event{
	public final static String TWEEN_COMPLETE_EVENT = "Tween complete";
	private Tween completed;
	public TweenEvent(String type, IEventDispatcher src, Tween completed) {
		super(type, src);
		this.completed = completed;
		// TODO Auto-generated constructor stub
	}
	public Tween getCompleted() {
		return completed;
	}
	public void setCompleted(Tween completed) {
		this.completed = completed;
	}
	

}
