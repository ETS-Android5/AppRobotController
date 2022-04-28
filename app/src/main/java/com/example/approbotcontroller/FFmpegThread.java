package com.example.approbotcontroller;

import android.view.View;
import android.widget.Toast;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;


public class FFmpegThread extends Thread{
    View _myView;
    String cmd = "-fflags nobuffer -f:v mpegts -probesize 8192 -i udp://10.5.5.100:8554 -f mpegts -vcodec copy rtp://localhost:10000";
    FFmpegSession session;

    public FFmpegThread(View view){
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

    @Override
    public void run(){
        session = FFmpegKit.execute(cmd);
        WriteToast(session.getState().toString());
    }
}
