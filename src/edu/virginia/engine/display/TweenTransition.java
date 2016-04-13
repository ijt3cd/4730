package edu.virginia.engine.display;

public class TweenTransition {
	private String transitionType;
	public static final String QUADRATIC = "quad";

	public TweenTransition(String type) {
		transitionType = type;
	}

	public double applyTransition(double percentDone) {
		if (transitionType.equals(QUADRATIC)) {
			percentDone *= percentDone;
		}
		return percentDone;
	}
}
