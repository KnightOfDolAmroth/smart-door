package android.smartdoor.feature;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class DoorConnectionTask {
    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final long TEMP_UPDATE_REQUEST_MS_PERIOD = 500;

    private static final String MSG_TEMP_REQUEST = "Temp?";
    private static final String MSG_VALUE_SET_PREFIX = "Value:";
    private static final String MSG_ACCESS_PREFIX = "A:";
    private static final String MSG_ACCESS_SEPARATOR = ":";

    private final MainActivity activity;

    private boolean connected = false;

    private final Handler handler = new Handler();

    private final Runnable tempUpdateRequester = new Runnable() {
        @Override
        public void run() {
            try {
                BTConnection.getInstance().write(MSG_TEMP_REQUEST);
                handler.postDelayed(tempUpdateRequester, TEMP_UPDATE_REQUEST_MS_PERIOD);
            } catch (IOException e) { }
        }
    };

    public DoorConnectionTask(MainActivity activity) {
        this.activity = activity;
        BTConnection.getInstance().addObserver(new BTMessageParser());
    }

    /**
     * @param BTDeviceName the name of the device to start connection with
     * @throws IllegalArgumentException if the device specified is not paired
     * @throws IllegalStateException    if bluetooth is not enabled or device is already connected
     */
    public synchronized void initializeConnection(String BTDeviceName) {
        if (connected) {
            throw new IllegalStateException("Already connected to device");
        }
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            throw new IllegalStateException("Bluetooth is not enabled");
        }
        BluetoothDevice targetDevice = null;
        Set<BluetoothDevice> pairedList = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedList) {
            if (device.getName().equals(BTDeviceName))
                targetDevice = device;
        }
        if (targetDevice == null) {
            throw new IllegalArgumentException("Specified device is not paired");
        }
        new ConnectionInitializationTask(targetDevice, DEFAULT_UUID, new ConnectionUser() {
            @Override
            public void notifyConnectionStatus(boolean connectionSuccessful) {
                updateConnectionStatus(connectionSuccessful);
            }
        });
    }

    public synchronized boolean AttemptAccess(String username, String password) {
        return tryWrite(MSG_ACCESS_PREFIX + username + MSG_ACCESS_SEPARATOR + password);
    }

    public synchronized boolean SetValue(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Value must be between 0 and 100 included");
        }
        /*
        if (!connected) {
            throw new IllegalStateException("Not connected");
        }
        if (state != State.SESSION) {
            throw new IllegalStateException("Not in a session");
        }
        */
        return tryWrite(MSG_VALUE_SET_PREFIX + value);
    }

    private boolean tryWrite(String msg) {
        if (!connected) {
            try {
                BTConnection.getInstance().write(msg);
                return true;
            } catch (IOException e) {
                updateConnectionStatus(false);
            }
        }
        return false;
    }

    private synchronized void updateConnectionStatus(boolean connected) {
        if (connected != this.connected) {
            this.connected = connected;
            activity.updateConnectionStatus(connected);
        }
    }

    private void startSession() {
        activity.updateConnectionStatus(true);
        tempUpdateRequester.run();
    }

    private void stopSession() {
        activity.updateConnectionStatus(false);
        handler.removeCallbacks(tempUpdateRequester);
    }

    private class BTMessageParser implements ConnectionObserver {
        private static final String MSG_S_TIMEOUT = "S:Timeout";
        private static final String MSG_S_BEGIN = "S:Begin";
        private static final String MSG_S_STOP = "S:Stop";
        private static final String MSG_HELLO = "Hello";
        private static final String MSG_BYE = "Bye";
        private static final String MSG_ACCESS_OK = "Valid:T";
        private static final String MSG_ACCESS_NOT_OK = "Valid:F";
        private static final String MSG_TEMP_PREFIX = "Temperature: ";

        @Override
        public void notifyDataReceived(String msg) {
            synchronized (DoorConnectionTask.this) {
                if (connected) {
                    switch (msg) {
                        case MSG_HELLO:
                            activity.updateRange(true);
                            break;
                        case MSG_BYE:
                            activity.updateRange(false);
                            break;
                        case MSG_S_BEGIN:
                            startSession();
                            break;
                        case MSG_S_STOP: case MSG_S_TIMEOUT:
                            stopSession();
                            break;
                        case MSG_ACCESS_OK:
                            activity.updateAuthentication(true);
                            break;
                        case MSG_ACCESS_NOT_OK:
                            activity.updateAuthentication(false);
                            break;
                        default:
                            if (msg.startsWith(MSG_TEMP_PREFIX)) {
                                try {
                                    int temp = Integer.parseInt(msg.substring(MSG_TEMP_PREFIX.length()));
                                    activity.updateTemp(temp);
                                } catch (NumberFormatException e) {
                                    unknownMessage(msg);
                                }
                            } else {
                                unknownMessage(msg);
                            }
                    }
                }
            }
        }

        @Override
        public void notifyConnectionLost() {
            updateConnectionStatus(false);
        }

        private void unknownMessage(String msg) {
            Log.e("BTC", "Unknown message received: " + msg);
        }
    }
}
