package com.example.uladzislau_nikitsin.pishapp;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server {
    private final int PORT = 8080;
    private MainActivity activity;
    private ServerSocket serverSocket;
    private String message = "";

    Server(MainActivity activity) {
        this.activity = activity;
    }

    public void start() {
        final Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {
        private int clientCount = 0;
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);

                while(true) {
                    Socket socket = serverSocket.accept();
                    clientCount++;
                    message += "#" +clientCount +" from " +socket.getInetAddress() + ":" +socket.getPort() + "\n";

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.consoleTextView.setText(message);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, clientCount);
                    socketServerReplyThread.run();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread{
        private final int clientCount;
        private final Socket socket;

        SocketServerReplyThread(final Socket socket, final int clientCount) {
            this.clientCount = clientCount;
            this.socket = socket;
        }

        @Override
        public void run() {
            final OutputStream outputStream;
            final String replyMessage = "Hello from server, you are" +clientCount;

            try {
                outputStream = socket.getOutputStream();
                final PrintStream printStream= new PrintStream(outputStream);
                printStream.print(replyMessage);
                printStream.close();

                message += "replayed: " + replyMessage + "\n";
                Log.d("testingthis", ""+message);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.consoleTextView.setText(message);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.consoleTextView.setText(message);
                }
            });
        }
    }
}
