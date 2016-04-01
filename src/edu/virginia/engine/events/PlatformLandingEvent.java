package edu.virginia.engine.events;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.Sprite;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventDispatcher;

public class PlatformLandingEvent extends Event{
	public static final String PLATFORM_LANDED_ON = "Platform landed on";
	public static final String PLATFORM_FALLEN_OFF = "Platform fallen off";
	public PlatformLandingEvent(String type, IEventDispatcher src) {
		super(type, src);
		// TODO Auto-generated constructor stub
	}

	public PlatformLandingEvent(String type, IEventDispatcher src, AnimatedSprite lander) {
		super(type, src);
		if(type == PLATFORM_LANDED_ON)
			lander.landOnPlatform((Sprite)src);
		else if(type == PLATFORM_FALLEN_OFF){
			lander.setOnFloor(false);
			lander.setPlatform(null);
		}
	}
}
