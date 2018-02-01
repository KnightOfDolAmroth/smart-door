package consegna_4.events;

import java.util.Objects;

import consegna_4.seiot.common.Event;

public class SessionUpdateEvent implements Event  {
    public static enum SessionInfo {BEGIN, TIMEOUT, STOP};
    private final SessionInfo info;
    
    public SessionUpdateEvent(SessionInfo info) {
        Objects.requireNonNull(info);
        this.info = info;
    }
    public SessionInfo getInfo() {
        return info;
    }
}
