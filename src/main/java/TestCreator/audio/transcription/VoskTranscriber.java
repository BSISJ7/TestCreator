package TestCreator.audio.transcription;

import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static TestCreator.audio.textToSpeech.TTSManager.TTS_OUTPUT_FILE;

public class VoskTranscriber {

    public static final String VOSK_LGRAPH = STR."\{System.getProperty("user.dir")}/vosk-model-en-us-0.22-lgraph";
    public static final String VOSK_SMALL = STR."\{System.getProperty("user.dir")}/vosk-model-small-en-us-0.15";
    private final Model model = new Model(VOSK_SMALL);
    private final Recognizer recognizer = new Recognizer(model, 16000);
    private final AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
    private final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

    public VoskTranscriber() throws IOException {
        LibVosk.setLogLevel(LogLevel.WARNINGS);

    }

    public String listenForCommands(String commandsList) throws RuntimeException, LineUnavailableException {
        String cleanedCommandsList = getCleanedCommands(commandsList);
        recognizer.setGrammar(commandsList);

        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException("Line not supported");
        }

        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = microphone.read(buffer, 0, buffer.length)) > 0) {
            if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                JSONObject resultJson = new JSONObject(recognizer.getResult());
                String text = resultJson.getString("text");
                if (!text.isEmpty() && isCommand(text, cleanedCommandsList)) {
                    System.out.println(STR."Text: \{text}");
                    return text;
                }
            } else {
//                        System.out.println(recognizer.getPartialResult());
            }
        }
        return "";
    }

    private boolean isCommand(String command, String commandList) {
        for (String listCommand : commandList.split(",")) {
            if(command.equalsIgnoreCase(listCommand)) {
                return true;
            }
        }
        return false;
    }

    private String getCleanedCommands(String commandsList) {
        return commandsList.replaceAll("[^a-zA-Z0-9, ]", "");
    }

    public String transcribe() throws IOException, UnsupportedAudioFileException {
        LibVosk.setLogLevel(LogLevel.WARNINGS);
        InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(TTS_OUTPUT_FILE)));

        int nbytes;
        byte[] b = new byte[4096];
        while ((nbytes = ais.read(b)) >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                System.out.println(recognizer.getResult());
            } else {
//                    System.out.println(recognizer.getPartialResult());
            }
        }

        System.out.println(recognizer.getFinalResult());
        return recognizer.getFinalResult();
    }
}
