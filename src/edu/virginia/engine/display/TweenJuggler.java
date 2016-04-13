package edu.virginia.engine.display;

import java.util.ArrayList;

import edu.virginia.engine.events.EventDispatcher;
import edu.virginia.engine.events.TweenEvent;

public class TweenJuggler extends EventDispatcher {
	private static TweenJuggler instance = null;
	private ArrayList<Tween> tweens;

	public TweenJuggler() {
		instance = this;
		instance.tweens = new ArrayList<Tween>();
	}

	public static TweenJuggler getInstance() {
		if (instance != null) {
			return instance;
		} else {
			instance = new TweenJuggler();
			return instance;
		}
	}

	public static void addTween(Tween tween) {
		for (TweenParam p : tween.getParams()) {
			p.setStartTime(System.currentTimeMillis());
		}
		instance.tweens.add(tween);
	}

	public static void nextFrame() {
		for (int i = 0; i < instance.tweens.size(); i++) {
			if (instance.tweens.get(i).isComplete()) {
				Tween completed = instance.tweens.remove(i);
				instance.dispatchEvent(new TweenEvent(TweenEvent.TWEEN_COMPLETE_EVENT, instance, completed));
			} else {
				instance.tweens.get(i).update();
			}
		}
	}
}
