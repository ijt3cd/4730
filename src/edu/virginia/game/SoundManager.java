package edu.virginia.game;

import java.io.File;

import javax.sound.sampled.spi.FormatConversionProvider;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import edu.virginia.engine.events.DeathEvent;
import edu.virginia.engine.events.Event;
import edu.virginia.engine.events.IEventListener;
import edu.virginia.engine.events.LevelCompleteEvent;
import edu.virginia.engine.events.PickedUpEvent;

public class SoundManager implements IEventListener {

	static void playSound(File sound) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(sound));
			clip.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
	}

	static void playMusic(File sound) {
		try {
			AudioInputStream soundIn = AudioSystem.getAudioInputStream(sound);
			AudioFormat format = soundIn.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip)AudioSystem.getLine(info);
			clip.open(soundIn);
			clip.start();
			
			
//			Clip clip = AudioSystem.getClip();
//			clip.open(AudioSystem.getAudioInputStream(sound));

		//	clip.start();
			clip.loop(clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
	}

	@Override
	public void handleEvent(Event event) {
		String eventType = event.getEventType();
		if (eventType.equals(LevelCompleteEvent.LEVEL_COMPLETE)) {

			File noise = new File("resources/picked_up_powerup.wav");
			playSound(noise);

		} else if(eventType.equals(DeathEvent.DEAD_EVENT)) {
			
		} else if(eventType.equals(PickedUpEvent.COIN_PICKED_UP)) {
			
		}
		

	}

}