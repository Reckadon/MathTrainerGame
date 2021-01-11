package com.romitMohane.mathtrainer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class SecondActivity extends AppCompatActivity {

    private TextView tvTimer;
    private TextView tvPoints;
    private TextView tvHighScore;
    private Button btn;
    private ProgressBar pb;
    private TextView question;
    private EditText answer;
    private CardView quesCard;
    private int secondsRemaining=60;
    int diff,points=0,run=1,highScore;
    String ans,key="";
    private boolean gameRunning=false;

    private SharedPreferences offlineScores;

    private Dialog popup;

    private MediaPlayer player;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        popup=new Dialog(this);
        tvHighScore=findViewById(R.id.tvHighScore);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        Intent intent=getIntent();
        diff = intent.getIntExtra("difficulty", 0);
        switch (diff){
            case 1:
                getSupportActionBar().setTitle("Difficulty:Easy");
                key="eScore";
                break;
            case 2:
                getSupportActionBar().setTitle("Difficulty:Medium");
                key="mScore";
                break;
            case 3:
                getSupportActionBar().setTitle("Difficulty:Hard");
                key="hScore";
                break;
        }
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                highScore=Integer.parseInt(dataSnapshot.child(key).getValue().toString());
                tvHighScore.setText("Current High Score:"+highScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
            }
        });
        offlineScores=getSharedPreferences("offline",MODE_PRIVATE);
        SharedPreferences.Editor editor=offlineScores.edit();
        editor.putString(key,String.valueOf(highScore));
        editor.apply();
        if (!isNetworkAvailable()){
            getSupportActionBar().setSubtitle("No Internet");
            highScore=Integer.parseInt(offlineScores.getString(key, String.valueOf(0)));
            tvHighScore.setText("Current High Score:"+highScore);
        }

        tvPoints=findViewById(R.id.tvPoints);
        answer=findViewById(R.id.etAnswer);
        quesCard=findViewById(R.id.crdQuestion);
        question=findViewById(R.id.tvQuestion);
        tvTimer=findViewById(R.id.tvTimer);
        pb= findViewById(R.id.pbTimer);
        btn= findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameRunning &&btn.getText().equals("START")) {
                    pb.setProgress(60);
                    btn.setEnabled(false);
                    CountDownTimer timer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            secondsRemaining = (int) millisUntilFinished / 1000;
                            tvTimer.setText(String.valueOf(secondsRemaining));
                            pb.incrementProgressBy(-1);
                            gameRunning = true;
                        }

                        @Override
                        public void onFinish() {
                            tvTimer.setText("Time's Up!!");
                            gameRunning = false;
                            btn.setEnabled(true);
                            btn.setText("Reset");
                            quesCard.setClickable(false);
                            answer.setEnabled(false);
                            checkHighScore();
                            playSound();
                        }
                    }.start();
                    quesCard.setClickable(true);
                    quesCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            gameOn();
                        }
                    });
                    answer.setEnabled(true);
                    gameOn();
                }
                else{
                    pb.setProgress(60);
                    btn.setText("START");
                    tvTimer.setText("60");
                    question.setText("");
                    answer.setText("");
                    answer.setHint("");
                    tvPoints.setText("Points:0");
                    points=0;
                    run=1;
                }

            }
        });
    }

    private void gameOn() {
        if (answer.getText().toString().equals(ans) || run == 1) {
            Random rand=new Random();
            answer.setText("");
            answer.setHint("");
            int x=0,y=0,val=0;
            int[] ques;
            String operator="";
            int op=rand.nextInt(4);
            switch(diff) {
                case 1:
                    if (op<=1) {
                        operator = " + ";
                        x = generateA(diff);
                        y = generateA(diff);
                        ans = String.valueOf(x + y);
                    } else{
                        operator = " - ";
                        ques=generateS(diff);
                        ans = String.valueOf(ques[0] - ques[1]);
                        x=ques[0];
                        y=ques[1];
                    }
                    val = 10;
                    break;
                case 2:
                    if (op<=1) {
                        operator = " + ";
                        x = generateA(diff);
                        y = generateA(diff);
                        ans = String.valueOf(x + y);
                    } else if (op==2){
                        operator = " x ";
                        x=generateM(diff);
                        y=generateM(diff);
                        ans = String.valueOf(x * y);
                    }else if (op==3){
                        operator = " - ";
                        ques=generateS(diff);
                        ans = String.valueOf(ques[0] - ques[1]);
                        x=ques[0];
                        y=ques[1];
                    }
                    val=20;
                    break;
                case 3:
                    if (op<=1) {
                        operator = " + ";
                        x = generateA(diff);
                        y = generateA(diff);
                        ans = String.valueOf(x + y);
                    } else if (op==2){
                        operator = " x ";
                        x=generateM(diff);
                        y=generateM(diff);
                        ans = String.valueOf(x * y);
                    }else if (op==3) {
                        operator = " - ";
                        ques=generateS(diff);
                        ans = String.valueOf(ques[0] - ques[1]);
                        x=ques[0];
                        y=ques[1];
                    }
                    val=30;
                    break;
            }
            question.setText(x + operator + y);
            if (run != 1) {
                points += val;
                tvPoints.setText("Points:" + points);
            }
            if (run == 1)
                run++;
        } else if (answer.getText().toString().equals(""))
            answer.setHint("Enter a number");
        else {
            answer.setText("");
            answer.setHint("Wrong!");
        }
    }

    private void checkHighScore(){
        if (points>highScore){
            int t=highScore;
            highScore=points;
            showPopup(t);
            UpdateScore();
        }else{
            showPopup();
        }
    }

    public int generateA(int dif) {
        Random rand=new Random();
        int num=0;
        switch (dif) {
            case 1:
                num= rand.nextInt(101);
                break;
            case 2:
                num=rand.nextInt(501);
                break;
            case 3:
                num=rand.nextInt(1001);
                break;
        }
        return num;
    }
    public int generateM(int dif) {
        Random rand=new Random();
        int num=0;
        switch (dif) {
            case 2:
                num = rand.nextInt(16);
                break;
            case 3:
                num=rand.nextInt(51);
                break;
        }
        return num;
    }
    public int[] generateS(int dif){
        Random rand =new Random();
        int arr[]=new int[2];
        switch (dif) {
            case 1:
                arr[0]= rand.nextInt(101);
                arr[1]=rand.nextInt(101);
                break;
            case 2:
                arr[0]= rand.nextInt(501);
                arr[1]=rand.nextInt(501);
                break;
            case 3:
                arr[0]= rand.nextInt(1001);
                arr[1]=rand.nextInt(1001);
                break;
        }
        if (arr[1]>arr[0]){
            int t=arr[1];
            arr[1]=arr[0];
            arr[0]=t;
        }
        return arr;
    }
    int eScore,mScore,hScore,max,total;
    private void UpdateScore(){
        databaseUser.child(key).setValue(String.valueOf(highScore));
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eScore=Integer.parseInt(dataSnapshot.child("eScore").getValue().toString());
                mScore=Integer.parseInt(dataSnapshot.child("mScore").getValue().toString());
                hScore=Integer.parseInt(dataSnapshot.child("hScore").getValue().toString());
                max=Math.max(eScore,Math.max(mScore,highScore));
                total=eScore+mScore+hScore;
                databaseUser.child("max").setValue(String.valueOf(max));
                databaseUser.child("total").setValue(String.valueOf(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences.Editor editor=offlineScores.edit();
        editor.putString(key,String.valueOf(highScore));
        editor.apply();
    }

    public void showPopup(int old){
        TextView prev;
        TextView neww;
        Button okay;
        popup.setContentView(R.layout.highscore_popup);
        prev=popup.findViewById(R.id.tvPrev);
        neww=popup.findViewById(R.id.tvNew);
        prev.setText("Previous HighScore="+old);
        neww.setText("New HighScore="+highScore);
        okay=popup.findViewById(R.id.btnOkay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                stopPlayer();
            }
        });
        popup.setCanceledOnTouchOutside(false);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }
    public void showPopup(){
        TextView prev;
        TextView neww;
        TextView title;
        Button okay;
        popup.setContentView(R.layout.highscore_popup);
        title=popup.findViewById(R.id.tvCongrats);
        prev=popup.findViewById(R.id.tvPrev);
        neww=popup.findViewById(R.id.tvNew);
        title.setText("Try Harder!!");
        prev.setText("Current HighScore="+highScore);
        neww.setText("Your Points="+points);
        okay=popup.findViewById(R.id.btnOkay);
        okay.setText("okay");
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                stopPlayer();
            }
        });
        popup.setCanceledOnTouchOutside(false);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();
    }
    public void playSound(){
        if (player==null){
            player=MediaPlayer.create(this,R.raw.time_over);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }
    public void stopPlayer(){
        if(player!=null){
            player.release();
            player=null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}