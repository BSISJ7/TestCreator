package TestCreator.audio.transcription;

import TestCreator.audio.AudioStreamPublisher;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.*;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AwsAudioConverter {
    private static final double VOLUME_THRESHOLD = 0.3;
    private static boolean stopTranscription = false;


    public static void convertAudio(TranscribeStreamingAsyncClient client) throws Exception {
        try {
            StartStreamTranscriptionRequest request = StartStreamTranscriptionRequest.builder()
                    .mediaEncoding(MediaEncoding.PCM)
                    .languageCode(LanguageCode.EN_US)
                    .mediaSampleRateHertz(16_000).build();

            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, format);

            TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(datalineInfo);
            mic.open(format);
            mic.start();

            byte[] buffer = new byte[mic.getBufferSize() / 5];
            int numBytesRead;

            // Create a ScheduledExecutorService
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

            // Schedule a task to stop the transcription after 1 second
            executor.schedule(AwsAudioConverter::stopTranscription, 1, TimeUnit.SECONDS);

            while ((numBytesRead = mic.read(buffer, 0, buffer.length)) != -1 && !stopTranscription) {
                double volume = calculateRMS(buffer);
                if (volume > VOLUME_THRESHOLD) {
                    AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(buffer, 0, numBytesRead), mic.getFormat(), numBytesRead);
                    AudioStreamPublisher publisher = new AudioStreamPublisher(audioInputStream);
                    StartStreamTranscriptionResponseHandler response =
                            StartStreamTranscriptionResponseHandler.builder().subscriber(e -> {
                                TranscriptEvent event = (TranscriptEvent) e;
//                                event.transcript().results().forEach(r -> r.alternatives().forEach(a -> System.out.println(a.transcript())));
                            }).build();

                    client.startStreamTranscription(request, publisher, response);
                }
            }

            // Shutdown the executor
            executor.shutdown();

        } catch (TranscribeStreamingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void stopTranscription() {
        stopTranscription = true;
    }

    public static double calculateRMS(byte[] audioData) {
        long sum = 0;
        for (byte sample : audioData) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / (double) audioData.length);
    }
}
