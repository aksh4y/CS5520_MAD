package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

/**
 * Created by Akshay on 7/3/2018.
 */

public class User {
    public String name;
    public String email;
    public int score;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email, int score) {
        this.name = name;
        this.email = email;
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}