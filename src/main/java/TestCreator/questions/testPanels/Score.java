package TestCreator.questions.testPanels;

//TODO use score class to calculate test scores
public class Score {
    private float maxScore;
    private float score;

    public Score(float maxScore, float score) {
        this.maxScore = maxScore;
        this.score = score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    public void setMaxScore(float maxScore) {
        this.maxScore = maxScore;
    }

    public float getMaxScore() {
        return maxScore;
    }

    public float getPercentage() {
        return (score / maxScore) * 100;
    }

    public String getLetterGrade() {
        float percentage = getPercentage();
        if (percentage >= 0.9) {
            return "A";
        } else if (percentage >= 0.8) {
            return "B";
        } else if (percentage >= 0.7) {
            return "C";
        } else if (percentage >= 0.6) {
            return "D";
        } else {
            return "F";
        }
    }

    public String toString() {
        return """
                Score: %s
                Max Score: %s
                Percentage: %s
                Letter Grade: %s
                """.formatted(score, maxScore, getPercentage() * 100, getLetterGrade());
    }


}
