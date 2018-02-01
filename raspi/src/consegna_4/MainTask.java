package consegna_4;

import java.io.IOException;

import consegna_4.events.AccessAttemptEvent;
import consegna_4.events.SessionUpdateEvent;
import consegna_4.seiot.common.BasicEventLoopController;
import consegna_4.seiot.common.Event;
import consegna_4.seiot.common.Observable;
import consegna_4.seiot.devices.FlashableLight;
import consegna_4.seiot.devices.Light;
import consegna_4.synchronization.DoorCommander;

public class MainTask extends BasicEventLoopController {
    private static final int L_FLASH_MS = 500;
    private static final String BEGIN_LOG_MSG = "Session started";
    private static final String TIMEOUT_LOG_MSG = "Session timed out";
    private static final String STOP_LOG_MSG = "Session stopped";
    private static final String ACCESS_OK_LOG_MSG = "Access attempt ok";
    private static final String ACCESS_FAIL_LOG_MSG = "Access attempt failed";
    private final DoorCommander doorCommander;
    private final CredentialChecker credentialChecker;

    private final Light lInside;
    private final FlashableLight lFailedAccess;

    public MainTask(DoorCommander doorCommander, CredentialChecker credentialChecker, Observable sessionUpdateProvider,
            Observable credentialValidationRequestProvider, Light lInside, Light lFailedAccess) {
        this.doorCommander = doorCommander;
        this.credentialChecker = credentialChecker;
        this.startObserving(sessionUpdateProvider);
        this.startObserving(credentialValidationRequestProvider);

        this.lInside = lInside;
        this.lFailedAccess = new FlashableLight(lFailedAccess);
    }

    @Override
    protected void processEvent(Event ev) {
        if (ev instanceof AccessAttemptEvent) {
            if (credentialChecker.checkCredentials(
                    ((AccessAttemptEvent) ev).getUsername(),
                    ((AccessAttemptEvent) ev).getPassword())) {
                log(ACCESS_OK_LOG_MSG);
                doorCommander.validateCredentials(true);
            } else {
                log(ACCESS_FAIL_LOG_MSG);
                doorCommander.validateCredentials(false);
                try {
                    lFailedAccess.flash(L_FLASH_MS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (ev instanceof SessionUpdateEvent) {
            switch (((SessionUpdateEvent) ev).getInfo()) {
            case BEGIN: 
                log(BEGIN_LOG_MSG);
                try {
                    lInside.switchOn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case STOP:
                log(STOP_LOG_MSG);
                try {
                    lInside.switchOff();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case TIMEOUT:
                log(TIMEOUT_LOG_MSG);
                try {
                    lFailedAccess.flash(L_FLASH_MS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void log(String msg) {
        Logger.getInstance().log(msg);
    }
}
