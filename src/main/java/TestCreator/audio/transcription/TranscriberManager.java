package TestCreator.audio.transcription;

import java.io.IOException;

public class TranscriberManager {

    public enum STTType {
        ASSEMBLY_AI,
        GOOGLE_SPEECH_TO_TEXT,
        MICROSOFT_SPEECH_TO_TEXT,
        AMAZON_TRANSCRIBE,
        IBM_SPEECH_TO_TEXT,
        SPHINX_SPEECH_TO_TEXT,
        VOSK_TRANSCRIBER
    }

    private STTType sttType;

    public TranscriberManager() {
        this.sttType = STTType.ASSEMBLY_AI;
    }

    public void startListening() {

    }

    public String transcribe() throws IOException {
        switch (sttType) {
            case ASSEMBLY_AI:
                AssemblyTranscriber assemblyTranscriber = new AssemblyTranscriber();
            case GOOGLE_SPEECH_TO_TEXT:
                break;
            case MICROSOFT_SPEECH_TO_TEXT:
                break;
            case AMAZON_TRANSCRIBE:
                break;
            case IBM_SPEECH_TO_TEXT:
                break;
            case SPHINX_SPEECH_TO_TEXT:
//                SphinxTranscriber sphinxSTT = new SphinxTranscriber();
//                sphinxSTT.startListening();
                break;
            case VOSK_TRANSCRIBER:
                VoskTranscriber voskTranscriber = new VoskTranscriber();
                return voskTranscriber.transcribe();
        }

        return "";
    }
}
