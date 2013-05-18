package com.example.socketimagetest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText edittextIP, edittextPORT;
    private TextView ipT, portT;
    
	public final static String EXTRA_IP = "com.example.socketconnectiontest.IP";
	public final static String EXTRA_PORT = "com.example.socketconnection.PORT";
    private String ip;
    private String port;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        edittextIP = (EditText) findViewById(R.id.ipserverx2);
        edittextPORT = (EditText) findViewById(R.id.portserverx2);
        ipT = (TextView) findViewById(R.id.ipx2);
        portT = (TextView) findViewById(R.id.portx2);
        //edittextIP.setText("192.168.2.4");
        edittextIP.setText("130.39.149.179");
        edittextPORT.setText("55000");
        
        edittextIP.setTextColor(Color.BLACK);
        edittextPORT.setTextColor(Color.BLACK);
        ipT.setTextColor(Color.BLACK);
        portT.setTextColor(Color.BLACK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendMessage(View view) {
        // Do something in response to button
        ip = edittextIP.getText().toString();
        port = edittextPORT.getText().toString();
        Intent intent = new Intent(this, DisplayNewActivity.class);
        intent.putExtra(EXTRA_IP, ip);
        intent.putExtra(EXTRA_PORT, port);
        startActivity(intent);
    }
}
