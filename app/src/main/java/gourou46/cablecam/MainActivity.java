package gourou46.cablecam;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Orientation.Listener, View.OnClickListener{
    private ContentResolver cResolver;
    private SeekBar potardVitesse = null;
    private ImageView camera = null;
    private ImageView settings = null;
    private ImageView trajet = null;
    private ImageView sensor = null;
    private boolean isCameraOn;
    private boolean isSensorOn;
    public static boolean btActivated=false;

    private Orientation mOrientation;

    private BluetoothSerial bluetoothSerial;
    private String deviceNamePrefix="HC-06";

    private BroadcastReceiver bluetoothConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast toast = Toast.makeText(getApplicationContext(), "Connecté au périphérique Bluetooth.", Toast.LENGTH_SHORT);
        }
    };
    private BroadcastReceiver bluetoothDisconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast toast = Toast.makeText(getApplicationContext(), "Déconnecté du périphérique Bluetooth !", Toast.LENGTH_SHORT);
            btActivated=false;
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sensor:
                if(isSensorOn) {
                    isSensorOn=false;
                    sensor.setImageResource(R.drawable.gyro_off);
                }
                else {
                    isSensorOn=true;
                    sensor.setImageResource(R.drawable.gyro_on);
                }
                vibrate(50);
                break;

            case R.id.camera:
                if(isCameraOn) {
                    isCameraOn=false;
                    camera.setImageResource(R.drawable.camera_off);
                }
                else {
                    isCameraOn=true;
                    camera.setImageResource(R.drawable.camera_on);
                }
                vibrate(50);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        potardVitesse=(SeekBar)findViewById(R.id.potardVitesse);
        setHighBrightness();

        sensor=(ImageView) findViewById(R.id.sensor);
        sensor.setOnClickListener(this);
        isSensorOn=true;
        sensor.setImageResource(R.drawable.gyro_on);

        camera=(ImageView) findViewById(R.id.camera);
        camera.setOnClickListener(this);
        isCameraOn=true;
        camera.setImageResource(R.drawable.camera_on);

        potardVitesse=(SeekBar)findViewById(R.id.potardVitesse);
        potardVitesse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                vibrate(50);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                potardVitesse.setProgress(50);
                vibrate(50);
            }
        });


        //MessageHandler is call when bytes are read from the serial input
        bluetoothSerial = new BluetoothSerial(this, new BluetoothSerial.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {
                //return doRead(bufferSize, buffer);
                return 0;
            }
        }, deviceNamePrefix);
        //Fired when connection is established and also fired when onResume is called if a connection is already established.
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothConnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_CONNECTED));
        //Fired when the connection is lost
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_DISCONNECTED));
        //Fired when connection can not be established, after 30 attempts.
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_FAILED));

        mOrientation = new Orientation(this);
    }

    protected void onResume() {
        super.onResume();

        //onResume calls connect, it is safe
        //to call connect even when already connected
        bluetoothSerial.onResume();
    }

    public void setHighBrightness(){
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 0.3F;
        getWindow().setAttributes(layout);
    }

    public void vibrate(int n) {
        Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vi.vibrate(n);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrientation.startListening(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOrientation.stopListening();
    }

    @Override
    public void onOrientationChanged(float pitch, float roll) {

        if (btActivated && isSensorOn){
            int intPitch = (int)pitch/2;
            int intRoll = (int)roll/2;
            if(intPitch>45) intPitch=45;
            if(intPitch<-45) intPitch=-45;
            if(intRoll>45) intRoll=45;
            if(intRoll<-45) intRoll=-45;
            intRoll+=90;
            intPitch+=90;

            byte[] buffer = ("P"+String.valueOf(intPitch)+"\n").getBytes();
            byte[] buffer2 = ("R"+String.valueOf(intRoll)+"\n").getBytes();
            try {
                bluetoothSerial.write(buffer);
                bluetoothSerial.write(buffer2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
