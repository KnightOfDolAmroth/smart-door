package consegna_4.synchronization;

import consegna_4.events.AccessAttemptEvent;
import consegna_4.seiot.common.Observable;

public class ObservableCredentialValidationRequester extends Observable implements CredentialValidationRequester {
    @Override
    public void attemptAccess(String username, String password) {
        notifyEvent(new AccessAttemptEvent(username, password));
    }
}
