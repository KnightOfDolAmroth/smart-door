package consegna_4.events;

import consegna_4.seiot.common.Event;

public class AccessAttemptEvent implements Event {
    private final String username;
    private final String password;

    public AccessAttemptEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
