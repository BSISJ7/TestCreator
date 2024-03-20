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

    public void listenForCommands(String commandsList){
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            Model model = new Model("vosk-model-en-us-0.22-lgraph");
            Recognizer recognizer = new Recognizer(model, 16000, commandsList);

            new Thread(() -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = microphone.read(buffer, 0, buffer.length)) > 0) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        JSONObject resultJson = new JSONObject(recognizer.getResult());
                        String text = resultJson.getString("text");
                        System.out.println(text);
                    } else {
//                        System.out.println(recognizer.getPartialResult());
                    }
                }
            }).start();
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String transcribe() throws IOException {
        LibVosk.setLogLevel(LogLevel.DEBUG);

        try (Model model = new Model("vosk-model-en-us-0.22-lgraph");
             InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(TTS_OUTPUT_FILE)));
             Recognizer recognizer = new Recognizer(model, 16000)) {

            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = ais.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    System.out.println(recognizer.getResult());
                } else {
                    System.out.println(recognizer.getPartialResult());
                }
            }

            System.out.println(recognizer.getFinalResult());
            return recognizer.getFinalResult();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }
}
