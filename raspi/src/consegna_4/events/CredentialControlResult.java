package consegna_4.events;

import consegna_4.seiot.common.Event;

public class CredentialControlResult implements Event {
    private final boolean credentialOk;

    public CredentialControlResult(boolean credentialOk) {
        this.credentialOk = credentialOk;
    }

    public boolean areCredentialOk() {
        return credentialOk;
    }
}
