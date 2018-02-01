package consegna_4;

import java.util.Date;

import consegna_4.seiot.common.Event;
import consegna_4.seiot.common.Observable;

public class Logger extends Observable {
    private static Logger INSTANCE = new Logger();
    
    public static Logger getInstance() {
        return INSTANCE;
    }
    
    private Logger() {}
    
    public void log(String msg) {
        LoggerEvent event = new LoggerEvent(msg);
        System.out.println(event.getFormattedMessage());
        this.notifyEvent(event);
    }

    public static class LoggerEvent implements Event {
        private final Date timestamp = new Date();
        private final String msg;
        private LoggerEvent(String msg) {
            this.msg = msg;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getMsg() {
            return msg;
        }

        public String getFormattedMessage() {
            return "[" + timestamp.toString() + "] - " + msg;
        }
    }
}
