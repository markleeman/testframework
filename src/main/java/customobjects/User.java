package customobjects;

import framework.Util;

/**
 *
 */
public class User {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private String phoneNumber;

    public User (String firstName, String lastName, String emailAddress, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress  = emailAddress;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public static User createNewRandomUser() {
        return new User(Util.getRandomString(8), Util.getRandomString(8), Util.getValidEmail(), Util.getRandomString(8), Util.getRandomNumber(11));
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
        lastName = lastName;
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
}