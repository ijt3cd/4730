package edu.virginia.engine.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {
	private HashMap<String, String> sounds;

	public SoundManager() {
		sounds = new HashMap<String, String>();
	}

	public void loadSoundEffect(String id, String filename) {
		sounds.put(id, filename);
	}

	public void playSoundEffect(String id) throws UnsupportedAudioFileException, IOException {
		if (sounds.containsKey(id)) {
			String filename = sounds.get(id);
			InputStream in = new FileInputStream("resources" + File.separator + filename);
			// AudioStream as = new AudioStream(in);
			// AudioPlayer.player.start(as);
		}
	}

	public void loadMusic(String id, String filename) {
		sounds.put(id, filename);
	}

	public void playMusic(String id) throws IOException {
		if (sounds.containsKey(id)) {
			String filename = sounds.get(id);
			InputStream in = new FileInputStream("resources" + File.separator + filename);
			// AudioStream as = new AudioStream(in);
			// AudioData audiodata = as.getData();
			// InputStream loop = new ContinuousAudioDataStream(audiodata);
			// AudioPlayer.player.start(loop);
		}
	}
}
