package consegna_4.smartdoor;

import java.util.HashSet;
import java.util.Set;

public class ObservableDoorStatusImpl implements ObservableDoorStatus {
    private boolean connected;
    private Status currentStatus = Status.NO_RANGE;
    private int temp = -274;
    private Set<StatusObserver> statusObservers = new HashSet<>();

    @Override
    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
        notifyObservers();
    }

    @Override
    public synchronized Status getCurrentStatus() {
        return currentStatus;
    }

    public synchronized void setCurrentStatus(Status currentStatus) {
        this.currentStatus = currentStatus;
        notifyObservers();
    }

    @Override
    public synchronized int getTemp() {
        return temp;
    }

    public synchronized void setTemp(int temp) {
        this.temp = temp;
        notifyObservers();
    }

    @Override
    public synchronized void addObserver(StatusObserver o) {
        statusObservers.add(o);
    }

    @Override
    public synchronized void removeObserver(StatusObserver o) {
        statusObservers.remove(o);
    }

    private void notifyObservers() {
        for (StatusObserver o : statusObservers) {
            o.notifyStatusChanged();
        }
    }
}
