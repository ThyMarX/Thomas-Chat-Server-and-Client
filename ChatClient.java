import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {

        String username, serverIP;
        int port;

        do {
            Scanner scUser = new Scanner(System.in);
            System.out.println("Please type in your Username.");
            username = scUser.next();
            if (username.length() > 12){
                System.out.println("The Username is max 12 chars long, only letters, digits, ‘-‘ and ‘_’ allowed");
            }
        }while (username.length() > 12);

        String username2 =username;
        Scanner scIP = new Scanner(System.in);
        System.out.println("Please type the IP of the server you want to connect to.");
        serverIP = scIP.next();
        Scanner scPort = new Scanner(System.in);
        System.out.println("Please type the servers port number.");
        port = Integer.parseInt(scPort.next());
        String conMSG = "JOIN " + username + ", " + serverIP + " : " + port + ".";
        Socket s = new Socket(serverIP, port);
        PrintStream print = new PrintStream(s.getOutputStream());
        print.println(conMSG);
        Scanner input = new Scanner(s.getInputStream());
        String MsgFromServer = input.nextLine();
        System.out.println(MsgFromServer);

        Thread thread1 = new Thread() {
            public void run() {
                int a = 0;
                while (true) {
                    PrintStream p = null;
                    try {
                        p = new PrintStream(s.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    p.println("IMAV");
                    try {
                        Thread.sleep(60000); //
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Please restart app.");
                    }
                }
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                while (true) try {
                    Scanner scc = new Scanner(s.getInputStream());
                    String msg = scc.nextLine();
                    if (msg.equals("")) {
                    } else {
                        System.out.println(msg);
                    }

                    if (msg.equalsIgnoreCase("QUIT")) {
                        s.close();
                        System.out.println("Goodbye.");
                        System.exit(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Please restart app.");
                }
            }
        };

        Thread thread3 = new Thread() {
            public void run() {
                String message;
                while (true) {
                    try {
                        Scanner sc = new Scanner(System.in);
                        message = sc.nextLine();
                        PrintStream p = new PrintStream(s.getOutputStream());
                        p.println("DATA " + username2+ ": "+ message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Please restart app.");
                    }
                }
            }
        };

        thread1.start();
        thread2.start();
        thread3.start();
    }
}