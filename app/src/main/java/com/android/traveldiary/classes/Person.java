package com.android.traveldiary.classes;

public class Person {
    int personID;
    String username, name, surname, email;

    public Person(int personID, String username, String name, String surname, String email) {
        this.personID = personID;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
