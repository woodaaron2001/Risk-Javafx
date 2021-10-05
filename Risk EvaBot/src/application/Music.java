package application;
//
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Music {
	Clip clip;
	int volume;
	AudioInputStream audioInputStream;
	URL Sound;
	public Music(int x) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		this.volume =x;
		switch(x) {
		case 0:
			Sound = this.getClass().getResource("/battle_of_lepanto.wav");
			break;
		
		case 1:
			Sound = this.getClass().getResource("/army.wav");
			break;
	
		case 2:
			Sound = this.getClass().getResource("/battle.wav");
			break;
		
		case 3:
			Sound = this.getClass().getResource("/cardOpen.wav");
			break;
		
		case 4:
			Sound = this.getClass().getResource("/cardClose.wav");
			break;
		case 5:
			Sound = this.getClass().getResource("/dice.wav");
			break;
		case 6:
			Sound = this.getClass().getResource("/win.wav");
			break;
		case 7:
			Sound = this.getClass().getResource("/lose.wav");
			break;
		case 8:
			Sound = this.getClass().getResource("/winScreen.wav");
			
	}

		audioInputStream = AudioSystem.getAudioInputStream(Sound);

		clip = AudioSystem.getClip();
		clip.open(audioInputStream);
	}
	public void Pause() {
	clip.stop();
	}
	public void restart() {
		clip.setFramePosition(0);
	}
	
	public void Play() {
		
		clip.start();
		if(volume == 0 || volume == 2) {
		clip.loop(clip.LOOP_CONTINUOUSLY);
	      	FloatControl volumeClip = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        volumeClip.setValue(1);
	        clip.start();
		}
		
		
		}
	
}
	

