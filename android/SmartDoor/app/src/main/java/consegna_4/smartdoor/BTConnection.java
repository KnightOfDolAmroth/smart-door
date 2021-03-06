package consegna_4.smartdoor;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

public class BTConnection {
    private BluetoothSocket btSocket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean stop;
    private static BTConnection instance = null;
    private final Set<ConnectionObserver> observers = new HashSet<>();

    private Thread readingThread;

    private BTConnection() {
        stop = true;
    }

    public static BTConnection getInstance() {
        if (instance == null)
            instance = new BTConnection();
        return instance;
    }

    public boolean setChannelAndStart(BluetoothSocket socket) {
        btSocket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            stop = false;
            readingThread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            while (!stop) {
                                try {
                                    String msg = reader.readLine();
                                    synchronized (observers) {
                                        for (ConnectionObserver observer : observers) {
                                            observer.notifyDataReceived(msg);
                                        }
                                    }
                                } catch (IOException e) {
                                    stop = true;
                                    synchronized (observers) {
                                        for (ConnectionObserver observer : observers) {
                                            observer.notifyConnectionLost();
                                        }
                                    }
                                    Log.d("BT", "Connection lost");
                                }
                            }
                        }
                    });
            readingThread.start();
            return true;
        } catch (IOException e) {
            Log.d("BT", "Error while creating streams");
            return false;
        }
    }

    public void addObserver(ConnectionObserver o) {
        synchronized (observers) {
            observers.add(o);
        }
    }

    public void removeObserver(ConnectionObserver o) {
        synchronized (observers) {
            observers.remove(o);
        }
    }

    public synchronized void write(String msg) throws IOException {
        if (writer == null) {
            throw new IllegalStateException("BT channel was not set up correctly");
        }
        try {
            Thread.sleep(5);//To make sure arduino receives messages separately
        } catch (InterruptedException e) {/**/}
        writer.write(msg);
        writer.flush();
    }

    public void cancel() {
        try {
            btSocket.close();
        } catch (IOException e) { /* ... */ }
    }
}
