package musical_synthesizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MainThread {
	/**Chooses the specified waveform with given bitrate and frequency.
	 * Anything goes straight from the waveform buffer to SourceDataLine.
	 * 0 is the type of Sine waveform by default */
	static void waveForm(SourceDataLine sdl, int wavetype, double frequency, int volume) {
		byte[] waveBuffer = new byte[ 1 ];
		int howLong = (int)(44100.0 / frequency);
	    for (int i = 0; i < howLong; i++) {
	    	switch (wavetype) {
	    		case 1: // the Saw waveform
	    			waveBuffer[0] = (byte)(MakeSaw(frequency, i) * volume / 1.7);
	    			break;
	    		case 2: // the Aeugh waveform
	    			waveBuffer[0] = (byte)(MakeAeugh(frequency, i) * volume / 2.5);
	    			break;
	    		case 3: // the Super waveform
	    			waveBuffer[0] = (byte)(MakeSuper(frequency, i) * volume / 3.0);
	    			break;
	    		case 4: // the What waveform
	    			waveBuffer[0] = (byte)(MakeSuper(frequency, i) * MakeSaw(frequency, i) * MakeSine(frequency, i) * volume / 0.5);
	    			break;
	    		case 5: // the White Noise waveform
	    			waveBuffer[0] = (byte)(MakeWhiteNoise() * volume / 3.0);
	    			break;
	    		default: // the Sine waveform; if volume set to 0, then just do a silence
	    			waveBuffer[0] = (byte)(MakeSine(frequency, i) * volume / 1.0);
	    			break;
	    	}
	        sdl.write(waveBuffer, 0, waveBuffer.length);
		}
	}
	static double MakeAeugh(double frequency, double time) {
		return Math.cos(2 * Math.PI * Math.sin(time / (44100.0 / frequency) * 2.0 * Math.PI));
	}
	static double MakeSaw(double frequency, double time) {
		return time / (44100.0 / frequency);
	}
	static double MakeSine(double frequency, double time) {
		return Math.sin(time / (44100.0 / frequency) * 2.0 * Math.PI);
	}
	static double MakeWhiteNoise() {
		return Math.random();
	}
	static double MakeSuper(double frequency, double time) {
		double cool = Math.sin(time / (44100.0 / frequency) * 2.0 * Math.PI + (44100.0 / frequency) / time);
		if (cool < -1) {
			return -1.0;
		} else if (cool > 1) {
			return 1.0;
		}
		return cool;
	}
	static void soundForm(SourceDataLine sdl, int soundtype, double frequency, int volume, int milliseconds) {
		double speed = ((double)milliseconds * (frequency / 440.0)) / 2.28;
		if (soundtype < 0) {
			soundtype = 0; // waveform shouldn't be a negative value; 0 is a silence by default
		}
		if (frequency < 1) {
			frequency = 1; // frequency is always a natural number; besides, 1 Hz is super low frequency for ears, so consider this as a silence
		} else if (frequency > 44100) {
			frequency = 44100; // there is no more than 44100 sampling rate, so we stick with 44100 as maximum... for a while
		}
		if (volume < 0) {
			volume = 0; // volume is never a negative number
		} else if (volume > 127) {
			volume = 127; // volume should never be more than its maximum;
		}
		for (int i = 1; i < speed; i++) {
			waveForm(sdl, soundtype, frequency, volume);
		}
	}
	public static void main(String[] args) {
		System.out.println("Начало воспроизведения...");
		AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
		SourceDataLine sdl;
		try {
			sdl = AudioSystem.getSourceDataLine( af );
			sdl.open();
			sdl.start();
			
			double[] keyFreq = {
				// doh,     doh minor,  reh,        reh minor,  mee,        fah,        fah minor,  sol,        sol minor,  lya,        lya minor,  see
		/*MO*/	130.812782, 133.591315, 146.832384, 155.563491, 164.813778, 174.614115, 184.997211, 195.997718, 207.652348, 220.000000, 233.081880, 246.941650,
		/*1o*/	261.625565, 277.182631, 293.664767, 311.126983, 329.627556, 349.228231, 369.994422, 391.995436, 415.304697, 440.000000, 466.163761, 493.883301,
		/*2o*/	523.251130, 554.365262, 587.329535, 622.253967, 659.255113, 698.456462, 739.988845, 783.990872, 830.609395, 880.000000, 932.327523, 987.766602,
		/*3o*/	1046.50226,
				// 0,       1,          2,          3,          4,          5,          6,          7,          8,          9,          10,         11,
			};
			
			int time_in_ms = 20; // time in milliseconds
			double pitch = 1.0; // additional sound pitching
			int volume = 64; // sound volume is integer value, change this only if needed!
			
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < keyFreq.length; j++) {
					soundForm(sdl, i, keyFreq[j] * pitch, volume, time_in_ms);
				}
			}
			
			sdl.drain();
			sdl.stop();
			sdl.close();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Конец воспроизведения.");
	}
}
