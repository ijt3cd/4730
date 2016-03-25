package edu.virginia.lab1test;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventDispatcher;

public class PickedUpEvent extends Event{
	public static final String COIN_PICKED_UP = "Coin picked up";
	public PickedUpEvent(String type, IEventDispatcher src) {
		super(type, src);
		// TODO Auto-generated constructor stub
	}
	

}
