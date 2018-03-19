package com.codewarriors.hackathone.relaypension;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewarriors.hackathone.relaypension.customvariablesforparsing.ConsituencyCustomVAR;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StatusActivity extends AppCompatActivity {
    TextView name,aadharno,consituency,applictionstatus;
    ImageView pic;

    DatabaseReference mDB;
    String aadharnost;//="499240755287";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        name=findViewById(R.id.statusnametv);
        aadharno=findViewById(R.id.aadharnostatustv);
        consituency=findViewById(R.id.consituencystatustv);
        applictionstatus=findViewById(R.id.applicationstatustv);
        pic=findViewById(R.id.statuspic);

        SharedPreferences prefs = getSharedPreferences("codewarriors", MODE_PRIVATE);
        String restoredText = prefs.getString("userid", null);

        if(restoredText!=null)
        {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("UserState/");
            rootRef.child(restoredText).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        aadharnost= dataSnapshot.getValue().toString();
                        mDB= FirebaseDatabase.getInstance().getReference("userstatecons/"+aadharnost);

                        StorageReference storageRef =
                                FirebaseStorage.getInstance().getReference();
                        storageRef.child(aadharnost+"/userpic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(StatusActivity.this).load(uri).into(pic);

                            }
                        });

                        mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ConsituencyCustomVAR consituencyCustomVAR=dataSnapshot.getValue(ConsituencyCustomVAR.class);
                                name.setText(consituencyCustomVAR.getUsername());
                                aadharno.setText(consituencyCustomVAR.getAadharno());
                                consituency.setText(consituencyCustomVAR.getConsituency());
                                setstatus(consituencyCustomVAR.getApplictionstate());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mDB.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                if(dataSnapshot.exists())
                                {
                                    String consituencyCustomVAR=dataSnapshot.getValue().toString();
                                    setstatus(consituencyCustomVAR);

                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else
                    {
                        Log.d("error in STATUSACTIVITY","datasnapshot empty");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    public void setstatus(String consitu)
    {
        switch (consitu)
        {
            case "0":
            {
                applictionstatus.setText("IN QUEUE");
                break;
            }
            case "1":
            {
                applictionstatus.setText("IN READY");
                break;
            }
            default:
            {
                applictionstatus.setText("Cancelled");

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
