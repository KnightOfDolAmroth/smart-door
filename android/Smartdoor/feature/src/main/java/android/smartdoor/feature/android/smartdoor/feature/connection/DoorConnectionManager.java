package android.smartdoor.feature.android.smartdoor.feature.connection;

import android.smartdoor.feature.ObservableDoorStatus;

public interface DoorConnectionManager {
    void initializeConnection(String BTDeviceName);

    boolean attemptAccess(String username, String password);

    boolean setValue(int value);

    boolean stopSession();

    public ObservableDoorStatus getDoorStatus();
}
