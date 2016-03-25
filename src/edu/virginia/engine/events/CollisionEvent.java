package edu.virginia.engine.events;

public class CollisionEvent extends Event{

	public CollisionEvent(String type, IEventDispatcher src) {
		super(type, src);
	}

}
