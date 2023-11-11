package TestCreator;

public class TestOptions {

    private boolean disableTestReview; //enable/disable test reviews
    private boolean disableVocabularyReview; //enable/disable reviews for individual vocabulary from test.
    private int defaultReviewDelay; //default number of days before the test will be reviewed

    /**
     * Sets up options for tests
     */
    public TestOptions() {
        disableVocabularyReview = false;
        disableTestReview = false;
        defaultReviewDelay = 0;
    }
}
