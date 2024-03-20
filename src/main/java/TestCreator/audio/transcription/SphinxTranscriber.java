//package TestCreator.audio.speechToText;
//
//import edu.cmu.sphinx.api.Configuration;
//import edu.cmu.sphinx.api.LiveSpeechRecognizer;
//import edu.cmu.sphinx.api.SpeechResult;
//
//
//import java.io.IOException;
//
//public class SphinxTranscriber {
//
//    private LiveSpeechRecognizer recognizer;
//
//    public SphinxTranscriber() {
//        Configuration configuration = new Configuration();
//
//        // Set path to acoustic model.
//        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
//        // Set path to dictionary.
//        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
//        // Set language model.
//        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
//
//        try {
//            recognizer = new LiveSpeechRecognizer(configuration);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void startListening() {
//        // Start recognition process pruning previously cached data.
//        recognizer.startRecognition(true);
//        SpeechResult result = recognizer.getResult();
//        // Print utterance string.
//        System.out.println(result.getHypothesis());
//    }
//}
