package com.sample.packnmovers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.sample.packnmovers.Utils.isEmailValidate;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etUser, etPhone, etEmail,etPassword,etConfirmPass;
    String sErrorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etUser = (EditText) findViewById(R.id.etUser);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPass = (EditText) findViewById(R.id.etConfirmPass);

        btnRegister.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isValid()) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }else {
                Utils.alertMessageOk(RegisterActivity.this,
                        "Message",
                        sErrorMessage);
            }
        }
    });
    }

    private boolean isValid() {
        if (etUser.length() == 0 || etPhone.length() == 0
                || etEmail.length() == 0 || etPassword.length() == 0
                || etConfirmPass.length() == 0) {
            sErrorMessage = getResources().getString(R.string.error_all_required);
            return false;
        } else if (!isEmailValidate(etEmail.getText().toString())) {
            sErrorMessage = getResources().getString(R.string.alert_invalid_email);
            return false;
        } else if (!etPassword.getText().toString().equals(etConfirmPass.getText().toString())) {
            sErrorMessage = getResources().getString(R.string.error_password_mismatch);
            return false;
        }
        return true;
    }
}
