package edu.virginia.gametest;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventListener;

public class QuestManager implements IEventListener {

	boolean questComplete = false;
	@Override
	public void handleEvent(Event event) {
		if(event.getEventType()=="COIN_PICKED_UP"&&!questComplete){
			System.out.println("quest is complete");
			questComplete=true;
		}
		
	}
	
}
