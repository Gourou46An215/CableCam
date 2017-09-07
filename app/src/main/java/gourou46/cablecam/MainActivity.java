package gourou46.cablecam;

import android.content.ContentResolver;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private ContentResolver cResolver;
    private SeekBar potardVitesse = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        potardVitesse=(SeekBar)findViewById(R.id.potardVitesse);
        setHighBrightness();


    }

    public void setHighBrightness(){
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 0.3F;
        getWindow().setAttributes(layout);
    }
}
