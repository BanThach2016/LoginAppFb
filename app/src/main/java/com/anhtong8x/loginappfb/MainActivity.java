package com.anhtong8x.loginappfb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ProfilePictureView profilePictureView;
    LoginButton loginButton;
    Button btlLogout, btlFunc;
    TextView txtName, txtEmail, txtFirstName;
    String strEmail, strName, strFirstName;

    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

        // goi ham lay keyhash lan dau de dang ky vao facebook

        // init view
        initView();

        // hidden view
        btlFunc.setVisibility(View.INVISIBLE);
        btlLogout.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        txtFirstName.setVisibility(View.INVISIBLE);
        txtName.setVisibility(View.INVISIBLE);

        // set permission loginButton facebook
        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        setLoginButton();



        btlFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SubActivity.class));
            }
        });

        btlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogoutButton();
            }
        });
    }

    private void setLogoutButton() {
        LoginManager.getInstance().logOut();

        btlFunc.setVisibility(View.INVISIBLE);
        btlLogout.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        txtFirstName.setVisibility(View.INVISIBLE);
        txtName.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.VISIBLE);

        txtEmail.setText("");
        txtFirstName.setText("");
        txtName.setText("");
        profilePictureView.setProfileId(null);

    }

    private void setLoginButton() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.INVISIBLE);
                btlFunc.setVisibility(View.VISIBLE);
                btlLogout.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtFirstName.setVisibility(View.VISIBLE);
                txtName.setVisibility(View.VISIBLE);

                resultInfo();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void resultInfo() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("Json", response.getJSONObject().toString());

                try {
                    strEmail = object.getString("email").toString();
                    txtFirstName.setText(object.getString("first_name"));
                    txtName.setText(object.getString("name"));

                    profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
                    //Picasso.with(MainActivity.this).load(Profile.getCurrentProfile().getLinkUri()).into(profilePictureView);
                   /* ImageView imgFb =  findViewById(R.id.imgViewFb);
                    Picasso.with(MainActivity.this)
                            .load("https://graph.facebook.com/" + Profile.getCurrentProfile().getId()+ "/picture?type=large")
                            .into(imgFb);*/


                    Log.d("id", Profile.getCurrentProfile().getId());
                    //profilePictureView.setProfileId(object.getString("id"));
                    txtEmail.setText(strEmail);

                }catch (JSONException e){
                    Log.d("Error", e.toString());
                }

            }
        });

        Bundle parameter = new Bundle();
        parameter.putString("fields", "name, email, first_name");
        graphRequest.setParameters(parameter);
        graphRequest.executeAsync( );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        // moi lan mo app se bat login facebook
        LoginManager.getInstance().logOut();
        super.onStart();
    }

    private void initView() {
        profilePictureView = (ProfilePictureView)findViewById(R.id.imageProfilePicture);
        loginButton = findViewById(R.id.login_button);
        btlLogout = findViewById(R.id.btn_logout);
        btlFunc = findViewById(R.id.btn_func);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtFirstName = findViewById(R.id.txtFirstName);
    }

    // lay keyhash facebook
    void getKeyhash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.anhtong8x.loginappfb",                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}