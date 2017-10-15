import java.io.PrintStream;
import java.net.Socket;

public class Clients {
    int ID = -1;
    String IP = null;
    String Name = "Undefined";
    boolean IsConnected;
    String IsAlive = "NotAlive";
    Socket UserSocket = null;
    PrintStream MyWriter;

    public void setName(String name) {
        Name = name;
    }
    public String getName() {
        return Name;
    }
    public Socket getScket() {
        return UserSocket;
    }
    public String getIP() {
        return IP;
    }

    Clients(int id, String ip, String name, boolean isConnected, String isAlive, Socket userSocket, PrintStream myWriter) {
        ID = id;
        IP = ip;
        Name = name;
        IsConnected = isConnected;
        IsAlive = isAlive;
        UserSocket = userSocket;
        MyWriter = myWriter;
    }
}