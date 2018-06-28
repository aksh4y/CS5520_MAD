package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.content.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordsGenerator {
    private BufferedReader br;
    ArrayList<String> list;
    private Context context;
    WordsGenerator(Context c) {
        br = null;
        list = new ArrayList<>();
        this.context = c;
    }

    private void readWords() {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.nine_letter_words);
            br = new BufferedReader(new InputStreamReader(inputStream));
            String word;
            while ((word = br.readLine())!=null) {
                list.add(word);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public String getRandomWord() {
        readWords();
        return list.get(randomNumberInRange(list.size() - 1));
    }


    private static int randomNumberInRange(int lim) {
        Random random = new Random();
        return random.nextInt(lim );
    }

    public String shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
}
