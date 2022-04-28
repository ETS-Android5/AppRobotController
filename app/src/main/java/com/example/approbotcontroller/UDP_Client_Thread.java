package com.example.approbotcontroller;

import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

// https://github.com/yixia/VitamioBundle
// FFmpeg lib made to be used for android

public class UDP_Client_Thread extends Thread {
    private byte[] KEEP_ALIVE;
    private DatagramSocket _mySocket;
    private DatagramPacket _myPacket;
    private int _myPort;
    private String _myUdp;
    private InetAddress _myAddress;
    private View _myView;

    public UDP_Client_Thread(View view) {
        _myView = view;
        _myPort = 8554;
        _myUdp = "10.5.5.9";
        KEEP_ALIVE = "_GPHD_:0:0:2:0.000000".getBytes(StandardCharsets.UTF_8);
        try {
            _mySocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        _myPacket = new DatagramPacket(KEEP_ALIVE,KEEP_ALIVE.length,_myAddress,_myPort);
        try {
            _myAddress = InetAddress.getByName(_myUdp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void WriteToast(String input){
        _myView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_myView.getContext(), input, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void run() {
        _mySocket.connect(_myAddress, _myPort);
        while(true){
            try {
                _mySocket.send(_myPacket);
                Thread.currentThread().sleep(2500);
            }  catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
