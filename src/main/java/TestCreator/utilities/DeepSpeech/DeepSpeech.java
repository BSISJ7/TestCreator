package TestCreator.utilities.DeepSpeech;

public class DeepSpeech {

    public static void main(String[] args) {
        String modelPath = "path_to_your_model.pbmm"; // Replace with the path to the pre-trained model
        String audioPath = "path_to_your_audio_file.wav"; // Replace with the path to your audio file

//        try {
//            DeepSpeechModel model = new DeepSpeechModel(modelPath);
//            byte[] audioBytes = Files.readAllBytes(Paths.get(audioPath));
//
//            String transcription = model.stt(audioBytes, audioBytes.length / 2);
//            System.out.println(transcription);
//
//            model.freeModel();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}