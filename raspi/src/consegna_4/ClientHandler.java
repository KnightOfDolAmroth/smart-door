package consegna_4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Collection;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final LogInfoProvider logInfoProvider;
    private final RoomInfoProvider roomInfoProvider;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public ClientHandler(Socket socket, LogInfoProvider logInfoProvider, RoomInfoProvider roomInfoProvider)
            throws IOException {
        this.socket = socket;
        this.logInfoProvider = logInfoProvider;
        this.roomInfoProvider = roomInfoProvider;
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String lastMsg;
            do {
                lastMsg = readFromServer();
                switch (lastMsg) {
                case "i":
                    writeLineToServer(roomInfoProvider.getRoomInfo().toString());
                    break;
                case "l":
                    writeLinesToServer(logInfoProvider.getLog());
                    break;
                case "q":
                    writeLineToServer("Bye");
                    break;
                default:
                    writeLineToServer("Can't understand request");
                }
            } while (!lastMsg.equals("q"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromServer() throws IOException {
        return reader.readLine();
    }

    private void writeLineToServer(String msg) throws IOException {
        writer.write(msg);
        writer.newLine();
        writer.flush();
    }

    private void writeLinesToServer(Collection<String> msgs) throws IOException {
        for (String s : msgs) {
            writer.write(s);
            writer.newLine();
        }
        writer.flush();
    }
}
