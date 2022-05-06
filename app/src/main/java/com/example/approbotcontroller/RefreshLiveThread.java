package com.example.approbotcontroller;

import android.webkit.WebView;

public class RefreshLiveThread extends Thread {
    private WebView _myWebView;
    private boolean ON_LIVE;

    public RefreshLiveThread(WebView webView){
        ON_LIVE = true;
        _myWebView = webView;
    }

    @Override
    public void run(){
        while(ON_LIVE){
            _myWebView.post(new Runnable() {
                @Override
                public void run() {
                    _myWebView.loadUrl("http://10.5.5.9/gp/gpControl/execute?p1=gpStream&a1=proto_v2&c1=restart");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void Kill(){
        ON_LIVE = false;
    }


}
