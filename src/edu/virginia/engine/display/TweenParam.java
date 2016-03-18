package edu.virginia.engine.display;

public class TweenParam {

	
	private TweenableParams paramToTween;
	private double startVal;
	private double endVal;
	private double time;
	private double delay;
	
	
	public TweenParam(TweenableParams paramToTween, double startVal, double endVal, double time, double delay){
		this.paramToTween = paramToTween;
		this.startVal = startVal;
		this.endVal = endVal;
		this.time = time;
		this.delay = delay;
	}
	
	public TweenableParams getParam(){
		return this.paramToTween;
	}
	
	public double getStartVal(){
		return this.startVal;
	}
	
	public double getEndVal(){
		return this.endVal;
	}
	
	public double getTime(){
		return this.time;
	}
	
	public double getDelay(){
		return this.delay;
	}
	
}
