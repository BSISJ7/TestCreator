package TestCreator.audio;

import TestCreator.utilities.StageManager;
import com.jpro.webapi.WebAPI;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

import static TestCreator.audio.textToSpeech.TTSManager.TTS_OUTPUT_NAME;

public class AudioPlayer {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public void playSound(String audioFilePath) {
        if (WebAPI.isBrowser()) {
            WebAPI webAPI = WebAPI.getWebAPI(StageManager.getStage());
            webAPI.executeScript(
                "var audioContext = new (window.AudioContext || window.webkitAudioContext)();\n" +
                "var source = audioContext.createBufferSource();\n" +
                "// Load audio file data into a buffer\n" +
                "var request = new XMLHttpRequest();\n" +
                STR."request.open('GET','\{TTS_OUTPUT_NAME}', true);\n" +
//                "request.open('GET','" + System.getProperty("user.dir") + "/" + TTS_OUTPUT_FILE + "', true);\n" +
                "request.responseType = 'arraybuffer';\n" +
                "request.onload = function() {\n" +
                "    audioContext.decodeAudioData(request.response, function(buffer) {\n" +
                "        source.buffer = buffer;\n" +
                "        source.connect(audioContext.destination);\n" +
                "        source.start(0);\n" +
                "        source.onended = function() {\n" +
                // This is a callback function that will be called when the audio has finished playing
                "        };\n" +
                "    });\n" +
                "};\n" +
                "request.send();"
            );
        } else {
            playSound(audioFilePath, 1.0);
        }
    }

    public void playSound(String audioFilePath, double rate) {
        Media sound = new Media(new File(audioFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setRate(rate);
        mediaPlayer.play();
        isPlaying = true;
        mediaPlayer.setOnEndOfMedia(() -> isPlaying = false);
        System.out.println("Playing");
    }

    public void stopSound() {
        if (mediaPlayer != null && isPlaying){
            mediaPlayer.stop();
            mediaPlayer.dispose();
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
