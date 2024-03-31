package TestCreator.audio.textToSpeech;

import TestCreator.audio.AudioConverter;
import TestCreator.audio.AudioPlayer;
import com.jpro.webapi.WebAPI;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static TestCreator.audio.textToSpeech.TTSManager.TTS_OUTPUT_FILE;

public class AmazonPollyTTS {
    private final PollyClient polly;
    private final AudioPlayer audioPlayer = new AudioPlayer();

    private boolean isPlaying = false;
    private boolean stopping = false;

    public AmazonPollyTTS() {
        polly = PollyClient.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    public void speak(String text, float playbackSpeed, TTSManager ttsManager) {
        stopping = false;
        String ssmlText = STR."<speak><prosody rate=\"\{playbackSpeed}\">\{text}</prosody></speak>";

        SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
                .text(ssmlText)
                .textType(TextType.SSML)
                .voiceId(VoiceId.JOANNA)
                .outputFormat(OutputFormat.MP3)
                .build();

        try (ResponseInputStream<SynthesizeSpeechResponse> stream = polly.synthesizeSpeech(synthReq);
             OutputStream outputStream = new FileOutputStream(STR."\{TTS_OUTPUT_FILE}.mp3")) {
            byte[] buffer = new byte[1024];
            int readBytes;

            while ((readBytes = stream.read(buffer)) > 0 && !stopping) {
                outputStream.write(buffer, 0, readBytes);
            }

            AudioConverter.convertToWav(STR."\{TTS_OUTPUT_FILE}.mp3", STR."\{TTS_OUTPUT_FILE}");
            audioPlayer.playSound(STR."\{TTS_OUTPUT_FILE}");
            isPlaying = true;
            if (!WebAPI.isBrowser()) {
                audioPlayer.getMediaPlayer().setOnEndOfMedia(() -> {
                    isPlaying = false;
                    ttsManager.playNextAudio(playbackSpeed);
                });
            }

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void stopSpeaking() {
        stopping = true;
        isPlaying = false;
        audioPlayer.stopSound();
    }
}