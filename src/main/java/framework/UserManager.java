package framework;

import customobjects.User;

public enum UserManager {

    CUSTOMER ("Tom", "Smith", "tom.smith@example.com", "SuperSecretPassword!", "07700 900000", "tomsmith"),
    STAFF_MEMBER ("Alice", "Jones", "alice.jones@example.com", "PasswordWithAZero", "07700 900100", "ajones"),
    ADMIN ("Bob", "Williams", "bob.williams@example.com", "admin", "07700 900200", "admin");

    public final String firstName;
    public final String lastName;
    public final String emailAddress;
    public final String password;
    public final String phoneNumber;
    public final String username;

UserManager(String firstName, String lastName, String emailAddress, String password, String phoneNumber, String username){
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.username = username;
    }

    public User getUser() {
        return new User(firstName, lastName, emailAddress, password, phoneNumber, username);
    }
}