import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.io.*;

public class ChatServer {
    public   int LastSocketNumber = 0;
    Clients userArray[] = new Clients[20];
    ArrayList Outputs;

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer(){
        Outputs = new ArrayList();
        try{
            ServerSocket serverSocket = new ServerSocket(3333);
            System.out.println("Waiting for a client.");
            boolean running = true;
            firstloop:

            while  (running){
                Socket clientSocket = serverSocket.accept();
                Scanner input = new Scanner(clientSocket.getInputStream());
                String MsgFromClientJoin = input.next();
                String MsgFromClientName = input.next();
                if(SearchByName(MsgFromClientName)==true) {
                }

                PrintStream p = new PrintStream(clientSocket.getOutputStream());
                PrintStream h = new PrintStream(clientSocket.getOutputStream());

                if(SearchByName(MsgFromClientName)==true) {
                    p.println("J_ER 409: Duplicate username detected. Please use another one.");
                    MsgFromClientName= "";
                } else if (MsgFromClientJoin.equalsIgnoreCase("join")) {
                    p.println("J_OK");
                    System.out.println("Number of users in the chat: "+ LastSocketNumber + ".");
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                    Outputs.add(writer);
                    Thread t = new Thread(new ClientManager(clientSocket));
                    t.start();
                } else  {
                    h.println("J_ER 400: The request cannot be fulfilled due to bad syntax.");
                    p.println("quit");
                }

                String IP = clientSocket.getRemoteSocketAddress().toString();
                System.out.println(IP);
                System.out.println("Got a connection to: " + IP);

                ArrayList<String> list = new ArrayList<String>();
                userArray[LastSocketNumber] = new Clients(LastSocketNumber, IP, MsgFromClientName, true, "IMAV", clientSocket,p);
                LastSocketNumber++;
                for(int i = 0; i< LastSocketNumber; i++){
                    list.add(userArray[i].getName());
                }

                String listString = "";
                for (String s : list)
                {
                    listString += s + "\t";
                }

                sendMessageToEveryone("LIST: "+listString);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Fourth");
        }
    }

    public class ClientManager implements Runnable{
        BufferedReader reader;
        Socket serverSocket;
        String clientsIP;

        public ClientManager(Socket clientSocket){
            try {
                String IP = clientSocket.getRemoteSocketAddress().toString();
                System.out.println(IP);
                serverSocket =clientSocket;
                InputStreamReader theReader=new InputStreamReader(serverSocket.getInputStream());

                reader = new BufferedReader(theReader );
                clientsIP =IP;

            } catch (Exception e) {
                System.out.println(e);
                System.out.println("First");
            }
        }

        @Override
        public void run() {
            String message;
            try{
                while((message = reader.readLine()) != null) {
                    System.out.println("Read " + message + clientsIP);
                    System.out.println("This is the socket" + clientsIP);
                    userArray[SearchByIP(clientsIP)].getScket().setSoTimeout(3000);
                    if (message.length()>=250) {
                        PrintStream z = new PrintStream(userArray[SearchByIP(clientsIP)].getScket().getOutputStream());
                        z.println("Your message can't be longer than 250 characters.");
                    } else {
                        sendMessageToEveryone(message);
                    }

                    if (GetWordFromString(message, 2).equalsIgnoreCase("QUIT")) {
                        System.out.println("Its closing time.");
                        ClientTerminator(clientsIP);
                    }
                }

            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Third");
                userArray[SearchByIP(clientsIP)].setName("");
            }
        }
    }

    public int SearchByIP(String IP){
        for (int i = 0; i < LastSocketNumber; i++) {
            String b = userArray[i].getIP();
            if (b.equalsIgnoreCase(IP)) {
                return i;
            } else {
                System.out.println("This IP: " + IP + " was not found.");
            }
        }
        return -1;
    }

    public void ClientTerminator (String clientsIP) {
        try {
            PrintStream z = new PrintStream(userArray[SearchByIP(clientsIP)].getScket().getOutputStream());
            z.println("You are now disconnected.");
            userArray[SearchByIP(clientsIP)].getScket().close();
            userArray[SearchByIP(clientsIP)].setName("");
            userArray[SearchByIP(clientsIP)].getScket().getInputStream().close();
            userArray[SearchByIP(clientsIP)].getScket().getOutputStream().close();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Sixth");
        }
    }

    public void sendMessageToEveryone(String messageFromClient){
        Iterator myIterator = Outputs.iterator();
        while (myIterator.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) myIterator.next();
                writer.println(messageFromClient);
                writer.flush();
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Fifth");
            }
        }
    }

    public String GetWordFromString(String message, int number){
        String[] split = message.split(" ");
        if(split.length>1) {
            System.out.println(split[number]+"");
            return split[number];
        }
        return "Not found.";
    }

    public boolean SearchByName(String Name){
        for (int i = 0; i < LastSocketNumber; i++) {
            String b = userArray[i].getName();
            if (b.equalsIgnoreCase(Name)) {
                return true;
            } else {
                System.out.println("This name: " + Name + " was not found");
            }
        }
        return false;
    }
}