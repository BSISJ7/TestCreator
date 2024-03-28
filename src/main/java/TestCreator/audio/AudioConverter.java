package TestCreator.audio;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.FileNotFoundException;

public class AudioConverter {

    public static void convertToWav(String inputFile, String wavFile) throws FileNotFoundException {
        String inputType = inputFile.substring(inputFile.lastIndexOf(".") + 1);
        switch (inputType) {
            case "mp3":
                convertMp3ToWav(inputFile, wavFile);
                break;
            case "wav":
                break;
            default:
                throw new IllegalArgumentException("Invalid audio type");
        }
    }

    public static void convertMp3ToWav(String mp3File, String wavFile) throws FileNotFoundException {
        if (!new File(mp3File).exists() || !mp3File.endsWith("mp3")) {
            throw new FileNotFoundException(STR."\{mp3File} does not exist or is not a valid mp3 file");
        }

        File source = new File(mp3File);
        File target = new File(!wavFile.endsWith(".wav") ? STR."\{wavFile}.wav" : wavFile);
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(source), target, attrs);
        } catch (IllegalArgumentException | EncoderException e) {
            e.printStackTrace();
        }
    }
}