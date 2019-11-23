package com.sample.packnmovers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    String sErrorMessage = "";
    EditText etUser, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    btnLogin = (Button) findViewById(R.id.btnLogin);
    etUser = (EditText) findViewById(R.id.etUser);
    etPassword = (EditText) findViewById(R.id.etPassword);
    btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isValid()) {
                Intent i = new Intent(LoginActivity.this, DashBoardActivity.class);
                startActivity(i);
            }else {
                    Utils.alertMessageOk(LoginActivity.this,
                            "Message",
                            sErrorMessage);

            }
        }
    });
    }


    private boolean isValid() {
        if (etUser.length() == 0 || etPassword.length() == 0
                ) {
            sErrorMessage = getResources().getString(R.string.error_all_required);
            return false;
        }
        return true;
    }

}
