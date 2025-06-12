package com.id013754;

public interface Subject {
    void attach(Observer observer);

    void detach(Observer observer);

    void notifyObserver(String message, Observer originator);
}
