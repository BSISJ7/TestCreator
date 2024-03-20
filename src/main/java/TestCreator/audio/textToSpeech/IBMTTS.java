package TestCreator.audio.textToSpeech;


import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static TestCreator.audio.textToSpeech.TTSManager.TTS_OUTPUT_FILE;

public class IBMTTS {
    private final TextToSpeech textToSpeech;
    private Clip audioClip;

    private boolean isPlaying;

    private TTSManager ttsManager;

    public IBMTTS() {
        IamAuthenticator authenticator = new IamAuthenticator("<your-ibm-api-key>");
        textToSpeech = new TextToSpeech(authenticator);
    }

    public void speak(String text, float playbackSpeed, TTSManager ttsManager) {
        try {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(text)
                    .accept("audio/wav")
                    .voice("en-US_AllisonV3Voice")
                    .build();

            InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
            InputStream in = WaveUtils.reWriteWaveHeader(inputStream);

            OutputStream out = new FileOutputStream(STR."\{TTS_OUTPUT_FILE}");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.close();
            in.close();
            inputStream.close();

            // Play the audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(STR."\{TTS_OUTPUT_FILE}"));
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.start();
            isPlaying = true;
            audioClip.addLineListener(event ->isPlaying = event.getType() != javax.sound.sampled.LineEvent.Type.STOP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSpeaking() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}