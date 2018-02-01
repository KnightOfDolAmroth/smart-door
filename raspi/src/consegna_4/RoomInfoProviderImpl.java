package consegna_4;

import consegna_4.events.InfoUpdateEvent;
import consegna_4.seiot.common.BasicEventLoopController;
import consegna_4.seiot.common.Event;
import consegna_4.seiot.common.Observable;
import consegna_4.synchronization.DoorCommander;

public class RoomInfoProviderImpl extends BasicEventLoopController implements RoomInfoProvider {
    private static final long MAX_ROOM_INFO_AGE_MS = 200;

    private RoomInfo roomInfo;
    private boolean roomInfoRequested = false;
    private long lastUpdateMillis = 0;

    private final DoorCommander doorCommander;

    public RoomInfoProviderImpl(DoorCommander doorCommander, Observable informationUpdateProvider) {
        this.doorCommander = doorCommander;
        this.startObserving(informationUpdateProvider);
    }

    public synchronized void updateRoomInfo(RoomInfo newInfo) {
        roomInfoRequested = false;
        roomInfo = newInfo;
        lastUpdateMillis = System.currentTimeMillis();
        notifyAll();
    }

    @Override
    public synchronized RoomInfo getRoomInfo() {
        try {
            while (System.currentTimeMillis() - lastUpdateMillis > MAX_ROOM_INFO_AGE_MS) {
                if (!roomInfoRequested) {
                    roomInfoRequested = true;
                    doorCommander.infoRequest();
                }
                wait();
            }
            return roomInfo;

        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    protected void processEvent(Event ev) {
        if (ev instanceof InfoUpdateEvent) {
            this.updateRoomInfo(((InfoUpdateEvent) ev).getInfo());
        }
    }
}
