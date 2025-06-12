package com.id013754;

/* Represents an observer that can be notified of changes in a Subject
 * Player will implement this interface
 */
public interface Observer {
    void update(String message);

    // To identify the observer
    String getName();
}
