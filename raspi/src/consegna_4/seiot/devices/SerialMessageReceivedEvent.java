package consegna_4.seiot.devices;

import consegna_4.seiot.common.Event;

public class SerialMessageReceivedEvent implements Event {
    private final String msg;
    public SerialMessageReceivedEvent(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }
}
