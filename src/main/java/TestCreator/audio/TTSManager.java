package TestCreator.audio;

import TestCreator.audio.AWS.AmazonPollyTTS;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class manages the Text to Speech (TTS) services for the application.
 * It currently supports Amazon Polly and IBM Text to Speech services.
 * The TTS service to be used can be selected by setting the SELECTED_TTS_TYPE variable.
 */
public class TTSManager {
    // Instance of Amazon Polly Text to Speech service
    private static final AmazonPollyTTS AWS_POLLY_TTS = new AmazonPollyTTS();
    // Instance of IBM Text to Speech service
    private static final IBMTTS IBM_TTS = new IBMTTS();

    private final Queue<String> audioQueue = new LinkedList<>();


    // Enum to represent the supported TTS services
    public enum TTSType {
        AWS_POLLY,
        IBM_TEXT_TO_SPEECH
    }

    // Variable to select the TTS service to be used
    private static final TTSType SELECTED_TTS_TYPE = TTSType.AWS_POLLY;

    public void speak(String text, float playbackSpeed) {
        audioQueue.add(text);
        if (!isPlaying())
            playNextAudio(playbackSpeed);
    }

    public void playNextAudio(float playbackSpeed) {
        if (audioQueue.isEmpty()) return;

        String text = audioQueue.poll();

        switch (SELECTED_TTS_TYPE) {
            case AWS_POLLY:
                AWS_POLLY_TTS.speak(text, playbackSpeed, this);
                break;
            case IBM_TEXT_TO_SPEECH:
                IBM_TTS.speak(text, playbackSpeed);
                break;
        }
    }

    private boolean isPlaying() {
        return switch (SELECTED_TTS_TYPE) {
            case AWS_POLLY -> AWS_POLLY_TTS.isPlaying();
            case IBM_TEXT_TO_SPEECH -> IBM_TTS.isPlaying();
            default -> false;
        };
    }

    /**
     * This method stops the speech playback of the selected TTS service.
     */
    public void stopSpeaking() {
        switch (SELECTED_TTS_TYPE) {
            case AWS_POLLY:
                AWS_POLLY_TTS.stopSpeaking();
                break;
            case IBM_TEXT_TO_SPEECH:
                IBM_TTS.stopSpeaking();
                break;
        }
        audioQueue.clear();
    }
}