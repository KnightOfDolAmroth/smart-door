package android.smartdoor.feature.android.smartdoor.feature.connection;

public interface ConnectionObserver {
    void notifyDataReceived(String msg);
    void notifyConnectionLost();
}
