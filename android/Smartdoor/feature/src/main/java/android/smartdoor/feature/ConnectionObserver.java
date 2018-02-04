package android.smartdoor.feature;

public interface ConnectionObserver {
    void notifyDataReceived(String msg);
    void notifyConnectionLost();
}
