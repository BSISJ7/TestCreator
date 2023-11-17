package TestCreator.utilities;

import TestCreator.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DictionaryManager {

    private static final List<String> DICTIONARY_LIST = new ArrayList<>();
    private static final DictionaryManager DICTIONARY = new DictionaryManager();

    private DictionaryManager() {
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

    public static DictionaryManager getDictionary() {
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