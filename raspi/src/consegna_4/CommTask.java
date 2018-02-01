package consegna_4;

import consegna_4.events.CredentialControlResult;
import consegna_4.events.InfoRequestEvent;
import consegna_4.events.SessionUpdateEvent;
import consegna_4.events.SessionUpdateEvent.SessionInfo;
import consegna_4.seiot.common.BasicEventLoopController;
import consegna_4.seiot.common.Event;
import consegna_4.seiot.common.Observable;
import consegna_4.seiot.devices.ObservableSerial;
import consegna_4.seiot.devices.SerialMessageReceivedEvent;
import consegna_4.synchronization.CredentialValidationRequester;
import consegna_4.synchronization.InformationUpdater;
import consegna_4.synchronization.SessionUpdater;

public class CommTask extends BasicEventLoopController {
    private static final String CREDENTIAL_INFO_COMMAND = "Valid:";
    private static final String INFO_REQUEST_COMMAND = "InfoRequest";
    private static final String SPLIT_VALUE = ":";
    private static final String INFO_PREFIX = "I";
    private static final String SESSION_INFO_PREFIX = "S";
    private static final String ACCESS_PREFIX = "A";
    private static final String SESSION_INFO_BEGIN = "B";
    private static final String SESSION_INFO_TIMEOUT = "T";
    private static final String SESSION_INFO_STOP = "S";

    private final ObservableSerial serial;
    //private final Observable commandProvider;
    private final CredentialValidationRequester credentialValidationRequester;
    private final InformationUpdater informationUpdater;
    private final SessionUpdater sessionUpdater;

    public CommTask(ObservableSerial serial, Observable commandProvider,
            CredentialValidationRequester credentialValidationRequester, InformationUpdater informationUpdater,
            SessionUpdater sessionUpdater) {
        this.serial = serial;
        this.startObserving(serial);
        //this.commandProvider = commandProvider;
        this.startObserving(commandProvider);
        this.credentialValidationRequester = credentialValidationRequester;
        this.informationUpdater = informationUpdater;
        this.sessionUpdater = sessionUpdater;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serial.close();
        }));
    }

    @Override
    protected void processEvent(Event ev) {
        if (ev instanceof SerialMessageReceivedEvent) {
            String msg = ((SerialMessageReceivedEvent) ev).getMsg();
            if (!parseMessage(msg)) {
                unknownMessage(msg);
            }
        } else if (ev instanceof CredentialControlResult) {
            sendCommand(CREDENTIAL_INFO_COMMAND + (((CredentialControlResult) ev).areCredentialOk() ? "T" : "F"));
        } else if (ev instanceof InfoRequestEvent) {
            sendCommand(INFO_REQUEST_COMMAND);
        }
    }

    private void unknownMessage(String msg) {
        System.err.println("Unknown message received: " + msg);
    }

    private void sendCommand(String command) {
        serial.sendMsg(command);
    }

    private boolean parseMessage(String msg) {
        String[] splitMsg = msg.split(SPLIT_VALUE);

        switch (splitMsg[0]) {
        case INFO_PREFIX:
            if (splitMsg.length != 3) {
                return false;
            }
            try {
                int temp = Integer.parseInt(splitMsg[1]);
                int val = Integer.parseInt(splitMsg[2]);
                informationUpdater.updateInfo(new RoomInfo(temp, val));
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        case SESSION_INFO_PREFIX:
            if (splitMsg.length != 2) {
                return false;
            }
            SessionUpdateEvent.SessionInfo info = null;
            switch (splitMsg[1]) {
            case SESSION_INFO_BEGIN:
                info = SessionInfo.BEGIN;
                break;
            case SESSION_INFO_STOP:
                info = SessionInfo.STOP;
                break;
            case SESSION_INFO_TIMEOUT:
                info = SessionInfo.TIMEOUT;
                break;
            }
            if (info == null) {
                return false;
            }
            sessionUpdater.updateSessionInfo(info);
            return true;
        case ACCESS_PREFIX:
            if (splitMsg.length != 3) {
                return false;
            }
            credentialValidationRequester.attemptAccess(splitMsg[1], splitMsg[2]);
            return true;
        default:
            return false;
        }
    }
}
