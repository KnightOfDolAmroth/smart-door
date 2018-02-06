package consegna_4.smartdoor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WorkingActivity extends Activity implements StatusObserver {
    private static final String WRONG_VALUE_MSG = "Il valore deve essere compreso tra 0 e 100";

    private boolean active;
    private final DoorConnectionManager model = DoorConnectionManagerImpl.getInstance();
    private Button buttonIntensity;
    private Button buttonEnd;
    private EditText intensity;
    private TextView temperature;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.working);
        active = true;

        model.getDoorStatus().addObserver(this);
        buttonIntensity = (Button) findViewById(R.id.buttonIntensity);
        buttonEnd = (Button) findViewById(R.id.buttonEnd);
        intensity = (EditText) findViewById(R.id.textIntensity);
        temperature = (TextView) findViewById(R.id.textTemperature);

        if (model.getDoorStatus().getCurrentStatus() == ObservableDoorStatus.Status.NO_RANGE) {
            active = false;
            finish();
        } else {
            buttonIntensity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int val = Integer.parseInt(intensity.getText().toString());
                    if (val < 0 || val > 100) {
                        Toast.makeText(getApplicationContext(), WRONG_VALUE_MSG, Toast.LENGTH_SHORT).show();
                    } else {
                        model.setValue(val);
                    }
                }
            });

            buttonEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onBackPressed() {
        if (buttonEnd.isEnabled()) {
            model.stopSession();
            buttonEnd.setEnabled(false);
            buttonIntensity.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
        model.getDoorStatus().removeObserver(this);
    }

    @Override
    public void notifyStatusChanged() {
        if (active) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ObservableDoorStatus doorStatus = model.getDoorStatus();
                    if (!doorStatus.isConnected()) {
                        active = false;
                        finish();
                    } else {
                        if (doorStatus.getCurrentStatus() != ObservableDoorStatus.Status.IN_SESSION) {
                            buttonEnd.setEnabled(false);
                            buttonIntensity.setEnabled(false);
                            finish();
                        } else {
                            temperature.setText("Temperatura: " + doorStatus.getTemp());
                        }
                    }
                }
            });
        }
    }
}
