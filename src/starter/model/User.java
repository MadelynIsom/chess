package model;

/**
 * represents a model object of a User
 * stores a username, password, and email
 */
public class User {
    /**
     * the user's username
     */
    public String username;
    /**
     * the user's password
     */
    public String password;
    /**
     * the user's email address
     */
    public String email;

    public User(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
