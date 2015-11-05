package com.example.sergbek.socialnetworks.facebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergbek.socialnetworks.BuildConfig;
import com.example.sergbek.socialnetworks.R;
import com.example.sergbek.socialnetworks.global.Constants;
import com.example.sergbek.socialnetworks.interfaces.PublisherActionListener;
import com.example.sergbek.socialnetworks.interfaces.GetTaskCallbackInterface;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

public class FacebookFragment extends Fragment implements View.OnClickListener, PublisherActionListener {

    private LoginButton mLoginButton;
    private TextView mTvAboutMe;
    private ImageView mIvPhotoUser;
    private Button mBtnSendText;
    private Button mBtnSendPhoto;
    private EditText mEdTextMessage;
    private ProgressBar mPbSendPost;
    private OnAddAndRemoveListener mOnAddAndRemoveListener;
    private GetTaskCallbackInterface mGetTaskCallbackInterface;

    private CallbackManager mCallbackManager;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddAndRemoveListener) {
            this.mOnAddAndRemoveListener = (OnAddAndRemoveListener) context;
            this.mGetTaskCallbackInterface = (GetTaskCallbackInterface) context;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.mOnAddAndRemoveListener = (OnAddAndRemoveListener) activity;
            this.mGetTaskCallbackInterface = (GetTaskCallbackInterface) activity;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        defineComponents(view);
        setListeners();
        mLoginButton.setPublishPermissions("publish_actions");

        if (AccessToken.getCurrentAccessToken() != null)
            setInfo(Profile.getCurrentProfile());

        if (mGetTaskCallbackInterface != null)
            mLoginButton.registerCallback(mCallbackManager,
                    mGetTaskCallbackInterface.getFacebookCallback());

    }

    private void defineComponents(View view) {
        mTvAboutMe = (TextView) view.findViewById(R.id.tv_aboutMe_FF);
        mIvPhotoUser = (ImageView) view.findViewById(R.id.iv_photoUser_FF);
        mLoginButton = (LoginButton) view.findViewById(R.id.btn_login_FF);
        mBtnSendText = (Button) view.findViewById(R.id.btn_sendText_FF);
        mBtnSendPhoto = (Button) view.findViewById(R.id.btn_sendPhoto_FF);
        mEdTextMessage = (EditText) view.findViewById(R.id.ed_textMessage_FF);
        mPbSendPost = (ProgressBar) view.findViewById(R.id.pb_sendPost_FF);
    }

    private void setListeners() {
        mBtnSendText.setOnClickListener(this);
        mBtnSendPhoto.setOnClickListener(this);
    }

    private void setInfo(Profile profile) {
        mTvAboutMe.setText("Name: " + profile.getName() + "\n"
                + "ID: " + profile.getId());

        Picasso.with(getActivity())
                .load((profile.getProfilePictureUri(250, 250)))
                .into(mIvPhotoUser);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mOnAddAndRemoveListener != null)
            mOnAddAndRemoveListener.onStartAddListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getActivity().getApplicationContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mOnAddAndRemoveListener != null)
            mOnAddAndRemoveListener.onStartRemoveListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAddAndRemoveListener = null;
        mGetTaskCallbackInterface = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CODE_GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            mPbSendPost.setVisibility(View.VISIBLE);
            Bundle params = new Bundle();
            params.putString("message", mEdTextMessage.getText().toString());

            if (mGetTaskCallbackInterface != null) {
                try {
                    GraphRequest.newUploadPhotoRequest(
                            AccessToken.getCurrentAccessToken(),
                            "me/photos",
                            data.getData(),
                            "Photo Users",
                            params,
                            mGetTaskCallbackInterface.getGraphRequestCallbackPhoto())
                            .executeAsync();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSendText) {
            mPbSendPost.setVisibility(View.VISIBLE);
            if (mGetTaskCallbackInterface != null) {
                Bundle params = new Bundle();
                params.putString("message", mEdTextMessage.getText().toString());
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        params,
                        HttpMethod.POST,
                        mGetTaskCallbackInterface.getGraphRequestCallbackText())
                        .executeAsync();
            }

        } else if (v == mBtnSendPhoto)
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.
                    INTERNAL_CONTENT_URI), Constants.CODE_GET_FROM_GALLERY);
    }

    @Override
    public void doAction(GraphResponse response) {
        mPbSendPost.setVisibility(View.INVISIBLE);
        mEdTextMessage.setText("");
        Toast.makeText(getActivity(), "Post published", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void doAction(LoginResult loginResult) {

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                this.stopTracking();
                Profile.setCurrentProfile(currentProfile);
                setInfo(Profile.getCurrentProfile());
            }
        };
        profileTracker.startTracking();
    }

    public interface OnAddAndRemoveListener {
        void onStartAddListener(PublisherActionListener listener);

        void onStartRemoveListener(PublisherActionListener listener);
    }
}
