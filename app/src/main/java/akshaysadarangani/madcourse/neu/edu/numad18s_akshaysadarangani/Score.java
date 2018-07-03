package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani;

public class Score {
    String id;
    String name;
    int score;

    public Score(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
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

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
