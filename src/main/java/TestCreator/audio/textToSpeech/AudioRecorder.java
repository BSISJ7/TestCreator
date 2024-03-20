package TestCreator.audio.textToSpeech;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecorder {
    // the line from which audio data is captured
    private TargetDataLine line;

    private boolean recording = false;

    public enum AudioThreshold {
        DEFAULT_THRESHOLD(20000),
        HIGH_THRESHOLD(50000);

        private final int value;

        AudioThreshold(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }



    public void testRecord(){
        try {
            // Specify the audio format
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

            // Get a target data line
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);

            // Open the target data line
            targetLine.open(format);


            System.out.println(STR."Start Buffer: \{targetLine.getBufferSize()}");

            // Flush the line to discard any remaining data from the previous recording
            targetLine.flush();
            targetLine.drain();

            System.out.println(STR."End Buffer: \{targetLine.getBufferSize()}");

            // Start capturing audio
            targetLine.start();
            targetLine.flush();
            targetLine.drain();

            // Write captured audio data to a file in a new thread
            File outputFile = new File("recorded_audio.wav");
            outputFile.delete();
            Thread recordingThread = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(targetLine)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recordingThread.start();
            System.out.println("Start capturing audio...");

            checkThresholdV2(20000);

            // Record audio for 5 seconds
            Thread.sleep(5000);

            // Stop capturing audio and close resources
            targetLine.stop();
            targetLine.close();

            // Wait for the recording thread to finish
            recordingThread.join();
        } catch (LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("End capturing audio...");
    }

    /**
     * Captures the sound and record into a WAV file
     */
    public void startRecording() {
        startRecording(0);
    }

    /**
     * Captures the sound and record into a WAV file
     */
    public void startRecording(int milliseconds, int threshold) {
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }else{
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.flush();
                line.start();
                recording = true;
                System.out.println("Start recording...");

//                new Thread(() -> {
//                }).start();

                Timer timer = new Timer();
                TimerTask stopTask = new TimerTask() {
                    @Override
                    public void run() {
                        finishRecording();
                    }
                };
                if(milliseconds > 0) {
                    System.out.println(STR."Recording for \{milliseconds} milliseconds");
                    timer.schedule(stopTask, milliseconds);
                    System.out.println("Stopping Task Scheduled");
                }
                System.out.println("Here");
                checkThreshold(threshold);
            }
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }


    private void checkThresholdV2(int threshold) {
        byte[] buffer = new byte[1024];
        while (recording) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            long totalSquared = 0;
            int numSamples = 0;
            for (int i = 0; i < bytesRead; i += 2) {
                short audioSample = (short) ((buffer[i + 1] << 8) | buffer[i]);
                totalSquared += audioSample * audioSample;
                numSamples++;
            }
            double rms = Math.sqrt(totalSquared / (double) numSamples);
            System.out.println("RMS: " + rms);
            if (rms > threshold) {
                System.out.println("Sound detected over threshold: " + rms);
                System.exit(0);
            }
        }
    }


    private void checkThreshold(int threshold) {
        byte[] buffer = new byte[1024];
        while (recording) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            for (int i = 0; i < bytesRead; i += 2) {
                short audioSample = (short) ((buffer[i + 1] << 8) | buffer[i]);
                System.out.println(STR."Audio Sample: \{Math.abs(audioSample)}");
                if (Math.abs(audioSample) > threshold) {
                    System.out.println(STR."Sound detected over threshold: \{Math.abs(audioSample)}");
                    System.exit(0);
                }
            }
        }
    }


    /**
     * Captures the sound and record into a WAV file
     */
    public void startRecording(int milliseconds) {
        startRecording(milliseconds, AudioThreshold.DEFAULT_THRESHOLD.getValue());
    }

    /**
     * Closes the target data line to finishRecording capturing and recording
     */
    private void finishRecording() {
        line.stop();
        line.close();
        recording = false;
        System.out.println("Recording stopped.");
        saveRecording();
        System.exit(0);
    }

    private void saveRecording() {
        try {
            AudioInputStream ais = new AudioInputStream(line);
            File audioFile = new File("recorded_audio.wav");
            if (audioFile.exists()) audioFile.delete();
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("recorded_audio.wav"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
