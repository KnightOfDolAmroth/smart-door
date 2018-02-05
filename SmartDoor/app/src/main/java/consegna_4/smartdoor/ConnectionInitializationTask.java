package consegna_4.smartdoor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectionInitializationTask extends AsyncTask<Void, Void, Boolean> {
    private BluetoothSocket btSocket = null;
    private final ConnectionUser onPostExecuteCallback;

    public ConnectionInitializationTask(BluetoothDevice server, UUID uuid, ConnectionUser onPostExecuteCallback) {
        try {
            btSocket = server.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { /* ... */ }
        this.onPostExecuteCallback = onPostExecuteCallback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            btSocket.connect();
        } catch (IOException connectException) {
            try {
                btSocket.close();
            } catch (IOException closeException) { /* ... */ }
            return false;
        }
        BTConnection cm = BTConnection.getInstance();
        if (cm.setChannel(btSocket)) {
            cm.start();
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean par) {
        onPostExecuteCallback.notifyConnectionStatus(par);
        if (par) {
            Log.i("BT", "Connected");
        } else {
            Log.e("BT", "Can't connect to server");
        }
    }
}
