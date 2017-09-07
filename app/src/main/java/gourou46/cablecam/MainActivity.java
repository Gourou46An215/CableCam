package gourou46.cablecam;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ContentResolver cResolver;
    private SeekBar potardVitesse = null;
    private ImageView camera = null;
    private ImageView settings = null;
    private ImageView trajet = null;
    private ImageView sensor = null;
    private boolean isCameraOn;
    private boolean isSensorOn;

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
}
