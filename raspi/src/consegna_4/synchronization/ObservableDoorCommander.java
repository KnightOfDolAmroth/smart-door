package consegna_4.synchronization;

import consegna_4.events.CredentialControlResult;
import consegna_4.events.InfoRequestEvent;
import consegna_4.seiot.common.Observable;

public class ObservableDoorCommander extends Observable implements DoorCommander {
    @Override
    public void infoRequest() {
        notifyEvent(new InfoRequestEvent());
    }

    @Override
    public void validateCredentials(boolean areValid) {
        notifyEvent(new CredentialControlResult(areValid));
    }
}
