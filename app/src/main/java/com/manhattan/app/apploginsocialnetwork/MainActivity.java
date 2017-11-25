package com.manhattan.app.apploginsocialnetwork;

import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private TwitterLoginButton login_main;
    private LoginButton login_facebook;
    private CallbackManager callbackManager;
    private Button buttonDialog;
    private BottomSheetDialog bottonSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);


        callbackManager = CallbackManager.Factory.create();
        login_main = (TwitterLoginButton) findViewById(R.id.login_main);
        login_facebook = findViewById(R.id.login_facebook);
        buttonDialog = findViewById(R.id.button_dialog);


        showCustomDialog();
        loginTwitter();
        loginFacebook();

    }

    private void showCustomDialog() {
        View view = getLayoutInflater().inflate(R.layout.bootomshit, null);
        bottonSheetDialog = new BottomSheetDialog(this);
        bottonSheetDialog.setContentView(view);
        buttonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottonSheetDialog.show();
            }
        });
    }

    private void loginFacebook() {
        login_facebook.setReadPermissions("public_profile", "email");
        login_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name  = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //Especificar que información del perfil quieres especificamente
                Bundle bundle = new Bundle();
                bundle.putString("fields", "name, email, birthday, picture.type(large)");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void loginTwitter(){
        login_main.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                //validar si la session esta activa == si esta activa validar que no vuelva a logearse
                Call<User> userResult = TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(true,true,true);

                userResult.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        Toast.makeText(MainActivity.this, ":: OK ::", Toast.LENGTH_LONG).show();
                        //String description = result.data.description;
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        login_main.onActivityResult(requestCode, resultCode, data);
    }
}
