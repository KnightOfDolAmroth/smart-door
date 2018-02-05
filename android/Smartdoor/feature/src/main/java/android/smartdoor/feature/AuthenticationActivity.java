package android.smartdoor.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.smartdoor.feature.android.smartdoor.feature.connection.DoorConnectionManager;
import android.smartdoor.feature.android.smartdoor.feature.connection.DoorConnectionManagerImpl;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.smartdoor.feature.ObservableDoorStatus.Status;
import android.widget.Toast;

public class AuthenticationActivity extends Activity implements StatusObserver{
    private static final String CREDENTAIL_OK_MSG = "Credenziali corrette";
    private static final String CREDENTAIL_WRONG_MSG = "Credenziali errate";

    private boolean active;
    private final DoorConnectionManager model = DoorConnectionManagerImpl.getInstance();
    private Button buttonAuthentication;
    private EditText username;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);
        active = true;

        model.getDoorStatus().addObserver(this);
        buttonAuthentication = findViewById(R.id.connection);
        username = findViewById(R.id.textUsername);
        password = findViewById(R.id.textPassword);

        if(model.getDoorStatus().getCurrentStatus() == Status.NO_RANGE) {
            buttonAuthentication.setEnabled(false);
        }

        buttonAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.attemptAccess(username.getText().toString(), password.getText().toString());
                buttonAuthentication.setEnabled(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (model.getDoorStatus().isConnected()) {
            active = true;
        } else {
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
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
                        switch (doorStatus.getCurrentStatus()) {
                            case RANGE:
                                buttonAuthentication.setEnabled(true);
                                break;
                            case NO_RANGE:
                                buttonAuthentication.setEnabled(false);
                                break;
                            case WAITING_SESSION_BEGIN:
                                Toast.makeText(getApplicationContext(), CREDENTAIL_OK_MSG, Toast.LENGTH_SHORT).show();
                                break;
                            case WRONG_CREDENTIALS:
                                Toast.makeText(getApplicationContext(), CREDENTAIL_WRONG_MSG, Toast.LENGTH_SHORT).show();
                                buttonAuthentication.setEnabled(true);
                                break;
                            case IN_SESSION:
                                Intent intent = new Intent(AuthenticationActivity.this, WorkingActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                }
            });
        }

    }
}