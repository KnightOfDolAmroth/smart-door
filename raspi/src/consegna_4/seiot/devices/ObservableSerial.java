package consegna_4.seiot.devices;

import consegna_4.seiot.common.Observable;

public abstract class ObservableSerial extends Observable {
    public abstract void sendMsg(String msg);
    public abstract void close();
    protected void messageReceived(String msg) {
        //System.out.println(msg);
        this.notifyEvent(new SerialMessageReceivedEvent(msg));
    }
}
