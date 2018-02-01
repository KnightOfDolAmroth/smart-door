package consegna_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final LogInfoProvider logInfoProvider;
    private final RoomInfoProvider roomInfoProvider;

    public ClientHandler(Socket socket, LogInfoProvider logInfoProvider, RoomInfoProvider roomInfoProvider)
            throws IOException {
        this.socket = socket;
        this.logInfoProvider = logInfoProvider;
        this.roomInfoProvider = roomInfoProvider;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {
            String lastMsg;

            do {
                lastMsg = reader.readLine();
                switch (lastMsg) {
                case "i":
                    writer.write(roomInfoProvider.getRoomInfo().toString() + "\n");
                    break;
                case "l":
                    for (String s : logInfoProvider.getLog()) {
                        writer.write(s + "\n");
                    }
                    break;
                case "q":
                    writer.write("Bye\n");
                    break;
                default:
                    writer.write("Can't understand request\n");
                }
            } while (!lastMsg.equals("q"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
