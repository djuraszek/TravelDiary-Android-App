package com.android.traveldiary.classes;

public class User {
    int personID;
    String username, name, surname, email;

    public User(int personID, String username, String name, String surname, String email) {
        this.personID = personID;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public User(int personID, String username, String name) {
        this.personID = personID;
        this.username = username;
        this.name = name;
    }

    public User() { }

    public int getPersonID() {
        return personID;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getNameInitials() {
        String initials = "";
        name = " " + name;

        for (int i = 0; i < name.length(); i++) {
            // sorry about the 3x&&, dont remember the use of trim, but you
            // can check " your name complete" if " y"==true y is what you want
            if (("" + name.charAt(i)).equals(" ") && i + 1 < name.length() && !("" + name.charAt(i + 1)).equals(" ")) {
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                initials += name.charAt(i + 1);
            }
        }

        return initials.toUpperCase();
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString(){
        return username+" ("+name+")";
    }
}
