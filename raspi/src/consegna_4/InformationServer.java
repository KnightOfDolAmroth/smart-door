package consegna_4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class InformationServer extends Thread {
    private static final int SERVER_PORT = 1234;
    private final ServerSocket server;
    private final LogInfoProvider logInfoProvider;
    private final RoomInfoProvider roomInfoProvider;

    public InformationServer(LogInfoProvider logInfoProvider, RoomInfoProvider roomInfoProvider) throws IOException {
        server = new ServerSocket(SERVER_PORT);
        this.logInfoProvider = logInfoProvider;
        this.roomInfoProvider = roomInfoProvider;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Socket clientSocket = server.accept();
                new ClientHandler(clientSocket, logInfoProvider, roomInfoProvider).start();
            } catch (IOException e) {
            }
        }
    }
}
