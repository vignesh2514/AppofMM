package com.motomecha.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.motomecha.app.dbhandler.SQLiteHandler;
import com.motomecha.app.dbhandler.SessionManager;

import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class OtpActivity extends AppCompatActivity implements OTPListener {
String Sotpnum,Sotpt,otp,getpass,mobile_number,name,email,address,uid,cfd="0";

    EditText Eotp_check;
    ImageButton Iverify;
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        try {
            mobile_number = getIntent().getStringExtra("mobile_number");
            otp = getIntent().getStringExtra("otp");
            getpass = getIntent().getStringExtra("getpass");
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            address = getIntent().getStringExtra("address");
            uid = getIntent().getStringExtra("uid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Eotp_check=(EditText) findViewById(R.id.otp_text);
        Iverify=(ImageButton) findViewById(R.id.verify_otp);
        Iverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sotpt=Eotp_check.getText().toString();
                if (Sotpt.isEmpty())
                {
                    Toast.makeText(OtpActivity.this,"Please enter your OTP ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(otp.equals(Sotpnum)&&getpass.equals(cfd)){
                        Intent intent = new Intent(OtpActivity.this, ResgisterActivity.class);
                        intent.putExtra("mobile_number",mobile_number);
                        intent.putExtra("otp",otp);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        session.setLogin(true);
                        db.addUser(name, email, uid, mobile_number);
                        Intent intent = new Intent(OtpActivity.this, BasicActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

            }
        });
        OtpReader.bind(this,"MMECHA");

    }
    @Override
    public void otpReceived(String smsText) {
        String[] parts = smsText.split(":"); // escape .
        String part1 = parts[0];
        String part2 = parts[1];
        String[] parts1=part2.split("\\.");
        Sotpnum=parts1[0];
        Eotp_check.setText(Sotpnum);
        if(otp.equals(Sotpnum)&&getpass.contains(cfd)){
            Intent intent = new Intent(OtpActivity.this, ResgisterActivity.class);
            intent.putExtra("mobile_number",mobile_number);
            intent.putExtra("otp",otp);
            startActivity(intent);
            finish();
        }
        else
        {
            session.setLogin(true);
            db.addUser(name, email, uid, mobile_number);
            Intent intent = new Intent(OtpActivity.this, BasicActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
