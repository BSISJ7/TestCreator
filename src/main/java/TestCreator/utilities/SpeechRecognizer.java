//package TestCreator.utilities;
//
//import edu.cmu.sphinx.api.Configuration;
//import edu.cmu.sphinx.api.LiveSpeechRecognizer;
//
//import java.io.IOException;
//
//public class SpeechRecognizer {
//    private final LiveSpeechRecognizer recognizer;
//
//    public SpeechRecognizer() throws IOException {
//        Configuration configuration = new Configuration();
//        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
//        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
//        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
//
//        recognizer = new LiveSpeechRecognizer(configuration);
//    }
//
//    public void startRecognition() {
//        recognizer.startRecognition(true);
//    }
//
//    public void stopRecognition() {
//        recognizer.stopRecognition();
//    }
//
//    public String getHypothesis() {
//        return recognizer.getResult().getHypothesis();
//    }
//}