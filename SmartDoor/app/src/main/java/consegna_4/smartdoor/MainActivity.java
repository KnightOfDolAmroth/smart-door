package consegna_4.smartdoor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements StatusObserver {

    private static final String BT_NAME = "isi04"; //TODO
    private final DoorConnectionManager model = DoorConnectionManagerImpl.getInstance();
    private Button buttonConnection;
    /*private final Button btAuthentication = (Button) findViewById(R.id.access);
    private final Button btEnd = (Button) findViewById(R.id.buttonEnd);
    private final Button btIntensity = (Button) findViewById(R.id.buttonIntensity);
    private final Button btTemperature = (Button) findViewById(R.id.buttonTemperature);
    private final TextView textIntensity = (TextView) findViewById(R.id.textIntensity);
    private final TextView textTemperature = (TextView) findViewById(R.id.textTemperature);*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model.getDoorStatus().addObserver(this);
        buttonConnection = findViewById(R.id.connection);
        buttonConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.initializeConnection(BT_NAME);
                buttonConnection.setEnabled(false);
            }
        });
    }

    @Override
    public void notifyStatusChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (model.getDoorStatus().isConnected()) {
                    //remove observer;
                    Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                    startActivity(intent);
                } else {
                    //toast e riabilita
                    buttonConnection.setEnabled(true);
                }
            }
        });
    }
}
