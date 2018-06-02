package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Search {

    private static HashSet<String> set;
    private static InputStream inputStream;

    Search(InputStream is) {
        set = new HashSet<>();
        inputStream = is;
        new LoadDictionary().execute();
    }

    /** Class loads wordlist.txt and creates a HashSet out of it for constant time check.
     *  This happens in the background so that the user does not face any delays and the conversion time
     *  is super quick and unnoticeable to the user */
    static class LoadDictionary extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        @Override
        protected String doInBackground(Void... voids) {
            try {
                BufferedReader f = new BufferedReader(new InputStreamReader(inputStream));
                String word;
                while ((word = f.readLine())!=null) {
                    set.add(word);
                }
            } catch (Exception e) {
                Log.e(TAG,"Unable to read words from wordlist.txt");
                // continue with empty dictionary
            }
            return null;
        }

        protected void onPreExecute (){
            super.onPreExecute();
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    public boolean isWord(String w) {
        return set.contains(w);
    }
}
