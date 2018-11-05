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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMyLocationButtonClickListener, CustomDialogClass.CustomDialogListener, ServerListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private UserClient userClient;

    private FloatingActionButton fabMenu, fabJoinGroup, fabListGroups;

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
        userClient = new UserClient("Axelandr", this);

    }

    private void initUIListeners() {

        fabMenu.setOnClickListener(v -> {
            fabMenu.setImageResource(R.drawable.close);
            if(fabJoinGroup.isShown()) {
                resetMenu();
            } else {
                fabJoinGroup.show();
                fabListGroups.show();
            }
        });

        fabJoinGroup.setOnClickListener(v -> {
            resetMenu();
            final PrettyDialog p = new PrettyDialog(v.getContext()).setTitle("Lägg till").setMessage("Vill du lägga till en grupp på denna positionen?");
            p.addButton("Lägg till grupp", R.color.colorWhite, R.color.colorGreen1, new PrettyDialogCallback() {
                @Override
                public void onClick() {
                    openGroupNameDialog();
                    p.dismiss();
                }
            });
            p.show();
        });

        fabListGroups.setOnClickListener(v -> {
            resetMenu();
            userClient.requestGroups();
        });

    }

    private void resetMenu() {
        fabJoinGroup.hide();
        fabListGroups.hide();
        fabMenu.setImageResource(R.drawable.openmenu);
    }

    private void openGroupNameDialog() {
        CustomDialogClass customDialogClass = new CustomDialogClass(this);
        customDialogClass.setTitle("Skriv in ditt gruppnamn");
        customDialogClass.show();
    }

    private void initUIComponents() {
        fabMenu = findViewById(R.id.fabMenu);
        fabJoinGroup = findViewById(R.id.fabJoinGroup);
        fabListGroups = findViewById(R.id.fabListGroups);
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


        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
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
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
        userClient.killConnection();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onRestart() {
        super.onRestart();
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 30000, 0, this);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 30000, 0, this);
        userClient.restoreConnection();
    }

    @Override
    public void editTextCallBack(String groupName) {
        fabJoinGroup.hide();
        fabListGroups.hide();
        fabMenu.setImageResource(R.drawable.openmenu);
        userClient.JSONMyGroup(groupName);
    }

    @Override
    public void groupClickedCallBack(String groupName) {
        userClient.JSONGetGroupMembers(groupName);
    }

    @Override
    public void joinGroupCallBack(String groupName) {
        userClient.JSONMyGroup(groupName);
    }

    public void leaveGroupCallBack(String groupName) {
        userClient.JSONLeaveGroup(groupName);
    }

    @Override
    public void serverCallback(JSONObject jsonObject) {

        runOnUiThread(() -> {
            try {
                String type = jsonObject.getString("type");
                switch (type) {
                    case "groups":
                        JSONArray groups = jsonObject.getJSONArray("groups");
                        String[] content = new String[groups.length()];

                        for (int i = 0; i < groups.length(); i++) {
                            JSONObject tempGroup = groups.getJSONObject(i);
                            content[i] = tempGroup.getString("group");
                        }

                        ListDialog listDialogGroups = new ListDialog(MapsActivity.this, content);
                        listDialogGroups.show();
                        listDialogGroups.setTitle("Active Groups");
                        break;

                    case "members":
                        JSONArray members = jsonObject.getJSONArray("members");
                        String groupName = jsonObject.getString("group");
                        String[] mContent = new String[members.length()];

                        for (int i = 0; i < members.length(); i++) {
                            JSONObject tempMember = members.getJSONObject(i);
                            mContent[i] = tempMember.getString("member");
                        }

                        ListDialog listDialogMembers = new ListDialog(MapsActivity.this, mContent);
                        listDialogMembers.show();
                        listDialogMembers.setTitle(groupName);
                        listDialogMembers.activateButton();
                        break;

                    case "locations":
                        mMap.clear();

                        JSONArray locations = jsonObject.getJSONArray("location");
                        LatLng tempLatLng;
                        for (int i = 0; i < locations.length(); i++) {
                            JSONObject tempMember = locations.getJSONObject(i);

                            double lat = Double.parseDouble(tempMember.getString("latitude"));
                            double lon = Double.parseDouble(tempMember.getString("longitude"));
                            tempLatLng = new LatLng(lat, lon);
                            mMap.addMarker(new MarkerOptions().position(tempLatLng).title(tempMember.getString("member")));
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
