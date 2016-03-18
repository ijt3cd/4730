package edu.virginia.engine.events;

import edu.virginia.engine.display.Tween;

public class TweenEvent extends Event{

	private Tween tween;
	
	public final static String e1 = "TWEEN_COMPLETE_EVENT";

	
	
	public TweenEvent(String eventType, Tween tween){
		this.tween = tween;
	}
	
	public Tween getTween(){
		return this.tween;
	}
	
}
