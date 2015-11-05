package com.example.sergbek.socialnetworks.facebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.sergbek.socialnetworks.interfaces.PublisherInterface;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;


public class TaskLoginFacebookFragment extends Fragment implements FacebookCallback<LoginResult> {

    private LoginResult loginResult;
    private PublisherInterface mPublisherInterface;

    private static final String TAG = TaskLoginFacebookFragment.class.getSimpleName();

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PublisherInterface) {
            this.mPublisherInterface = (PublisherInterface) context;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.mPublisherInterface = (PublisherInterface) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        setLoginResult(loginResult);
        mPublisherInterface.notifySubscribers(loginResult);
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "onCancel");
    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "onError");
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }
}
