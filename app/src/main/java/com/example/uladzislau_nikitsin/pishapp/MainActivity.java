package com.example.uladzislau_nikitsin.pishapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Server server;

    TextView serverMessages;
    private TextView serverInfoTextView;
    private EditText connectIpEditText;
    private EditText connectPortEditText;
    private Button startConnectButton;
    private TextView responseTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverInfoTextView = (TextView) findViewById(R.id.text_view_main_server_info);
        serverMessages = (TextView) findViewById(R.id.text_view_main_server_messages);
        connectIpEditText = (EditText) findViewById(R.id.edit_text_main_connect_ip);
        connectPortEditText = (EditText) findViewById(R.id.edit_text_main_connect_port);
        startConnectButton = (Button) findViewById(R.id.button_main_start_connect);
        responseTextView = (TextView) findViewById(R.id.text_view_main_response);

        startConnectButton.setOnClickListener(this);

        server = new Server(this);
        server.start();

        serverInfoTextView.setText(server.getIp() +":"+server.getPort());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }

    @Override
    public void onClick(final View v) {
        switch(v.getId()) {
            case R.id.button_main_start_connect:
                final Client client = new Client(String.valueOf(connectIpEditText.getText()),
                        Integer.parseInt(connectPortEditText.getText().toString()),
                        responseTextView);
                client.execute();
                break;
        }
    }
}
