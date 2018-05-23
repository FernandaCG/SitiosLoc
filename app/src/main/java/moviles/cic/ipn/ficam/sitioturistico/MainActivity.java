package moviles.cic.ipn.ficam.sitioturistico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Set;

public class MainActivity extends AppCompatActivity  {
    Button buttonSitioMexico;
    Button buttonSitioTokio;
    Button buttonSitioSydney;
    Button buttonSitioMontreal;
    Button buttonSitioPekin;
    Button buttonSitioParis;
    Button buttonSitioRio;
    Button buttonSitioBerlin;
    Button buttonSitioNewYork;
    Button buttonSitioRoma;

    MapsActivity mapsActivity;
    LocationManager locationManager;
    private int timeUpdateLocation = 2000;
    private float distanceUpateLocation = (float)0.05;


    // Keys for reading data from SharedPreferences.
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";
    public static final String DZOOM = "pref_distance";


    private Set<String> regionsSet; // World regions in current quiz

    private boolean phoneDevice = true; // Used to force portrait mode.
    private boolean preferencesChanged = true; // The user changed preferences.
    private int guessRows; // Number of rows displaying guess Buttons
    private LinearLayout[] guessLinearLayouts; // Rows of answer Buttons
    private int action_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mapsActivity = (MapsActivity) getSupportFragmentManager().findFragmentById(R.id.fragmentGoogleMaps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        guessLinearLayouts = new LinearLayout[5];
        guessLinearLayouts[0] = (LinearLayout) findViewById(R.id.linearLayout1);
        guessLinearLayouts[1] = (LinearLayout) findViewById(R.id.linearLayout2);
        guessLinearLayouts[2] = (LinearLayout) findViewById(R.id.linearLayout3);
        guessLinearLayouts[3] = (LinearLayout) findViewById(R.id.linearLayout4);
        guessLinearLayouts[4] = (LinearLayout) findViewById(R.id.linearLayout5);

        ///editTextPreference = (EditTextPreference) findViewById(action_settings);

        // Set default values in the app's SharedPreferences.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Register listener for SharedPreferences changes.
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        // Determine screen size.
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // If the device is a tablet, set phoneDevice to false.
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
        {
            phoneDevice = false;
        }

        // If the app is running on phone-sized device, allow only portrait orientation.
        if(phoneDevice == true)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        initButtons();
        initLocationService();
    }

    private void initLocationService() {
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.d("initLocationService", "Registrando Servicio....");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    timeUpdateLocation, distanceUpateLocation, this.mapsActivity);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Get the device's current orientation.
        int orientation = getResources().getConfiguration().orientation;

        // Display the app's menu only in portrait orientation.
        if(orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(preferencesChanged) {
            // Now that the default preferences have been set, initialize MainActivityFragment
            // and start the quiz.
           // MainActivityFragment quizFragment = (MainActivityFragment)getSupportFragmentManager().
              //      findFragmentById(R.id.quizFragment);
            updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
//            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                // Called when the user changes the app's preferences.
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true;  // User changed app settings.

                   // MainActivityFragment quizFragment = (MainActivityFragment)getSupportFragmentManager().
                          //  findFragmentById(R.id.quizFragment);

                    if(key.equals(CHOICES))
                    {
                        // Number of choices to display changed.
                        //quizFragment.updateGuessRows(sharedPreferences);
                        updateGuessRows(sharedPreferences);
                        //quizFragment.resetQuiz();
                    }
                    else
                         if (key.equals(DZOOM))
                        {
                            updateRegions(sharedPreferences);
                        }
                    Toast.makeText(MainActivity.this,
                            R.string.restarting_quiz,
                            Toast.LENGTH_SHORT).show();
                }
            };


    public void updateGuessRows(SharedPreferences sharedPreferences) {
        // Get the number of guess buttons that should be displayed
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        // Hide all guess button LinearLayouts
        for (LinearLayout layout : guessLinearLayouts)
            layout.setVisibility(View.GONE);

        // Display appropriate guess button LinearLayouts
        for (int row = 0; row < guessRows; row++)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    public void updateRegions(SharedPreferences sharedPreferences)
    {
       // regionsSet = sharedPreferences.getString(MainActivity.DZOOM, null);
       //    regionsSet = sharedPreferences.edit().putString(DZOOM, editTextPreference.getText()).apply();
    }

    public void initButtons(){
        // Set OnClickListeners methods.
        buttonSitioMexico = findViewById(R.id.buttonSitioMexico);
        buttonSitioMexico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(19.432608, -99.133208));
            }
        });
        buttonSitioTokio = findViewById(R.id.buttonSitioTokio);
        buttonSitioTokio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(35.689487, 139.691706));
            }
        });
        buttonSitioSydney = findViewById(R.id.buttonSitioSidney);
        buttonSitioSydney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(-33.868820, 151.209296));
            }
        });
        buttonSitioPekin = findViewById(R.id.buttonSitioPekin);
        buttonSitioPekin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(39.904200, 116.407396));
            }
        });
        buttonSitioMontreal = findViewById(R.id.buttonSitioMontreal);
        buttonSitioMontreal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(45.501689, -73.567256));
            }
        });
        buttonSitioParis = findViewById(R.id.buttonSitioParis);
        buttonSitioParis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(48.856614, 2.352222));
            }
        });
        buttonSitioRio = findViewById(R.id.buttonSitioRio);
        buttonSitioRio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(-22.906847, -43.172896));
            }
        });
        buttonSitioBerlin = findViewById(R.id.buttonSitioBerlin);
        buttonSitioBerlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(52.520007, 13.404954));
            }
        });
        buttonSitioNewYork = findViewById(R.id.buttonSitioNewYork);
        buttonSitioNewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(40.712775, -74.005973));
            }
        });
        buttonSitioRoma = findViewById(R.id.buttonSitioRoma);
        buttonSitioRoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsActivity.moveCamera(new LatLng(41.902783, 12.496366));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 10:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            timeUpdateLocation, distanceUpateLocation, this.mapsActivity);
                return;
        }
    }
}
