package edu.virginia.engine.events;

import java.util.ArrayList;
import java.util.HashMap;

public class EventDispatcher implements IEventDispatcher{

	
	
	private HashMap<String,ArrayList<IEventListener> > ieMap = new HashMap<String, ArrayList<IEventListener>>() ;
	//Hashmap where key is eventType and value is list of listeners
	
	
	@Override
	public void addEventListener(IEventListener listener, String eventType) {
		if(listener!=null&&eventType!=null){			
			if (this.ieMap.get(eventType)==null){
				this.ieMap.put(eventType, new ArrayList<IEventListener>());
			}
			this.ieMap.get(eventType).add(listener);			
		}				
	}

	@Override
	public void removeEventListener(IEventListener listener, String eventType) {
		if(listener!=null&&eventType!=null){
			if (this.ieMap.get(eventType)!=null){
				this.ieMap.get(eventType).remove(listener);
			}	
		}	
	}

	@Override
	public void dispatchEvent(Event event) {
		if(event!=null){
			String eType = event.getEventType();
			if(this.ieMap.get(eType)!=null){
			for (IEventListener ie: this.ieMap.get(eType)){
				ie.handleEvent(event);
			}
			}
		}
	}

	@Override
	public boolean hasEventListener(IEventListener listener, String eventType) {
		if(listener!=null&&eventType!=null){
			for (IEventListener ie: this.ieMap.get(eventType)){
				if(ie==listener){
					return true;
				}
			}
		}
		return false;
	}
}
