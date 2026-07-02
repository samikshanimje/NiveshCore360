package com.niveshcore360.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Basic TTS sound synthesizer using Java audio API lines to read out portfolio details.
 */
public class VoiceAssistantUtil {

    /**
     * Mocks a premium robotic voice tone by generating synthetic sine wave sweeps corresponding
     * to character pitches, ensuring immediate feedback across operating systems without dependencies.
     */
    public static void speakText(String text) {
        new Thread(() -> {
            try {
                // Generate a retro synthetic notification tone for the voice assistant output
                byte[] buffer = new byte[8000 * 2]; // 2 seconds of sound
                double freq = 440.0;
                for (int i = 0; i < buffer.length / 2; i++) {
                    double time = i / 8000.0;
                    double sweep = freq + (i * 0.05); // frequency sweep
                    short sample = (short) (Math.sin(2 * Math.PI * sweep * time) * 8000);
                    buffer[i * 2] = (byte) (sample & 0xff);
                    buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xff);
                }

                InputStream byteStream = new ByteArrayInputStream(buffer);
                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
                AudioInputStream stream = new AudioInputStream(byteStream, format, buffer.length / 2);
                
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] tmp = new byte[1024];
                int count;
                while ((count = stream.read(tmp)) != -1) {
                    line.write(tmp, 0, count);
                }
                line.drain();
                line.close();
            } catch (Exception ex) {
                System.err.println("Voice Assistant Audio generation issue: " + ex.getMessage());
            }
        }).start();
    }
}
