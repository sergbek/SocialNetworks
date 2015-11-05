package com.example.sergbek.socialnetworks.interfaces;

import com.example.sergbek.socialnetworks.facebook.TaskFacebookFragment;
import com.facebook.FacebookCallback;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;


public interface GetTaskCallbackInterface {

    GraphRequest.Callback getGraphRequestCallbackText();

    TaskFacebookFragment getGraphRequestCallbackPhoto();

    FacebookCallback<LoginResult> getFacebookCallback();
}
