package com.sample.packnmovers.ui.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sample.packnmovers.R;
import com.sample.packnmovers.Utils;
import com.sample.packnmovers.model.PaymentData;

import io.realm.Realm;

public class PaymentFragment extends Fragment {


    EditText etCardHolder,etCardNo, etExpiry, etCvv;
    Button btnSave;
    private Realm realms;
    String sErrorMessage = "";
    boolean addData = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_payment, container, false);

        etCardHolder = (EditText) root.findViewById(R.id.etCardHolder);
        etCardNo = (EditText) root.findViewById(R.id.etCardNo);
        etExpiry = (EditText) root.findViewById(R.id.etExpiry);
        etCvv = (EditText) root.findViewById(R.id.etCvv);
        btnSave = (Button) root.findViewById(R.id.btnSave);


        Realm.init(getActivity());    //initialize to access database for this activity
        realms = Realm.getDefaultInstance();   //create a object for read and write database


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    updateRecords();
                    if ( addData) {
                        addData = false;
                        addProfile();
                    }
                    /*if (!realm.isEmpty()) {
                        //  addProfile();
                        updateRecords();
                    }else {
                        addProfile();
                        Utils.alertMessageOk(getActivity(),
                                "Message",
                                "Save successfully");
                    }*/
                    // getActivity().getSupportFragmentManager().popBackStack();
                }else {
                    Utils.alertMessageOk(getActivity(),
                            "Message",
                            sErrorMessage);
                }
            }
        });

        if (!realms.isEmpty()) {
            //  addProfile();
            readRecords();
        }

        return root;
    }

    private boolean isValid() {
        if (etCardHolder.length() == 0 || etCardNo.length() == 0
                || etExpiry.length() == 0 || etCvv.length() == 0) {
            sErrorMessage = getResources().getString(R.string.error_all_required);
            return false;
        } else if (etCardNo.length()>15) {
            sErrorMessage = getResources().getString(R.string.alert_invalid_card);
            return false;
        }
        return true;
    }

    public void addProfile(){

        realms.beginTransaction();

        PaymentData payment = realms.createObject(PaymentData.class);
        payment.setCardholdername(etCardHolder.getText().toString());
        payment.setCardno(etCardNo.getText().toString());
        payment.setExpiry(etExpiry.getText().toString());
        payment.setCvv(etCvv.getText().toString());
        payment.setId("1");

        realms.commitTransaction();
        Utils.alertMessageOk(getActivity(),
                "Message",
                "save successfully");
    }

    private void updateRecords() {


        realms.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                PaymentData payment = realm.where(PaymentData.class).equalTo("id", "1").findFirst();
                if (payment!=null) {
                    payment.setCardholdername(etCardHolder.getText().toString());
                    payment.setCardno(etCardNo.getText().toString());
                    payment.setExpiry(etExpiry.getText().toString());
                    payment.setCvv(etCvv.getText().toString());
                    payment.setId("1");
                    Utils.alertMessageOk(getActivity(),
                            "Message",
                            "Update successfully");
                }else{
                    addData = true;
                }

            }
        });


    }

    public void readRecords(){
        realms.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                PaymentData payment = realm.where(PaymentData.class).equalTo("id", "1").findFirst();
               if (payment!=null) {
                   etCardHolder.setText(payment.getCardholdername());
                   etCardNo.setText(payment.getCardno());
                   etExpiry.setText(payment.getExpiry());
                   etCvv.setText(payment.getCvv());
               }



            }
        });
    }

}