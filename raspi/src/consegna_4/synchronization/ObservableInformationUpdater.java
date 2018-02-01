package consegna_4.synchronization;

import consegna_4.RoomInfo;
import consegna_4.events.InfoUpdateEvent;
import consegna_4.seiot.common.Observable;

public class ObservableInformationUpdater extends Observable implements InformationUpdater {
    @Override
    public void updateInfo(RoomInfo newInfo) {
        notifyEvent(new InfoUpdateEvent(newInfo));
    }
}
