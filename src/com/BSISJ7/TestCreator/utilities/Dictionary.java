package com.BSISJ7.TestCreator.utilities;

import com.BSISJ7.TestCreator.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dictionary {

    private static List<String> dictionary;
    private static final Dictionary loadDictionary = new Dictionary();

    private Dictionary(){
        dictionary = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(Main.workDir+"\\Dictionary.txt")));
            String word;
            while((word = reader.readLine()) != null) {
                dictionary.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Dictionary getDictionary(){
        return loadDictionary;
    }

    public String getRandomWord(){
        return dictionary.get(new Random(System.nanoTime()).nextInt(dictionary.size()));
    }

    public void addWord(String newWord){
        dictionary.add(newWord);
        Collections.sort(dictionary);
    }
}
