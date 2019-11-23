package com.sample.packnmovers.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.sample.packnmovers.CameraUtils;
import com.sample.packnmovers.R;
import com.sample.packnmovers.Utils;
import com.sample.packnmovers.model.ProfileData;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import io.realm.Realm;

import static android.app.Activity.RESULT_CANCELED;
import static com.sample.packnmovers.Utils.isEmailValidate;


public class ProfileFragment extends Fragment {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    private static String imageStoragePath;

    byte[] byteArray;

    ImageView ivTakeImg;


    private Realm realm;
    EditText etFirstName,etLastName,etPhone,etEmail,etPassword;
    Button btnSaveData;
    String sErrorMessage = "";
    boolean addData = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ivTakeImg = (ImageView) root.findViewById(R.id.ivTakeImg);
        etFirstName = (EditText) root.findViewById(R.id.etFirstName);
        etLastName = (EditText) root.findViewById(R.id.etLastName);
        etPhone = (EditText) root.findViewById(R.id.etPhone);
        etEmail = (EditText) root.findViewById(R.id.etEmail);
        etPassword = (EditText) root.findViewById(R.id.etPassword);
        btnSaveData= (Button) root.findViewById(R.id.btnSaveData);

        if (!CameraUtils.isDeviceSupportCamera(getActivity())) {
            Toast.makeText(getActivity(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device doesn't have camera
            getActivity().finish();
        }



        Realm.init(getActivity());    //initialize to access database for this activity
        realm = Realm.getDefaultInstance();   //create a object for read and write database


        if (!CameraUtils.isDeviceSupportCamera(getActivity())) {
            Toast.makeText(getActivity(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device doesn't have camera
            getActivity().finish();
        }
        ivTakeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraUtils.checkPermissions(getActivity())) {
                    captureImage();
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                }
            }
        });

        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    updateRecords();
                    if ( addData) {

                        addData = false;
                        addProfile();
                    }
                   /* if (!realm.isEmpty()) {
                        //  c
                        updateRecords();
                    }else {
                        addProfile();
                    }*/
                   // getActivity().getSupportFragmentManager().popBackStack();
                }else {
                    Utils.alertMessageOk(getActivity(),
                            "Message",
                            sErrorMessage);
                }
            }
        });


        realm = Realm.getDefaultInstance();
        if (!realm.isEmpty()) {
            //  addProfile();
            readRecords();
        }


        return root;
    }


    private boolean isValid() {
        if (etFirstName.length() == 0 || etLastName.length() == 0
                || etPhone.length() == 0 || etEmail.length() == 0
                || etPassword.length() == 0) {
            sErrorMessage = getResources().getString(R.string.error_all_required);
            return false;
        } else if (!isEmailValidate(etEmail.getText().toString())) {
            sErrorMessage = getResources().getString(R.string.alert_invalid_email);
            return false;
        }
        return true;
    }
    /*Real data base*/

    public void addProfile(){

        realm.beginTransaction();

        ProfileData person = realm.createObject(ProfileData.class);
        person.setFirstname(etFirstName.getText().toString());
        person.setLastname(etLastName.getText().toString());
        person.setPhone(etPhone.getText().toString());
        person.setEmail(etEmail.getText().toString());
        person.setPassword(etPassword.getText().toString());
        person.setImage(byteArray);
        person.setId("1");

        realm.commitTransaction();
        Utils.alertMessageOk(getActivity(),
                "Message",
                "save successfully");
        // }
    }

    private void updateRecords() {


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ProfileData person = realm.where(ProfileData.class).equalTo("id", "1").findFirst();
                if (person!=null) {
                    person.setFirstname(etFirstName.getText().toString());
                    person.setLastname(etLastName.getText().toString());
                    person.setPhone(etPhone.getText().toString());
                    person.setEmail(etEmail.getText().toString());
                    person.setPassword(etPassword.getText().toString());
                    person.setImage(byteArray);
                    person.setId("1");

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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                 ProfileData person = realm.where(ProfileData.class).equalTo("id", "1").findFirst();
                 if (person!=null) {
                     etFirstName.setText(person.getFirstname());
                     etLastName.setText(person.getLastname());
                     etPhone.setText(person.getPhone());
                     etEmail.setText(person.getEmail());
                     etPassword.setText(person.getPassword());
                    Log.e("imagen",""+person.getImage());
                    if (person.getImage()!=null){
                       // ivTakeImg.ByteArrayToBitmap();
                        ivTakeImg.setImageBitmap(ByteArrayToBitmap(person.getImage()));
                    }
                 }


            }
        });
    }

    /*take picture*/

    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    }
                }
            }
        }
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getActivity(), file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Saving stored image path to saved instance state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
  /*  @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }*/

    /**
     * Launching camera app to record video
     */
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getActivity(), file);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getActivity(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getActivity(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getActivity(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getActivity(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Display image from gallery
     */
    private void previewCapturedImage() {
        try {
            // hide video preview
          //  txtDescription.setVisibility(View.GONE);
         //   videoPreview.setVisibility(View.GONE);

         //   imgPreview.setVisibility(View.VISIBLE);

            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);

            convertImageIntoByte(bitmap);
            ivTakeImg.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(getActivity());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void convertImageIntoByte(Bitmap bmp){

      //  Bitmap bmp = intent.getExtras().get("data");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }

    public Bitmap ByteArrayToBitmap(byte[] byteArray)
    {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }
}