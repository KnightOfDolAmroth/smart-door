package consegna_4.smartdoor;

public interface DoorConnectionManager {
    void initializeConnection(String BTDeviceName);

    boolean attemptAccess(String username, String password);

    boolean setValue(int value);

    boolean stopSession();

    ObservableDoorStatus getDoorStatus();
}
