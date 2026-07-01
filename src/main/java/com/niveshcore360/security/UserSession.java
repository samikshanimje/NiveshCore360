package com.niveshcore360.security;

import com.niveshcore360.entity.User;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe session context for managing the logged-in User.
 * Employs the Observer pattern to notify UI components of session state changes.
 */
@Component
public class UserSession {

    private User currentUser;
    private final List<SessionListener> listeners = new ArrayList<>();

    /**
     * Listener interface to be notified when user log in or log out.
     */
    public interface SessionListener {
        void onSessionChanged(User user);
    }

    /**
     * Add a session change listener.
     */
    public synchronized void addListener(SessionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a session change listener.
     */
    public synchronized void removeListener(SessionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the currently authenticated user.
     */
    public synchronized User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the currently authenticated user and notifies listeners.
     */
    public synchronized void setCurrentUser(User user) {
        this.currentUser = user;
        notifyListeners();
    }

    /**
     * Checks if a user is currently logged in.
     */
    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clears the current user session.
     */
    public synchronized void logout() {
        this.currentUser = null;
        notifyListeners();
    }

    private void notifyListeners() {
        for (SessionListener listener : new ArrayList<>(listeners)) {
            listener.onSessionChanged(currentUser);
        }
    }
}
