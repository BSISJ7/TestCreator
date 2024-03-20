package TestCreator.audio.transcription;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptOptionalParams;

public class AssemblyTranscriber {

    public static final String ASSEMBLY_API_KEY = System.getenv("ASSEMBLY_API_KEY");

    public void transcribe(){
        AssemblyAI client = AssemblyAI.builder()
                .apiKey(ASSEMBLY_API_KEY)
                .build();

        String audioUrl = "./speech.wav";
        var params = TranscriptOptionalParams.builder()
                .speakerLabels(true)
                .build();

        Transcript transcript = client.transcripts().transcribe(audioUrl, params);

        transcript.getUtterances().ifPresent(utterances ->
                utterances.forEach(utterance ->
                        System.out.println(STR."Speaker \{utterance.getSpeaker()}: \{utterance.getText()}")
                )
        );
    }
}