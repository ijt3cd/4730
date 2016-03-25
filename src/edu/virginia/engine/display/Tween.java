package edu.virginia.engine.display;

import java.util.ArrayList;

public class Tween {
	private DisplayObject tweenee;
	private TweenTransition transition;
	private ArrayList<TweenParam> params;
	public Tween(DisplayObject object){
		tweenee = object;
		params = new ArrayList<TweenParam>();
		
	}
	public Tween(DisplayObject object, TweenTransition transition){
		tweenee = object;
		this.transition = transition;
		params = new ArrayList<TweenParam>();
	}
	public void animate(TweenParam param){
		if(param.isFinished()){
			return;
		}
		double value = (double)((float)(System.currentTimeMillis()-param.getStartTime())/(float)param.getTweenTime());
		if(transition != null){
			value = transition.applyTransition(value);
		}
		double change = param.getEndVal() - param.getStartVal();
		value = param.getStartVal() + (value * change);
		setValue(param.getParamToTween(), value);
	}
	public void update(){
		for(TweenParam param : params){
			animate(param);
		}
	}
	public DisplayObject getTweenee(){
		return this.tweenee;
	}
	public boolean isComplete(){
		for(TweenParam param : params){
			if(!param.isFinished()){
				return false;
			}
			else{
				setValue(param.getParamToTween(), param.getEndVal());
			}
		}
		return true;
	}
	public ArrayList<TweenParam> getParams(){
		return params;
	}
	public void addParam(TweenParam param){
		this.params.add(param);
	}
	public void setValue(TweenableParams param, double value){
		switch(param){
		case X : tweenee.setxPos((int)value);
				 tweenee.setPositionX((int)value);
				 break;
		case Y : tweenee.setyPos((int)value);
		 		 tweenee.setPositionY((int)value);
		         break;
		case SCALE_X : tweenee.setScaleX(value);
					   break;
		case SCALE_Y : tweenee.setScaleY(value);
					   break;
		case VELOCITY_X : tweenee.setVelocityX((float)value);
		                  break;
		case VELOCITY_Y : tweenee.setVelocityY((float)value);
						  break;
		case ALPHA : tweenee.setAlpha((float)value);
					 break;
		case ROTATION : tweenee.setRotation(value);
						break;
		case VISIBLE : if(value == 1) tweenee.setVisible(true);
						if(value == 0) tweenee.setVisible(false);
		}
	}
}
