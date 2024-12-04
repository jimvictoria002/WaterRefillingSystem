package com.example.waterrefilling;


public class User {

    // Attributes
    private String id;
    private String address;
    private String email;
    private String firstname;
    private String lastname;
    private String middlename;
    private String role;

    public User(){}

    // Constructor
    public User(String address, String email, String firstname, String lastname, String middlename, String id) {
        this.id = id;
        this.address = address;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
    }

    public String getFullname(){
        return this.firstname  + (this.middlename.isEmpty() ? " " : " " + this.middlename + " ")+ this.lastname;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setId(String id) {
        this.id = id;
    }
    // Getters
    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    // Setters
    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    // Optional: Override toString() for easy debugging
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                '}';
    }
}
