package consegna_4.smartdoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements StatusObserver {
    private static final String CONNECTION_FAILED_MSG = "Connessione non riuscita";

    private boolean active;

    private final DoorConnectionManager model = DoorConnectionManagerImpl.getInstance();
    private Button buttonConnection;
    private EditText deviceName;

    @Override
    public void onStart() {
        super.onStart();
        buttonConnection.setEnabled(true);
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model.getDoorStatus().addObserver(this);
        deviceName = (EditText) findViewById(R.id.textBTName);
        buttonConnection = (Button) findViewById(R.id.connection);
        buttonConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    model.initializeConnection(deviceName.getText().toString());
                    buttonConnection.setEnabled(false);
                } catch (IllegalStateException | IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void notifyStatusChanged() {
        if (active) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (model.getDoorStatus().isConnected()) {
                        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), CONNECTION_FAILED_MSG, Toast.LENGTH_SHORT).show();
                        buttonConnection.setEnabled(true);
                    }
                }
            });
        }
    }
}
