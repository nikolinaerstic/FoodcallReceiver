package com.example.nikolina.restaurantappreceiver;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> orders;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceForOrders;
    private PendingIntent pendingIntent;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new GoogleApiClient.Builder(this).addApi(Awareness.API).build();
        client.connect();



        Intent intent = new Intent(this, FenceReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);



        orders = new ArrayList<>();
        listView = findViewById(R.id.listView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("orders");
        databaseReferenceForOrders = FirebaseDatabase.getInstance().getReference().child("orderHistory");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cart cart = dataSnapshot.getValue(Cart.class);
                if (cart != null){
                    databaseReference.removeValue();
                    orders.add("Ime: " + cart.getName() + "\n" +
                            "Prezime: " + cart.getSurname() + "\n" +
                            "Adresa: " + cart.getAddress() + "\n" +
                            "Narudžba: " + cart.getOrder() + "\n" +
                            "Za platit: " + cart.getPrice());
                    ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, orders);
                    listView.setAdapter(adapter);

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(MainActivity.this, "Uključite vašu lokaciju.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AwarenessFence locationFence = LocationFence.in(cart.getLat(), cart.getLng(), 100, 1);
                    String randomId = UUID.randomUUID().toString();
                    registerFence(randomId, locationFence);
                    databaseReferenceForOrders.child(randomId).setValue(cart);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void registerFence(String fenceName, AwarenessFence fence) {
        Awareness.FenceApi.updateFences(client, new FenceUpdateRequest.Builder().addFence(fenceName, fence, pendingIntent)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
            }

            @Override
            public void onFailure(@NonNull Status status) {
            }
        });
    }

}
