package consegna_4.seiot.devices.impl;

import java.io.IOException;

import com.pi4j.io.gpio.*;

import consegna_4.seiot.devices.Light;

public class Led implements Light {
    private int pinNum;
    private GpioPinDigitalOutput pin;

    public Led(int pinNum) {

        this.pinNum = pinNum;
        try {
            GpioController gpio = GpioFactory.getInstance();
            pin = gpio.provisionDigitalOutputPin(Config.pinMap[pinNum]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.switchOff();
            } catch (IOException e) {}
        }));
    }

    @Override
    public synchronized void switchOn() throws IOException {
        pin.high();
        // System.out.println("LIGHT ON - pin "+pin);
    }

    @Override
    public synchronized void switchOff() throws IOException {
        pin.low();
        // System.out.println("LIGHT OFF - pin "+pin);
    }

}
