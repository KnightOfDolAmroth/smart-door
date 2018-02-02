package consegna_4.seiot.devices.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import consegna_4.seiot.devices.ObservableSerial;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;

public class ObservableSerialRXTX extends ObservableSerial {
    private final int DATA_RATE = 9600;
    private static final int TIME_OUT = 2000;

    private SerialPort serialPort;
    private BufferedReader input;
    //private InputStream input;
    private OutputStream output;

    public ObservableSerialRXTX(String portName) {
        CommPortIdentifier portId = null;

        try {
            portId = CommPortIdentifier.getPortIdentifier(portName);
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            //input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener((event) -> {
                if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    /*
                    try {
                        StringBuilder builder = new StringBuilder();
                        while (input.available() > 0){
                            int inputChar=input.read();
                            builder.append((char)inputChar);
                        }
                        messageReceived(builder.toString());
                    } catch (Exception e) {
                        System.err.println(e.toString());
                    }
                    */
                    
                    try {
                        messageReceived(input.readLine());
                    } catch (IOException e) {
                        if (!e.getMessage().equals("Underlying input stream returned zero bytes")) {
                            e.printStackTrace();
                        }
                    }
                    
                }
            });
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void sendMsg(String msg) {
        try {
			msg += "\n";
            output.write(msg.getBytes());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
}
