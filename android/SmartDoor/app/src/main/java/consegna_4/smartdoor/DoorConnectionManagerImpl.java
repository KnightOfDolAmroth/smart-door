package consegna_4.smartdoor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import consegna_4.smartdoor.ObservableDoorStatus.Status;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class DoorConnectionManagerImpl implements DoorConnectionManager {
    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String BT_NOT_ENABLED_MSG = "Il bluetooth non è abilitato";
    private static final String DEVICE_NOT_PAIRED_MSG = "Il dispositivo specificato non è accoppiato";


    private static final long TEMP_UPDATE_REQUEST_MS_PERIOD = 500;

    private static final String MSG_TEMP_REQUEST = "Temp?";
    private static final String MSG_VALUE_SET_PREFIX = "Value:";
    private static final String MSG_ACCESS_PREFIX = "A:";
    private static final String MSG_ACCESS_SEPARATOR = ":";
    private static final String MSG_STOP_SESSION = "End";

    private boolean connected = false;

    private final Handler handler = new Handler();

    private final ObservableDoorStatusImpl doorStatus;

    private final Runnable tempUpdateRequester = new Runnable() {
        @Override
        public void run() {
            try {
                BTConnection.getInstance().write(MSG_TEMP_REQUEST);
                handler.postDelayed(tempUpdateRequester, TEMP_UPDATE_REQUEST_MS_PERIOD);
            } catch (IOException e) { }
        }
    };

    private static DoorConnectionManager instance = new DoorConnectionManagerImpl();

    public static DoorConnectionManager getInstance() {
        return instance;
    }

    private DoorConnectionManagerImpl() {
        BTConnection.getInstance().addObserver(new BTMessageParser());
        this.doorStatus = new ObservableDoorStatusImpl();
    }

    @Override
    public ObservableDoorStatus getDoorStatus() {
        return doorStatus;
    }

    /**
     * @param BTDeviceName the name of the device to start connection with
     * @throws IllegalArgumentException if the device specified is not paired
     * @throws IllegalStateException    if bluetooth is not enabled or device is already connected
     */
    @Override
    public synchronized void initializeConnection(String BTDeviceName) {
        if (connected) {
            throw new IllegalStateException("Already connected to device");
        }
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            throw new IllegalStateException(BT_NOT_ENABLED_MSG);
        }
        BluetoothDevice targetDevice = null;
        Set<BluetoothDevice> pairedList = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedList) {
            if (device.getName().equals(BTDeviceName))
                targetDevice = device;
        }
        if (targetDevice == null) {
            throw new IllegalArgumentException(DEVICE_NOT_PAIRED_MSG);
        }
        new ConnectionInitializationTask(targetDevice, DEFAULT_UUID, new ConnectionUser() {
            @Override
            public void notifyConnectionStatus(boolean connectionSuccessful) {
                updateConnectionStatus(connectionSuccessful);
            }
        }).execute();
    }

    @Override
    public synchronized boolean attemptAccess(String username, String password) {
        return tryWrite(MSG_ACCESS_PREFIX + username + MSG_ACCESS_SEPARATOR + password);
    }

    @Override
    public synchronized boolean setValue(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Value must be between 0 and 100 included");
        }
        return tryWrite(MSG_VALUE_SET_PREFIX + value);
    }

    @Override
    public synchronized boolean stopSession() {
        return tryWrite(MSG_STOP_SESSION);
    }

    private boolean tryWrite(String msg) {
        if (connected) {
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
        //if (connected != this.connected) {
            this.connected = connected;
            doorStatus.setConnected(connected);
        //}
    }

    private synchronized void sessionStarted() {
        doorStatus.setCurrentStatus(Status.IN_SESSION);
        tempUpdateRequester.run();
    }

    private synchronized void sessionStopped() {
        doorStatus.setCurrentStatus(Status.NO_RANGE);
        handler.removeCallbacks(tempUpdateRequester);
    }

    private class BTMessageParser implements ConnectionObserver {
        private static final String MSG_S_TIMEOUT = "S:T";
        private static final String MSG_S_BEGIN = "S:B";
        private static final String MSG_S_STOP = "S:S";
        private static final String MSG_HELLO = "Hello";
        private static final String MSG_BYE = "Bye";
        private static final String MSG_ACCESS_OK = "Valid:T";
        private static final String MSG_ACCESS_NOT_OK = "Valid:F";
        private static final String MSG_TEMP_PREFIX = "Temp:";

        @Override
        public void notifyDataReceived(String msg) {
            synchronized (DoorConnectionManagerImpl.this) {
                if (connected) {
                    switch (msg) {
                        case MSG_HELLO:
                            doorStatus.setCurrentStatus(Status.RANGE);
                            break;
                        case MSG_BYE:
                            doorStatus.setCurrentStatus(Status.NO_RANGE);
                            break;
                        case MSG_S_BEGIN:
                            sessionStarted();
                            break;
                        case MSG_S_STOP: case MSG_S_TIMEOUT:
                            sessionStopped();
                            break;
                        case MSG_ACCESS_OK:
                            doorStatus.setCurrentStatus(Status.WAITING_SESSION_BEGIN);
                            break;
                        case MSG_ACCESS_NOT_OK:
                            doorStatus.setCurrentStatus(Status.WRONG_CREDENTIALS);
                            break;
                        default:
                            if (msg.startsWith(MSG_TEMP_PREFIX)) {
                                try {
                                    int temp = Integer.parseInt(msg.substring(MSG_TEMP_PREFIX.length()));
                                    doorStatus.setTemp(temp);
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
