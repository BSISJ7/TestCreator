package TestCreator.utilities;

import TestCreator.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NamesManager {

    private static final NamesManager NAMES_MANAGER = new NamesManager();
    private static List<String> namesList;

    private NamesManager() {
        namesList = new ArrayList<>();
        try {
            InputStream is = Main.class.getResourceAsStream("/textData/First Names List.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String word;
            while ((word = reader.readLine()) != null) {
                namesList.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NamesManager getNamesList() {
        return NAMES_MANAGER;
    }

    public String getRandomName() {
        return namesList.get(new Random(System.nanoTime()).nextInt(namesList.size()));
    }

    public void addName(String newWord) {
        namesList.add(newWord);
        Collections.sort(namesList);
    }

    public int size() {
        return namesList.size();
    }
}
