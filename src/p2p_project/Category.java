package p2p_project;

// enum defining our peer categories. I decided to allow different peers to store different categories of files. 
// e.g. a music peer will store mostly music files
public enum Category {
    MUSIC, SPORTS, MOVIES, TECH, GAMES;

    // this method allows for uniformly mapping peers to categories
    public static Category fromIndex(long idx) {
        Category[] vals = values();
        return vals[(int)(idx % vals.length)];
    }
}
