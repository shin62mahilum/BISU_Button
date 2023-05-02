package com.example.bisu_button;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bisu_button.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button btnConfirm;
    private TextView textViewAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnConfirm = findViewById(R.id.btnConfirm);

        //Set the text of Status

        DatabaseReference roomAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Room").child("Room1").child("Availability");
        textViewAvailability = findViewById(R.id.textViewAvailability);

        ValueEventListener availabilityListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean availability = dataSnapshot.getValue(Boolean.class);
                if (availability != null) {
                    if (availability) {
                        textViewAvailability.setText("Vacant");
                        textViewAvailability.setTextColor(Color.GREEN);
                    } else {
                        textViewAvailability.setText("Occupied");
                        textViewAvailability.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        roomAvailabilityRef.addValueEventListener(availabilityListener);


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference roomAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Room").child("Room1").child("Availability");

                roomAvailabilityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override


                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean availability = dataSnapshot.getValue(Boolean.class);
                        if (availability == null) {
                            // If the availability node does not exist or is not a boolean value, assume it is false
                            availability = false;
                        }
                        roomAvailabilityRef.setValue(!availability);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });


            }
        });

    }
}
