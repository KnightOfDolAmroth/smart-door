package android.smartdoor.feature;

public interface ObservableDoorStatus {
    boolean isConnected();

    Status getCurrentStatus();

    int getTemp();

    void addObserver(StatusObserver o);

    void removeObserver(StatusObserver o);

    enum Status {
        NO_RANGE, RANGE /*waiting authentication request*/, WRONG_CREDENTIALS, WAITING_SESSION_BEGIN, IN_SESSION
    }
}
