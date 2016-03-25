package edu.virginia.engine.events;

import java.util.ArrayList;
import java.util.HashMap;

public class EventDispatcher implements IEventDispatcher{
	private HashMap<String, ArrayList<IEventListener>> listeners;
	
	public EventDispatcher(){
		listeners = new HashMap<String, ArrayList<IEventListener>>();
	}
	@Override
	public void addEventListener(IEventListener listener, String eventType) {
		if(listener != null && eventType != null){
			if(listeners.get(eventType) == null){
				ArrayList<IEventListener> list = new ArrayList<IEventListener>();
				list.add(listener);
				listeners.put(eventType, list);
			}
			else if(!listeners.get(eventType).contains(listener)){
				listeners.get(eventType).add(listener);
			}
		}
	}

	@Override
	public void removeEventListener(IEventListener listener, String eventType) {
		if(listener != null && eventType != null){
			if(listeners.containsKey(eventType)){
				listeners.get(eventType).remove(listener);
			}
		}		
	}

	@Override
	public void dispatchEvent(Event event) {
		if(event != null && listeners.containsKey(event.getEventType())){
			ArrayList<IEventListener> relevantListeners = listeners.get(event.getEventType());
			for(int i = 0; i < relevantListeners.size(); i++){
				relevantListeners.get(i).handleEvent(event);
			}
		}
	}

	@Override
	public boolean hasEventListener(IEventListener listener, String eventType) {
		if(eventType != null && listener != null){
			return listeners.get(eventType).contains(listener);
		}
		return false;
	}

}
