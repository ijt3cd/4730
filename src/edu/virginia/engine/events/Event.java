package edu.virginia.engine.events;

public class Event {
	private String eventType;
	private IEventDispatcher source;

	public Event(String type, IEventDispatcher src) {
		this.setEventType(type);
		this.setSource(src);
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
}
