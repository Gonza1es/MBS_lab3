package com.example.lab2;


import java.util.Set;

public class Person {

    private String name;
    private Set<String> availableLetters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getAvailableLetters() {
        return availableLetters;
    }

    public void setAvailableLetters(Set<String> availableLetters) {
        this.availableLetters = availableLetters;
    }

    public Person(String name, Set<String> availableLetters) {
        this.name = name;
        this.availableLetters = availableLetters;
    }

    public void updateSet(Boolean value, String letter) {
        if (value)
            availableLetters.add(letter);
        else availableLetters.remove(letter);
    }
}
