package consegna_4.synchronization;

import consegna_4.events.SessionUpdateEvent.SessionInfo;

public interface SessionUpdater {
    void updateSessionInfo(SessionInfo info);
}
