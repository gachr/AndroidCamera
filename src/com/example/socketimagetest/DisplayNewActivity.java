package com.example.socketimagetest;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class DisplayNewActivity extends Activity {

    private boolean connected = false;
    private String serverIpAddress = "";
    private String serverPort = "";
    private Button buttonDISCONN;
    private ImageView imageSock, imageSockZ;
    private Socket socket = null;
    private DatagramSocket dsocket = null;
    private Intent intentback;
	private Handler mHandler;
	private Handler mHandlerText;
	int imageSet;
	char ok;
	private TextView commentS;
	Thread cThread; 
	Thread rThread;
	int img2plot;
	int busyIm;
	int AUt = 0;
	int numReadFinal = 0;
	int completeImage = 0;
	byte[] imBytes2Send;
	int totalbytes = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mainnewact);
		
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	Bitmap d2 = (Bitmap)msg.obj;
            	imageSock.setImageBitmap(d2);
            	//int numr = (Integer)msg.obj;
            	//commentS.setText(Integer.toString(numr));
            }
        };
        
        mHandlerText = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	int numr = (Integer)msg.obj;
            	commentS.setText(Integer.toString(numr));
            }
        };
        
	    Intent intent = getIntent();
	    serverIpAddress = intent.getStringExtra(MainActivity.EXTRA_IP);
	    serverPort = intent.getStringExtra(MainActivity.EXTRA_PORT);
	    
	    intentback = new Intent(this, MainActivity.class);
	    
	    imageSet = 0;
	    img2plot = 1;      
	    
	    commentS = (TextView) findViewById(R.id.commentStatus);
	    commentS.setTextColor(Color.BLACK);
	    imageSock = (ImageView) findViewById(R.id.socketImage);
	    imageSockZ = (ImageView) findViewById(R.id.socketImageAux);
	    buttonDISCONN = (Button) findViewById(R.id.disconnectNEW);
	    
        buttonDISCONN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	connected = false;
            }
        });	
        
        cThread = new Thread(new ClientThread());
        rThread = new Thread(new RcvThread());
		cThread.start();
	}
	
	public class ClientThread implements Runnable {
		public void run() {
				try {
					commentS.setText(serverIpAddress);
					socket = new Socket(serverIpAddress, Integer.parseInt(serverPort));
					//dsocket = new DatagramSocket();
					//byte[] ipAddress = serverIpAddress.getBytes();
					//InetAddress addr = InetAddress.getByAddress(ipAddress);
					//dsocket.connect(addr,Integer.parseInt(serverPort));
					commentS.setText("connected");
					connected = true;
					buttonDISCONN.setEnabled(true);
					busyIm = 0;
					rThread.start();
					commentS.setText("thread started");
					while (connected) { };
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	   				ok = 0;
	   				out.println("0");
					socket.close();
					startActivity(intentback);
				} catch (Exception e) {
					startActivity(intentback);
				}				
		}
	}
	
	@SuppressLint("HandlerLeak")
	public class RcvThread implements Runnable {
		public void run() {
				while (connected) {
						try {
								int maxRead = 500;//255;
								int numRead = 0;
								int offSET = 0;							
								DataInputStream inputStream = new DataInputStream(socket.getInputStream());
								byte[] imBytes = new byte[31000];
								/*byte[] imBytes;
								byte[] imBytesDG = new byte[500];
								DatagramPacket receivePacket = new DatagramPacket(imBytesDG,maxRead);
			                    dsocket.receive(receivePacket);
			                    imBytes = receivePacket.getData();*/
								numRead = inputStream.read(imBytes,offSET,maxRead);
								totalbytes = totalbytes + numRead;
								/*int i=0;
								int numXCV = 0;
								//Outer:
								while(i <= (maxRead-1)){//254) {
									if (imBytes[i] != 0) {
										numXCV = 1;
										break;// Outer;
									}
									//numXCV = numXCV + imBytes[i];
									i = i+1;
								}*/
								AUt = AUt + 1;
								/*if (numXCV == 0) {
									completeImage = 1;
								} else {*/
									if (AUt == 1){
										imBytes2Send = new byte[numRead];
										System.arraycopy(imBytes, 0, imBytes2Send, 0, numRead);
									} else {
										byte[] imByte2SendAux = imBytes2Send; 
										imBytes2Send = new byte[imByte2SendAux.length + numRead];
										System.arraycopy(imByte2SendAux, 0, imBytes2Send, 0, imByte2SendAux.length);
										System.arraycopy(imBytes, 0, imBytes2Send, imByte2SendAux.length, numRead);
									}
								//}	
								
							    if (numRead != maxRead) { //completeImage == 1) {
							    	Bitmap img2plot = BitmapFactory.decodeByteArray(imBytes2Send, 0, imBytes2Send.length);
							    	Message msg1 = new Message();
							    	msg1.obj = img2plot;
							    	mHandler.sendMessage(msg1);
							    	Message msg2 = new Message();
							    	msg2.obj = totalbytes;//img2plot;
							    	mHandlerText.sendMessage(msg2);							    	
							    	completeImage = 0;
							    	AUt = 0;
							    	totalbytes = 0;
							    }
						} catch (Exception e) {
							Log.e("SocketConnectionv02Activity", "C: ErrorRCVD", e);
						}
				}
		}
	}
	
}
