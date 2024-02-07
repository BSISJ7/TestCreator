package TestCreator.utilities;

import java.util.Collections;
import java.util.List;

public class TDBQuestion {
    public enum Categories {
        GENERAL_KNOWLEDGE(9),
        BOOKS(10),
        FILM(11),
        MUSIC(12),
        MUSICALS_AND_THEATRES(13),
        TELEVISION(14),
        VIDEO_GAMES(15),
        BOARD_GAMES(16),
        SCIENCE_AND_NATURE(17),
        COMPUTERS(18),
        MATHEMATICS(19),
        MYTHOLOGY(20),
        SPORTS(21),
        GEOGRAPHY(22),
        HISTORY(23),
        POLITICS(24),
        ART(25),
        CELEBRITIES(26),
        ANIMALS(27),
        VEHICLES(28),
        COMICS(29),
        GADGETS(30),
        ANIME_AND_MANGA(31),
        CARTOON_AND_ANIMATIONS(32);

        private final int id;

        /**
         * @param id The id of the category
         */
        Categories(int id) {
            this.id = id;
        }


        /**
         * @return A list of all the category names in alphabetical order
         */
        public static List<String> getCategoryNames() {
            List<String> categoryNames = new java.util.ArrayList<>();
            for (Categories category : Categories.values()) {
                categoryNames.add(category.name().replace("_", " "));
            }
            Collections.sort(categoryNames);
            return categoryNames;
        }

        public int getId() {
            return id;
        }
    }

    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correct_answer;
    private List<String> incorrect_answers;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public List<String> getIncorrect_answers() {
        return incorrect_answers;
    }

    public void setIncorrect_answers(List<String> incorrect_answers) {
        this.incorrect_answers = incorrect_answers;
    }
}
