package edu.virginia.engine.events;

import edu.virginia.engine.display.AnimatedSprite;

public class DeathEvent extends Event {
	
	public static String DEAD_EVENT = "dead";

	public DeathEvent(String type, IEventDispatcher src, AnimatedSprite dead) {
		super(type, src);
	}

}
