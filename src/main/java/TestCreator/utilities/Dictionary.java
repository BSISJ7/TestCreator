package TestCreator.utilities;

import TestCreator.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dictionary {

    private static final List<String> DICTIONARY_LIST = new ArrayList<>();
    private static final Dictionary DICTIONARY = new Dictionary();

    private Dictionary() {
        initialize();
    }

    public static void initialize(){
        try {
            InputStream is = Main.class.getResourceAsStream("/textData/Dictionary.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String word;
            while ((word = reader.readLine()) != null) {
                DICTIONARY_LIST.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Dictionary getDictionary() {
        return DICTIONARY;
    }

    public String getRandomWord() {
        return DICTIONARY_LIST.get(new Random(System.nanoTime()).nextInt(DICTIONARY_LIST.size()));
    }

    public void addWord(String newWord) {
        DICTIONARY_LIST.add(newWord);
        Collections.sort(DICTIONARY_LIST);
    }

    public int size() {
        return DICTIONARY_LIST.size();
    }
}
