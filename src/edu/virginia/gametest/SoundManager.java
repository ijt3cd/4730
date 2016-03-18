package edu.virginia.gametest;



import java.io.File;

import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.AudioFileReader;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventListener;

public class SoundManager implements IEventListener{

	
	
	
	
	static void playSound(File sound){
		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			clip.start();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			
		}
	}
	
	static void playMusic(File sound){
		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			
			clip.start();
			clip.loop(clip.LOOP_CONTINUOUSLY);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			
		}
	}
		@Override
		public void handleEvent(Event event) {
			if(event.getEventType()=="COIN_PICKED_UP"){
	
				File noise = new File("resources/thunk.wav");
				playSound(noise);
				
				
			}
		
			
		}
	
}
