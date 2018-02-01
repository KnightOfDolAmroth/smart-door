package consegna_4;

import java.io.IOException;

import consegna_4.seiot.devices.impl.ObservableSerialRXTX;
import consegna_4.synchronization.ObservableCredentialValidationRequester;
import consegna_4.synchronization.ObservableDoorCommander;
import consegna_4.synchronization.ObservableInformationUpdater;
import consegna_4.synchronization.ObservableSessionUpdater;

public class Main {

    public static void main(String[] args) throws IOException {
        ObservableDoorCommander doorCommander = new ObservableDoorCommander();
        ObservableCredentialValidationRequester credentialValidationRequester = new ObservableCredentialValidationRequester();
        ObservableInformationUpdater informationUpdater = new ObservableInformationUpdater();
        ObservableSessionUpdater sessionUpdater = new ObservableSessionUpdater();
        CredentialChecker credentialChecker = (username, password) -> {
            return username.equals("username") && password.equals("password");
        };

        CommTask commTask = new CommTask(new ObservableSerialRXTX(args[0]), doorCommander,
                credentialValidationRequester, informationUpdater, sessionUpdater);
        MainTask mainTask = new MainTask(doorCommander, credentialChecker, sessionUpdater,
                credentialValidationRequester, null, null /* lInside, lFailed */);
        LogInfoProviderImpl logInfoProvider = new LogInfoProviderImpl();
        RoomInfoProviderImpl roomInfoProvider = new RoomInfoProviderImpl(doorCommander, informationUpdater);
        InformationServer informationServer = new InformationServer(logInfoProvider, roomInfoProvider);

        commTask.start();
        mainTask.start();
        logInfoProvider.start();
        roomInfoProvider.start();
        informationServer.run();
    }

}
