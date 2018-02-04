package android.smartdoor.feature;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private boolean connectionStatus = true;
    private boolean authentication = true;
    private boolean range = true;
    private final Button btConnection = (Button) findViewById(R.id.connection);
    private final Button btAuthentication = (Button) findViewById(R.id.access);
    private final Button btEnd = (Button) findViewById(R.id.buttonEnd);
    private final Button btIntensity = (Button) findViewById(R.id.buttonIntensity);
    private final Button btTemperature = (Button) findViewById(R.id.buttonTemperature);
    private final TextView textIntensity = (TextView) findViewById(R.id.textIntensity);
    private final TextView textTemperature = (TextView) findViewById(R.id.textTemperature);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus && range) {
                    setContentView(R.layout.authentication);
                } else {
                    /*TODO dire che non ci si è connessi o si è fuori range*/
                }
            }
        });

        btAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authentication && range) {
                    setContentView(R.layout.working);
                } else {
                    /*TODO dire che non ci si è autenticati o si è fuori range*/
                }
            }
        });

        btEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus) {
                    setContentView(R.layout.authentication);
                } else {
                    /*TODO dire che non si è più connessi*/
                    setContentView(R.layout.activity_main);
                }
            }
        });

        btIntensity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus) {
                    if ((Integer.parseInt(textIntensity.getText().toString())) <= 100
                            && Integer.parseInt(textIntensity.getText().toString()) >= 0) {
                        /*TODO chiamare metodo del model per inviare il valore*/
                    } else {
                        /*TODO dire che l'input deve essere un intero tra 0 e 100*/
                    }
                } else {
                    /*TODO dire che non si è più connessi*/
                    setContentView(R.layout.activity_main);
                }
            }
        });

        btTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionStatus) {
                    textTemperature.setText(String.valueOf(10) /*TODO chiamare metodo del model per chiedere la temperatura*/);
                } else {
                    /*TODO dire che non si è più connessi*/
                    setContentView(R.layout.activity_main);
                }
            }
        });
    }

    public void updateConnectionStatus(boolean b) {
        this.connectionStatus = b;
    }

    public void updateAuthentication(boolean b) {
        this.authentication = b;
    }

    public void updateRange(boolean b) {
        this.range = b;
    }
}
