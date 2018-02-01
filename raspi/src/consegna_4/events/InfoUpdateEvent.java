package consegna_4.events;

import consegna_4.RoomInfo;
import consegna_4.seiot.common.Event;

public class InfoUpdateEvent implements Event {
    private final RoomInfo info;

    public InfoUpdateEvent(RoomInfo info) {
        this.info = info;
    }

    public RoomInfo getInfo() {
        return info;
    }
}
