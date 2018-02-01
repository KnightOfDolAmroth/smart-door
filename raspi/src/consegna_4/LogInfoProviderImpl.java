package consegna_4;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import consegna_4.seiot.common.BasicEventLoopController;
import consegna_4.seiot.common.Event;

public class LogInfoProviderImpl extends BasicEventLoopController implements LogInfoProvider {
    Calendar lastUpdate = Calendar.getInstance();
    private final List<String> dayLog = new ArrayList<>();
    
    public LogInfoProviderImpl() {
        this.startObserving(Logger.getInstance());
    }

    @Override
    public List<String> getLog() {
        synchronized (dayLog) {
            UpdateDay(new Date());
            return new ArrayList<>(dayLog);
        }
    }

    @Override
    protected void processEvent(Event ev) {
        if (ev instanceof Logger.LoggerEvent) {
            synchronized (dayLog) {
                UpdateDay(((Logger.LoggerEvent) ev).getTimestamp());
                dayLog.add(((Logger.LoggerEvent) ev).getFormattedMessage());
            }
        }
    }

    private void UpdateDay(Date date) {
        Calendar newUpdate = Calendar.getInstance();
        newUpdate.setTime(date);
        if (newUpdate.get(Calendar.DAY_OF_YEAR) != lastUpdate.get(Calendar.DAY_OF_YEAR)
                || newUpdate.get(Calendar.YEAR) != lastUpdate.get(Calendar.YEAR)) {
            lastUpdate = newUpdate;
            dayLog.clear();
        }
    }
}
