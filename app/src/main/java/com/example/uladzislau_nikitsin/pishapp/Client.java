package com.example.uladzislau_nikitsin.pishapp;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void>{
    private final String ip;
    private final int port;
    private final TextView responseTextView;
    private String response = "";

    public Client(final String ip, final int port, final TextView responseTextView) {
        this.ip = ip;
        this.port = port;
        this.responseTextView = responseTextView;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Socket socket = null;

        try {
            socket = new Socket(ip, port);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead;

            InputStream inputStream = socket.getInputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                response+=outputStream.toString("UTF-8");
            }

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            response = "Unknown host exeprion " +ex.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            response = "IOException: " + ex.toString();
        } finally {
            if (socket!=null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void results) {
        responseTextView.setText(responseTextView.getText() + response +"\n");
        super.onPostExecute(results);
    }
}
