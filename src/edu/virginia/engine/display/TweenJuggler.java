package edu.virginia.engine.display;

import java.util.ArrayList;

public class TweenJuggler {

	private ArrayList<Tween> tweenList;
	
	
	public static void add(Tween tween){
		instance.tweenList.add(tween);
	}
	
	public static void nextFrame(){
		for(Tween tween: instance.tweenList){
			tween.update();
			
		}
	}
	
	
	private static TweenJuggler instance = new TweenJuggler( );
	
	public TweenJuggler(){
		if(instance != null) System.out.println("ERROR: Cannot re-initialize singleton class!");
		this.tweenList = new ArrayList<Tween>();
		instance = this;
	}
	public static TweenJuggler getInstance(){
		return instance;
	}
	
	
}
