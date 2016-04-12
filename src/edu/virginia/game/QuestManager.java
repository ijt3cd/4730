package edu.virginia.game;

import edu.virginia.engine.display.Tween;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventListener;
import edu.virginia.engine.events.TweenEvent;

public class QuestManager implements IEventListener{
	
	public QuestManager(){
		
	}

	@Override
	public void handleEvent(Event event) {
		if(event.getEventType().equals(TweenEvent.TWEEN_COMPLETE_EVENT)){
			TweenEvent completedTween = (TweenEvent)event;
			Tween t = completedTween.getCompleted();
			t.getTweenee().setHasPhysics(true);
		}
	}

}
