//Author- Romit Mohane
package com.romitMohane.mathtrainer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements logoutDialog.logoutDialogListener {

    private Button btnEasy,btnMedium,btnHard,HTP,btnLeaderboard;
    public int diff;
    private ConstraintLayout main;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseUsers;

    Dialog usernamePopup,NetAlert;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAbout:
                showAbout();
                break;
            case R.id.itemLogout:
                openLogoutDialog();
                break;
            case R.id.itemUsername:
                takeUsername();
                break;
            case R.id.itemReconnect:
                if (isNetworkAvailable()) {
                    btnLeaderboard.setEnabled(true);
                    Toast.makeText(this, "Connected to Internet!", Toast.LENGTH_SHORT).show();
                }else{
                    showNetAlert();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openLogoutDialog() {
        logoutDialog dialog = new logoutDialog();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main=findViewById(R.id.main);

        HTP=findViewById(R.id.btnHowToPlay);
        btnLeaderboard=findViewById(R.id.btnLeaderboard);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseUsers= FirebaseDatabase.getInstance().getReference("users");

        if (firebaseUser==null){
            startActivity(new Intent(this,LoginOrRegister.class));
            finish();
        }
        getSupportActionBar().setSubtitle("Logged in as "+firebaseUser.getDisplayName());

        usernamePopup=new Dialog(this);

        HTP.setOnClickListener(v -> showHTP());

        btnLeaderboard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,Leaderboard.class)));

        btnEasy= findViewById(R.id.btnEasy);
        btnEasy.setOnClickListener(v -> {
            diff=1;
            Intent intent=new Intent(MainActivity.this,SecondActivity.class);
            intent.putExtra("difficulty",diff);
            startActivity(intent);
        });

        btnMedium= findViewById(R.id.btnMedium);
        btnMedium.setOnClickListener(v -> {
            diff=2;
            Intent intent=new Intent(MainActivity.this,SecondActivity.class);
            intent.putExtra("difficulty",diff);
            startActivity(intent);
        });

        btnHard= findViewById(R.id.btnHard);
        btnHard.setOnClickListener(v -> {
            diff=3;
            Intent intent=new Intent(MainActivity.this,SecondActivity.class);
            intent.putExtra("difficulty",diff);
            startActivity(intent);
        });

        if (!isNetworkAvailable()) showNetAlert();
    }

    private void showNetAlert() {
        NetAlert=new Dialog(this);
        NetAlert.setContentView(R.layout.internet_alert);
        Button cont=NetAlert.findViewById(R.id.btnContinue);
        Button exit=NetAlert.findViewById(R.id.btnExit);
        cont.setOnClickListener(v -> {
            NetAlert.dismiss();
            btnLeaderboard.setEnabled(false);
        });
        exit.setOnClickListener(v -> {
            NetAlert.dismiss();
            finish();
        });
        NetAlert.show();
        NetAlert.setCancelable(false);
        NetAlert.setCanceledOnTouchOutside(false);
        NetAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_options,menu);
        return true;
    }
    private void showAbout(){
        AboutBottomSheet about =new AboutBottomSheet();
        about.show(getSupportFragmentManager(),"About");
    }
    private void showHTP(){
        HowToPlayBottomSheet HTP=new HowToPlayBottomSheet();
        HTP.show(getSupportFragmentManager(),"HTP");
    }
    private void takeUsername(){
        usernamePopup.setContentView(R.layout.username_popup);
        TextView tvUsername=usernamePopup.findViewById(R.id.tvUsername);
        tvUsername.setText("Enter new Name");
        EditText etUsername = usernamePopup.findViewById(R.id.etUsername);
        Button btnUsername = usernamePopup.findViewById(R.id.btnUsername);
        btnUsername.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etUsername.getText().toString())){
                Toast.makeText(getApplicationContext(),"Enter a name!",Toast.LENGTH_SHORT).show();
                return;
            }
            String username = etUsername.getText().toString().trim();
            for (int i=0;i<username.length();i++){
                if (username.charAt(i)=='.'||username.charAt(i)=='$'||username.charAt(i)=='['||username.charAt(i)==']'||username.charAt(i)=='#'||username.charAt(i)=='/'){
                    Toast.makeText(getApplicationContext(),"Invalid Characters!",Toast.LENGTH_SHORT).show();
                    etUsername.setText("");
                    return;
                }
            }
            databaseUsers.child(firebaseUser.getUid()).child("username").setValue(username);

            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profile =new UserProfileChangeRequest.Builder()
                    .setDisplayName(username).build();

            user.updateProfile(profile)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            usernamePopup.dismiss();
                            Toast.makeText(getApplicationContext(),"Username changed Successfully!",Toast.LENGTH_SHORT).show();
                            getSupportActionBar().setSubtitle("Logged in as "+firebaseUser.getDisplayName());
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Invalid!",Toast.LENGTH_SHORT).show();
                    });
        });
        usernamePopup.show();
        usernamePopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        usernamePopup.setCanceledOnTouchOutside(false);
        usernamePopup.setCancelable(true);
    }
    @Override
    public void yesClicked() {
        firebaseAuth.signOut();
        startActivity(new Intent(this,LoginOrRegister.class));
        finish();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}