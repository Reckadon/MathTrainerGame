package com.romitMohane.mathtrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ListView lvUsers;

    ProgressDialog loading;

    List<User> userList;
    List<User> userListFinal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        lvUsers=(ListView) findViewById(R.id.LeaderboardList);
        userList=new ArrayList<>();
        userListFinal=new ArrayList<>();
        loading=new ProgressDialog(this);
        loading.setMessage("Loading..");
        loading.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading.dismiss();

                //clearing the previous artist list
                userList.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    User user = postSnapshot.getValue(User.class);
                    //adding artist to the list
                    userList.add(user);
                }
                //Collections.sort(userList, (User s1, User s2) -> s1.getMax().compareToIgnoreCase(s2.getMax()));
                for (int j=0;j<userList.size()-1;j++){
                    for (int i=0;i<userList.size()-1;i++){
                        if (Integer.parseInt(userList.get(i).getMax())<Integer.parseInt(userList.get(i+1).getMax())){
                            User t=userList.get(i);
                            userList.set(i,userList.get(i+1));
                            userList.set(i+1,t);
                        }
                        else if (Integer.parseInt(userList.get(i).getMax())==Integer.parseInt(userList.get(i+1).getMax())){
                            if (Integer.parseInt(userList.get(i).getTotal())<Integer.parseInt(userList.get(i+1).getTotal())){
                                User t=userList.get(i);
                                userList.set(i,userList.get(i+1));
                                userList.set(i+1,t);
                            }
                        }
                    }
                }

                //creating adapter
                UsersList adapter = new UsersList(Leaderboard.this, userList);
                //attaching adapter to the listview
                lvUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}