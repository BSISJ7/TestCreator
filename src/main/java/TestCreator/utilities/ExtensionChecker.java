package TestCreator.utilities;

import java.io.File;

public class ExtensionChecker {

    public enum ImageExtensions {
        JPG("jpg"),
        JPEG("jpeg"),
        BMP("bmp"),
        PNG("png"),
        GIF("gif");

        private final String extension;

        ImageExtensions(String extension) {
            this.extension = extension;
        }
        public String getExtension() {
            return extension;
        }
    }

    public enum AudioExtensions {
        MP3("mp3"),
        WAV("wav"),
        AIFF("aiff"),
        AU("au"),
        SND("snd"),
        MIDI("midi"),
        M4A("m4a"),
        WMA("wma"),
        FLAC("flac"),
        OGG("ogg");

        private final String extension;

        AudioExtensions(String extension) {
            this.extension = extension;
        }
        public String getExtension() {
            return extension;
        }
    }

    public static String getExt(File checkFile) {
        return checkFile.getName().substring(checkFile.getName().lastIndexOf(".") + 1);
    }

    public static boolean isImage(File checkFile) {
        return (ExtensionChecker.getExt(checkFile).equalsIgnoreCase("jpg") || ExtensionChecker.getExt(checkFile).equalsIgnoreCase("bmp")
                && !checkFile.getName().equalsIgnoreCase("blank.jpg"));
    }

    public static boolean isImage(String imageName) {
        File checkFile = new File(imageName);
        String extension = ExtensionChecker.getExt(checkFile);
        for (ImageExtensions imageExtension : ImageExtensions.values()) {
            if (extension.equalsIgnoreCase(imageExtension.getExtension())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAudio(File checkFile) {
        String extension = ExtensionChecker.getExt(checkFile);
        for (AudioExtensions audioExtension : AudioExtensions.values()) {
            if (extension.equalsIgnoreCase(audioExtension.getExtension())) {
                return true;
            }
        }
        return false;
    }
}
