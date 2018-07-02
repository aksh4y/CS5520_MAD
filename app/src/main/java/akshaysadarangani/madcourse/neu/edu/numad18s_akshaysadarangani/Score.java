package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

public class Score {
    int id;
    String name;
    int score;
    String timestamp;

    public Score(int id, String name, int score, String timestamp) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.timestamp = timestamp;
    }

    public Score() { }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
