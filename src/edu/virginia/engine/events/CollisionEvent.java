package edu.virginia.engine.events;

public class CollisionEvent extends Event {

	public static final String COLLIDE_TYPE = "collided";

	public CollisionEvent(String type, IEventDispatcher src) {
		super(type, src);
	}

}
