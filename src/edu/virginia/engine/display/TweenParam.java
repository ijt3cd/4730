package edu.virginia.engine.display;

public class TweenParam {
	private TweenableParams paramToTween;
	private double startVal;
	private double endVal;
	private long time;
	private long startTime;
	
	public TweenParam(TweenableParams paramToTween, double startVal, double endVal, long time){
		this.setParamToTween(paramToTween);
		this.startVal = startVal;
		this.endVal = endVal;
		this.time = time;
		this.setStartTime(System.currentTimeMillis());
	}
	
	public double getStartVal(){
		return startVal;
	}
	
	public double getEndVal(){
		return endVal;
	}
	
	public long getTweenTime(){
		return time;
	}

	public TweenableParams getParamToTween() {
		return paramToTween;
	}

	public void setParamToTween(TweenableParams paramToTween) {
		this.paramToTween = paramToTween;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public boolean isFinished() {
		return (((System.currentTimeMillis()-startTime)) > time);
	}

}
