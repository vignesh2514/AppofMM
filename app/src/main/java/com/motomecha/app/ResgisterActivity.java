package com.motomecha.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.motomecha.app.dbhandler.SQLiteHandler;
import com.motomecha.app.dbhandler.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ResgisterActivity extends AppCompatActivity {
String Sname,Semail,Saddress,mobile_number,otp;
    EditText Ename,Eemail,Eaddress;
    ImageButton Iproceed;
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
        Ename=(EditText) findViewById(R.id.name);
        Eemail=(EditText) findViewById(R.id.email);
        Eaddress=(EditText) findViewById(R.id.address);
        Iproceed=(ImageButton) findViewById(R.id.login_continue);
        mobile_number = getIntent().getStringExtra("mobile_number");
        otp = getIntent().getStringExtra("otp");
String email=null;
        Pattern gmailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches())
            {
                email = account.name;
            }
        }
        Eemail.setText(email);

        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
Iproceed.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Sname=Ename.getText().toString();
        Semail=Eemail.getText().toString();
        Saddress=Eaddress.getText().toString();

        if (Sname.isEmpty()||Semail.isEmpty()||Saddress.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter all fields",Toast.LENGTH_SHORT).show();
        }
        else
        {
registerme(Sname,Semail,Saddress,mobile_number,otp);
        }
    }
});

    }
public  void registerme(final String sname, final String semail, final String saddress, final String mobile_number, final String otp)
{
    StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrlInit.SIGN_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean success = jObj.getBoolean("exits");
                if (success) {
                    session.setLogin(true);
                    JSONObject user = jObj.getJSONObject("users");
                    String mobile_number = user.getString("mobile_number");
                    String name =user.getString("name");
                    String email   =user.getString("email");
                    String uid =user.getString("uid");
                    db.addUser(name, email, uid,mobile_number);
                    Intent intent = new Intent(ResgisterActivity.this, BasicActivity.class);
                    startActivity(intent);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }){
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
          Map<String,String> params = new HashMap<String, String>();
            params.put("mobile_number",mobile_number);
            params.put("otp",otp);
            params.put("name",sname);
            params.put("email",semail);
            params.put("address",saddress);
            return params;
        }
    };
    AppController.getInstance().addToRequestQueue(stringRequest);
}
}
