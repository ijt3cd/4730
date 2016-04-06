package edu.virginia.engine.events;

public class LevelCompleteEvent extends Event {

	public static final String LEVEL_COMPLETE = "Level Complete";
	
	public LevelCompleteEvent(String type, IEventDispatcher src) {
		super(type, src);
	}

}
