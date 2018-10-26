package se.mau.ag2656.p2worldandfriends;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONObject;


import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapLongClickListener, CustomDialogClass.CustomDialogListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private UserClient userClient;

    private FloatingActionButton fabMain, fabSecond;
    private EditText etGroupName;
    private RelativeLayout rL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initUIComponents();
        initUIListeners();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

         // Makes sure permission is all good before anything about permissions is runned.

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }
        userClient = new UserClient("Axelandr");

    }

    private void initUIListeners() {

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMain.setImageResource(R.drawable.close);
                if(fabSecond.isShown()) {
                    fabSecond.hide();
                    fabMain.setImageResource(R.drawable.openmenu);
                } else {
                    fabSecond.show();
                }
            }
        });

        fabSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PrettyDialog p = new PrettyDialog(v.getContext()).setTitle("Lägg till").setMessage("Vill du lägga till en grupp på denna positionen?");
                p.addButton("Lägg till grupp", R.color.colorWhite, R.color.colorGreenLight, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        Log.d("REE", "onClick: 3213211");
                        openDialog();
                        p.dismiss();
                    }
                });
                p.show();
            }
        });

        etGroupName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    userClient.JSONMyGroup(etGroupName.getText().toString());
//                    rL.setVisibility(View.INVISIBLE);
                    rL.setBackgroundResource(R.drawable.borderseethru);
                    etGroupName.setVisibility(View.INVISIBLE);


                    //closing keyboard on enter
                    InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etGroupName.getWindowToken(), 0);

                }
                return false;
            }
        });

    }

    private void openDialog() {
        CustomDialogClass customDialogClass = new CustomDialogClass(this);
        customDialogClass.setTitle("Skriv in ditt gruppnamn");
        customDialogClass.show();
    }

    private void initUIComponents() {
        fabMain = findViewById(R.id.fabMain);
        fabSecond = findViewById(R.id.fabTwo);
        rL = findViewById(R.id.rL);
        etGroupName = findViewById(R.id.etGroupName);
    }

    private void coordinates(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        LatLng latLng = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker on my position"));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 30000, 0, this);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 30000, 0, this);


        mMap.setOnMapLongClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);



        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userClient.JSONMyLocation(location);
        System.out.println("### " + "sent location to server" +" ###");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        new PrettyDialog(this).setTitle("Lägg till").setMessage("Vill du lägga till en grupp på denna positionen?").show();
        mMap.addMarker(new MarkerOptions()
                .position(latLng));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "register");
                    jsonObject.put("group", "Axelandr Group");
                    jsonObject.put("member", "Juan Carlos");
                    userClient.sendToServer(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "register");
                    jsonObject.put("group", "Rajnfeldt Group");
                    jsonObject.put("member", "Juan Carlos");
                    userClient.sendToServer(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        userClient.killConnection();
    }


    @Override
    public void returnText(String groupName) {

    }
}
