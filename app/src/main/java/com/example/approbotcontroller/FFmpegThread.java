package com.example.approbotcontroller;

import android.view.View;
import android.widget.Toast;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;


public class FFmpegThread extends Thread{
    private View _myView;
    private String cmd = "-an -flags low_delay -flags2 fast -fflags nobuffer -f:v mpegts -probesize 8192 -i udp://10.5.5.100:8554 -max_delay 1000 -preset ultrafast -r 24 -vcodec libx264 -tune zerolatency -b 900k -f mpegts -vcodec copy udp://127.0.0.1:12345";
    private FFmpegSession _mySession;
    private boolean FFmpegKill;

    public FFmpegThread(View view){
        FFmpegKill = false;
        _myView = view;
    }

    public void WriteToast(String input){
        _myView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(_myView.getContext(), input, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Kill(){
        FFmpegKill = true;
    }

    @Override
    public void run(){
        _mySession = FFmpegKit.execute(cmd);
        while(!FFmpegKill);
    }
}
