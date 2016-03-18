package edu.virginia.engine.display;

import java.util.ArrayList;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.EventDispatcher;
import edu.virginia.engine.events.IEventListener;

public class Tween extends EventDispatcher implements IEventListener{

	
	private DisplayObject object;
	private TweenTransitions transition;
	public boolean isTweening;
	private double startTime;
	private ArrayList<TweenParam> fieldsToAnimate;
	private Event PickedUpEvent;
	private boolean completed;
	
	public Tween(DisplayObject object){
		this.object = object;
		this.isTweening = false;
		this.fieldsToAnimate = new ArrayList<TweenParam>();
		this.PickedUpEvent = new Event();
		this.PickedUpEvent.setEventType("COIN_SHRINK");
		this.completed = false;
		
		
	}
	
	public Tween(DisplayObject object, TweenTransitions transitions){
		this.object = object;
		this.transition = transitions;
		this.isTweening = false;
		this.fieldsToAnimate = new ArrayList<TweenParam>();
		this.PickedUpEvent = new Event();
		this.PickedUpEvent.setEventType("COIN_SHRINK");
		this.completed = false;
		
	}
	
	public void animate(TweenableParams fieldToAnimate, double startVal, double endVal, double time){
		this.fieldsToAnimate.add(new TweenParam(fieldToAnimate,startVal,endVal,time,0));
	}
	
	public void update(){
		if (!this.isTweening){
			this.startTime = System.currentTimeMillis();
			this.isTweening = true;
		}
		else{

			for(TweenParam tweenParam  : this.fieldsToAnimate){
				if(    (!(((System.currentTimeMillis()-this.startTime)/1000) > (tweenParam.getTime())+tweenParam.getDelay())) && (((System.currentTimeMillis()-this.startTime)/1000)>tweenParam.getDelay())   ){
				this.setValue(tweenParam.getParam(), tweenParam.getStartVal()+((System.currentTimeMillis()-this.startTime)/1000-tweenParam.getDelay())/tweenParam.getTime()*(tweenParam.getEndVal()-tweenParam.getStartVal()));
				
					
				
				}
				else{
					
				}
			}
			
		}
	}
	
	public boolean isComplete(){
		return false;
	}
	
	public void setValue(TweenableParams param, double value){
		        
	        switch (param) {
	            case POSX:  this.object.setxPos((int)value);
	                     break;
	            case POSY:  this.object.setyPos((int)value);
	                     break;
	            case SCALEX:  this.object.setScaleX(value);;
	                     break;
	            case SCALEY:  this.object.setScaleY(value);;
	                     break;
	            case ROTATION:  this.object.setRotation(value);;
	                     break;
	            case ALPHA:  this.object.setAlpha((float)value);
	                     break;

	        }
	
	}

	@Override
	public void handleEvent(Event event) {
		if(event.getEventType()=="COIN_SHRINK"){
			this.animate(TweenableParams.ALPHA, 1, 0, 2);
		}
		
	}

	public void animate(TweenableParams fieldToAnimate, double startVal, double endVal, double time, double delay){
		this.fieldsToAnimate.add(new TweenParam(fieldToAnimate,startVal,endVal,time,delay));
	}
	
	
}