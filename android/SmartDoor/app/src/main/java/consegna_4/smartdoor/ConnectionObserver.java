package consegna_4.smartdoor;

public interface ConnectionObserver {
    void notifyDataReceived(String msg);
    void notifyConnectionLost();
}
