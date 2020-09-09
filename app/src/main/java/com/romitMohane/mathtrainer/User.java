package com.romitMohane.mathtrainer;

public class User {

    String email,username,eScore,mScore,hScore,max,total;

    public User(){

    }

    public User(String email,String username, String  eScore, String mScore, String hScore,String max,String total) {
        this.email = email;
        this.username=username;
        this.eScore = eScore;
        this.mScore = mScore;
        this.hScore = hScore;
        this.max=max;
        this.total=total;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String geteScore() {
        return String.valueOf(eScore);
    }

    public String getmScore() {
        return String.valueOf(mScore);
    }

    public String gethScore() {
        return String.valueOf(hScore);
    }
    public String getMax(){
        return String.valueOf(max);
    }
    public String getTotal(){
        return String.valueOf(total);
    }
}
