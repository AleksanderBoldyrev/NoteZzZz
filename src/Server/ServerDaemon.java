package Server;

import Main.CommonData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Sasha on 08.10.2015.
 * Class used to create new server threads.
 */

public class ServerDaemon extends Thread{
    private ArrayList<Server> _serverThreads;
    private int _port;
    private ServerSocket _ssocket;
    public final static SecurityHelper sHelper = new SecurityHelper();

    ServerDaemon(int port) {
        _serverThreads = new ArrayList<Server>();
        _serverThreads.clear();
        _port = port;
        try {
            _ssocket = new ServerSocket(_port);
        } catch (IOException e) {
            System.out.println("Couldn't create service.");
        }
    }

    public void run()
    {
        Socket s = null;
        while (true) {

            try {
                try {
                    s = _ssocket.accept();
                }catch (NullPointerException e){
                    System.out.println("Port "+ CommonData.PORT+" is busy. Trying another one.");
                    CommonData.PORT++;
                    _ssocket = new ServerSocket(CommonData.PORT);
                    s = _ssocket.accept();
                }

                Server serv = new Server(s);
                _serverThreads.add(serv);
                Thread t = new Thread(serv);
                t.setDaemon(true);
                t.start();
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Couldn't create a new server thread.");
                break;
            }
        }

        try {
            s.close();
            _ssocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*public void start() {

    }

    public void stop() {

    }

    public void pause() {

    }

    public void resume() {

    }*/
}
