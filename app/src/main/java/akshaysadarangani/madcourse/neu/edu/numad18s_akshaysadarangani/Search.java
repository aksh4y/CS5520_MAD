package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Search {

    private static HashSet<String> set;

    Search(InputStream is) {
        set = new HashSet<>();
        loadWords(is);
    }

    private static void loadWords(InputStream inputStream) {
        try {
            BufferedReader f = new BufferedReader(new InputStreamReader(inputStream));
            String word = null;
            while ((word = f.readLine())!=null) {
                set.add(word);
            }
        } catch (Exception e) {
            System.err.println("Unable to read words from words.txt");
            // continue with empty dictionary
        }
    }

    public boolean isWord(String w) {
        return set.contains(w);
    }
}
