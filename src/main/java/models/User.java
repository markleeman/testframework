package models;

import framework.Util;

/**
 * This class models a user and should contain all of the information needed to create one in the application under test
 * Generally, we should be creating our page objects so that when they need any information about a user we can simply
 * pass them an instance of this class.  The page object can then retrieve the information it needs to perform whatever
 * action we are trying to perform, such as filling in a sign-up form or logging-in to the application.
 */
public class User {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private String phoneNumber;
    private String username;

    public User (String firstName, String lastName, String emailAddress, String password, String phoneNumber, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress  = emailAddress;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.username = username;
    }

    public static User createNewRandomUser() {
        return new User(Util.getRandomStringOfLength(8), Util.getRandomStringOfLength(8), Util.getValidEmail(), Util.getRandomStringOfLength(8), String.valueOf(Util.getRandomNumberOfLength(11)), Util.getRandomStringOfLength(8));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}