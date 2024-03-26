package TestCreator.audio.transcription;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class TranscrptionManager {

    public enum AUDIO_TRANSCRIBER {
        ASSEMBLY_AI,
        GOOGLE_SPEECH_TO_TEXT,
        MICROSOFT_SPEECH_TO_TEXT,
        AMAZON_TRANSCRIBE,
        IBM_SPEECH_TO_TEXT,
        SPHINX_SPEECH_TO_TEXT,
        VOSK_TRANSCRIBER
    }

    private final AUDIO_TRANSCRIBER transcriber;

    VoskTranscriber voskTranscriber;

    public TranscrptionManager() throws IOException {
        this.transcriber = AUDIO_TRANSCRIBER.VOSK_TRANSCRIBER;
        switch (transcriber) {
            case ASSEMBLY_AI:
                break;
            case GOOGLE_SPEECH_TO_TEXT:
                break;
            case MICROSOFT_SPEECH_TO_TEXT:
                break;
            case AMAZON_TRANSCRIBE:
                break;
            case IBM_SPEECH_TO_TEXT:
                break;
            case SPHINX_SPEECH_TO_TEXT:
                break;
            case VOSK_TRANSCRIBER:
                voskTranscriber = new VoskTranscriber();
                break;
        }
    }

    public String transcribe() throws IOException, UnsupportedAudioFileException {
        switch (transcriber) {
            case ASSEMBLY_AI:
                break;
            case GOOGLE_SPEECH_TO_TEXT:
                break;
            case MICROSOFT_SPEECH_TO_TEXT:
                break;
            case AMAZON_TRANSCRIBE:
                break;
            case IBM_SPEECH_TO_TEXT:
                break;
            case SPHINX_SPEECH_TO_TEXT:
                break;
            case VOSK_TRANSCRIBER:
                VoskTranscriber voskTranscriber = new VoskTranscriber();
                return voskTranscriber.transcribe();
        }
        return "";
    }

    public String listenForCommands(String commandsList) throws RuntimeException, LineUnavailableException, IOException {
        String command = "";
        while (command != null) {
            switch (transcriber) {
                case ASSEMBLY_AI:
                    break;
                case GOOGLE_SPEECH_TO_TEXT:
                    break;
                case MICROSOFT_SPEECH_TO_TEXT:
                    break;
                case AMAZON_TRANSCRIBE:
                    break;
                case IBM_SPEECH_TO_TEXT:
                    break;
                case SPHINX_SPEECH_TO_TEXT:
                    break;
                case VOSK_TRANSCRIBER:
                    VoskTranscriber voskTranscriber = new VoskTranscriber();
                    return voskTranscriber.listenForCommands(commandsList);
            }
        }
        return "";
    }
}
