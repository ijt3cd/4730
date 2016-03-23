package edu.virginia.engine.events;

public class Event {

	public static final String COLLIDE = "collision";
	private String eventType;
	private IEventDispatcher source; // the object that created this event with
										// the new keyword.

	public Event(String eventType, IEventDispatcher source) {
		this.eventType = eventType;
		this.source = source;
	}

	public Event() {
	}

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

	// If your event needs more information (like questId, or timestamp, then
	// you would extend this class
	// and add the additional information to that new event type.

}
