package com.example.bisu_button;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button btnConfirm;
    private Button btnSubmitCode;
    private TextView textViewAvailability;
    private EditText editTextReserveCode;
    private String codeReserve = "No Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setVisibility(View.INVISIBLE);


        // Get a reference to the node you want to check
        DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference().child("Room").child("Room1");
        nodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Reserve").exists()) {
                    Log.d(TAG, "Node exists!");
                    // Do something if the node exists

                } else {
                    // Do something if the node doesn't exist
                    Log.d(TAG, "Node does not exist!");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur
                Log.e(TAG, "Error checking if node exists: " + databaseError.getMessage());
            }
        });

        //Button that compares the code and the one on the database
        Button btnSubmitCode = findViewById(R.id.btnSubmitCode);
        EditText editTextReserveCode = findViewById(R.id.editTextReserveCode);

        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reserveCode = editTextReserveCode.getText().toString().trim();
                fetchData(new OnDataFetchedListener() {
                    @Override
                    public void onDataFetched(String data) {
                        if (reserveCode.equals(data)) {
                            // code and reserveCode match, do something
                            Log.d(TAG, "Code is a match");
                            btnConfirm.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Code Verified. Please click CONFIRM to start using the Room", Toast.LENGTH_LONG).show();
                        } else {
                            // code and reserveCode do not match, do something else
                            Log.d(TAG, "Code is a mismatch");
                            Toast.makeText(MainActivity.this, "ERROR CODE", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onDataFetchFailed(String errorMessage) {
                        // handle data fetch failure
                    }
                });
            }
        });




        //Set the text of Status
        DatabaseReference roomAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Room").child("Room1").child("Availability");
        textViewAvailability = findViewById(R.id.textViewAvailability);

        //Listens for changes in the "Availability" Node
        //Changes the Visibility of the Confirm button according to the value of Availablity node
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
                        btnConfirm.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        roomAvailabilityRef.addValueEventListener(availabilityListener);
        //User clicks the button to confirm that they will now start using the room
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

    //Methods here

    //fetches the ReserveCode in the Reserve Node and returns the value
    public void fetchData(OnDataFetchedListener listener) {

        String room = "Room1"; //CHANGE THIS ACCORDING TO THE ROOM THAT YOU WANT TO SET UP

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Room").child(room).child("Reserve");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String reserveCode = (String) dataSnapshot.child("Reserve1").child("ReserveCode").getValue();
                Log.d(TAG, "Code is: " + reserveCode);
                listener.onDataFetched(reserveCode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error fetching data: " + databaseError.getMessage());
                listener.onDataFetchFailed(databaseError.getMessage());
            }
        });
    }

    public interface OnDataFetchedListener {
        void onDataFetched(String data);
        void onDataFetchFailed(String errorMessage);
    }




    //Checks the code, if it is correct, then the confirm button will emerge
    private void compareStrings() {

    }






}
