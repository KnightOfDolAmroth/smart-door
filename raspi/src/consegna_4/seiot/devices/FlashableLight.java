package consegna_4.seiot.devices;

import java.io.IOException;

public class FlashableLight implements Light {
    private final Light light;
    private final ObservableTimer timer = new ObservableTimer();

    public FlashableLight(Light light) {
        this.light = light;
        timer.addObserver((ev) -> {
            try {
                switchOff();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
    }
    
    /**
     * Turn on the light for a certain amount of milliseconds then turn it off.
     * @param milliseconds
     * @throws IOException
     */
    public synchronized void flash(long milliseconds) throws IOException {
        switchOn();
        timer.stop();
        timer.scheduleTick(milliseconds);
    }
    
    @Override
    public synchronized void switchOn() throws IOException {
        light.switchOn();
    }

    @Override
    public synchronized void switchOff() throws IOException {
        light.switchOff();
    }
}
