package edu.virginia.engine.events;

public class Event {

	private String eventType;
	private IEventDispatcher source; //the object that created this event with the new keyword.
	
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public IEventDispatcher getSource() {
		return source;
	}
	public void setSource(IEventDispatcher source) {
		this.source = source;
	}
	
		
	
	
//	If your event needs more information (like questId, or timestamp, then you would extend this class
//	and add the additional information to that new event type.

	
	
}
