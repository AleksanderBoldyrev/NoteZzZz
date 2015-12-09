package Server;

import Main.CommonData;

/**
 * Created by Alex on 12.11.2015.
 * Main class for "Server" logic.
 */

public class ServerStarter {
    public static void main(String[] args) {
        System.out.println("Creating server");
        ServerDaemon servDaemon = new ServerDaemon(CommonData.PORT);
        System.out.println("Creating server thread");
        Thread serverThread = new Thread(servDaemon);
        System.out.println("Trying to run server thread");
        serverThread.run();
        System.out.println("Server stopped");
    }

}
