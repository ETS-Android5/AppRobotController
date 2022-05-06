package com.example.approbotcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ImageButton _buttonLeft;
    ImageButton _buttonRight;
    ImageButton _buttonDown;
    ImageButton _buttonUp;
    ImageButton _buttonStop;
    ImageButton _buttonBT_Search;
    Spinner _spinnerBT_Devices;
    ImageButton _buttonLive;
    ImageButton _buttonRecord;
    VLCVideoLayout _myVideoView;
    WebView _myWebView;

    public BluetoothAdapter _myBluetoothAdapter;
    Intent _btEnablingIntent;
    int _requestCodeForEnable;

    ArrayAdapter _myAAdapter;
    BluetoothDevice _Selected_Device;
    Bluetooth_Connected_Thread _myBTservice;
    ArrayList<String> _BT_DevicesName_list;
    ArrayList _BT_Devices_list;
    Bluetooth_Connection_Thread _myBluetoothConnectionThread;
    Bluetooth_Connected_Thread.ConnectedThread _myBluetoothConnectedThread;

    UDP_Client_Thread _myUdpClientThread;
    FFmpegThread _myFFmpegThread;
    RefreshLiveThread _myRefreshLiveThread;
    LibVLC _myLibVlc;
    MediaPlayer _myPlayer;
    IVLCVout _myVout;
    boolean RECORDING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Application Widget
         */
        _buttonLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        _buttonRight = (ImageButton) findViewById(R.id.imageButtonRight);
        _buttonDown = (ImageButton) findViewById(R.id.imageButtonDown);
        _buttonUp = (ImageButton) findViewById(R.id.imageButtonUp);
        _buttonStop = (ImageButton) findViewById(R.id.imageButtonStop);
        _buttonBT_Search = (ImageButton) findViewById(R.id.imageButtonBT_Search);
        _spinnerBT_Devices = (Spinner) findViewById(R.id.spinnerBT_Devices);
        _buttonLive = (ImageButton) findViewById(R.id.imageButtonLive);
        _buttonRecord = (ImageButton) findViewById(R.id.imageButtonRecordGoPro);
        _myVideoView = (VLCVideoLayout) findViewById(R.id.VLCVideoLayoutGoPro);
        _myWebView = (WebView) findViewById(R.id.WebView);

        /**
         * Moving
         */
        buttonDownClick();
        buttonLeftClick();
        buttonRightClick();
        buttonUpClick();
        buttonStopClick();

        /**
         * Bluetooth
         * @Warning_Devices : Devices must be paired, otherwise they wont appear
         * @Warning_UUID : UUID is selected to work with Bluetooth serial card,
         *                 changing the UUID is not possible
         */
        buttonBT_SearchClick();
        _myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        _btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        _requestCodeForEnable = 1;
        _Selected_Device = null;
        _myBTservice = new Bluetooth_Connected_Thread();
        _BT_DevicesName_list = new ArrayList();
        _BT_Devices_list = new ArrayList();
        _spinnerBT_Devices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapterView, View view, int i, long l) {
                _Selected_Device = (BluetoothDevice) _BT_Devices_list.get(i);
                _myBluetoothConnectionThread = new Bluetooth_Connection_Thread(_myBluetoothAdapter, _Selected_Device);
                _myBluetoothConnectionThread.start();
                _myBluetoothConnectedThread = new Bluetooth_Connected_Thread.ConnectedThread(_myBluetoothConnectionThread.getMmSocket());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //_myThread.cancel();
            }
        });

        /**
         * GoPro Live
         * @Protocol : Udp
         * @Udp_Commands_Link : 10.5.5.9:8554
         * @Udp_VideoStream_Link : 10.5.5.100:8554
         */
        RECORDING = false;

        _myUdpClientThread = new UDP_Client_Thread(findViewById(android.R.id.content));
        _myUdpClientThread.start();
        _myFFmpegThread = new FFmpegThread(findViewById(android.R.id.content));
        _myFFmpegThread.start();
        _myRefreshLiveThread = new RefreshLiveThread(_myWebView);

        ArrayList<String> _Options = new ArrayList<String>();
        _Options.add("--file-caching=150");
        _Options.add("--network-caching=150");
        _Options.add("--clock-jitter=0");
        _Options.add("--live-caching=10000");
        _Options.add("--clock-synchro=0");
        _Options.add("--fullscreen");
        _Options.add("-vvv");
        _Options.add("--drop-late-frames");
        _Options.add("--skip-frames");
        _myLibVlc = new LibVLC(findViewById(android.R.id.content).getContext(), _Options);
        _myPlayer = new MediaPlayer(_myLibVlc);
        _myPlayer.attachViews(_myVideoView, null, false, false);
        _myVout = _myPlayer.getVLCVout();


        buttonRecordClick();
        buttonLiveClick();
    }

    @Override
    protected void onDestroy() {
        if(_myUdpClientThread != null){
            _myUdpClientThread.Kill();
        }
        if(_myFFmpegThread != null){
            _myFFmpegThread.Kill();
        }
        if(_myRefreshLiveThread != null){
            _myRefreshLiveThread.Kill();
        }
        super.onDestroy();
    }

    public void buttonUpClick() {
        _buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_myBluetoothConnectedThread != null){
                    if(_myBluetoothConnectedThread.getMmSocket().isConnected()){
                        _myBluetoothConnectedThread.write("z".getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
    }

    public void buttonLeftClick() {
        _buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_myBluetoothConnectedThread != null){
                    if(_myBluetoothConnectedThread.getMmSocket().isConnected()){
                        _myBluetoothConnectedThread.write("q".getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
    }

    public void buttonDownClick() {
        _buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_myBluetoothConnectedThread != null){
                    if(_myBluetoothConnectedThread.getMmSocket().isConnected()){
                        _myBluetoothConnectedThread.write("s".getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
    }

    public void buttonRightClick() {
        _buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_myBluetoothConnectedThread != null){
                    if(_myBluetoothConnectedThread.getMmSocket().isConnected()){
                        _myBluetoothConnectedThread.write("d".getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
    }

    public void buttonStopClick() {
        _buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_myBluetoothConnectedThread != null){
                    if(_myBluetoothConnectedThread.getMmSocket().isConnected()){
                        _myBluetoothConnectedThread.write("x".getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        });
    }

    public void buttonBT_SearchClick() {
        _buttonBT_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_myBluetoothAdapter == null) {
                    Toast.makeText(v.getContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!_myBluetoothAdapter.isEnabled()) {
                        Toast.makeText(v.getContext(), "Bluetooth ON", Toast.LENGTH_SHORT).show();
                        _myBluetoothAdapter.enable();
                    }
                }
                _BT_DevicesName_list.clear();
                _BT_Devices_list.clear();
                if (_myBluetoothAdapter != null && _myBluetoothAdapter.isEnabled()) {
                    Set<BluetoothDevice> pairedDevices = _myBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            _BT_Devices_list.add(device);
                            _BT_DevicesName_list.add(deviceName);
                        }
                        _myAAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, _BT_DevicesName_list);
                        _spinnerBT_Devices.setAdapter(_myAAdapter);
                    }
                }
            }
        });
    }

    public void buttonRecordClick(){
        _buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RECORDING == false){
                    _myWebView.loadUrl("http://10.5.5.9/gp/gpControl/command/shutter?p=1");
                    RECORDING = true;
                }
                else{
                    _myWebView.loadUrl("http://10.5.5.9/gp/gpControl/command/shutter?p=0");
                    RECORDING = false;
                }
            }
        });
    }

    public void buttonLiveClick() {
        _buttonLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Media media = new Media(_myLibVlc, Uri.parse("udp://@:12345"));
                media.setHWDecoderEnabled(true, true);
                _myPlayer.setMedia(media);
                _myPlayer.play();
                _myRefreshLiveThread.start();
            }
        });
    }
}

