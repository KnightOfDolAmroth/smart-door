package consegna_4.synchronization;

import consegna_4.events.SessionUpdateEvent;
import consegna_4.events.SessionUpdateEvent.SessionInfo;
import consegna_4.seiot.common.Observable;

public class ObservableSessionUpdater extends Observable implements SessionUpdater {
    @Override
    public void updateSessionInfo(SessionInfo info) {
        notifyEvent(new SessionUpdateEvent(info));
    }
}
