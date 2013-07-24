package org.jaykhopale.LocationAPIsTest;


import android.app.AlertDialog;
import android.content.*;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyLocationActivity extends FragmentActivity {


    private LocationDemand locDemand;
    private TextView locView;


    @Override
    protected void onPause() {
        unbindService(mConnection);
        stopService(new Intent(this, LocationDemand.class));

        super.onPause();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("GPS is Disabled!");
            alertDialogBuilder
                    .setMessage("Enable GPS?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Intent intent = new Intent(this, LocationDemand.class);
            startService(intent);
        }


        Button showLoc = (Button) findViewById(R.id.locbutton);

        locView = (TextView) findViewById(R.id.textView);

        showLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locDemand != null) {
                    String locationGot = locDemand.returnLocation();

                    Log.d(MyLocationActivity.class.getSimpleName(), "Location:" + locationGot);
                    locView.setText(locationGot);
                } else {
                    Log.d(MyLocationActivity.class.getSimpleName(), "Cannot bind to the service");
                    locView.setText("Trying to get location");
                }

            }
        });

    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            locDemand = ((LocationDemand.MyBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locDemand = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();


        bindService(new Intent(this, LocationDemand.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        stopService(new Intent(this, LocationDemand.class));
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(this, LocationDemand.class), mConnection,
                Context.BIND_AUTO_CREATE);


    }


}


