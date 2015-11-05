package com.example.sergbek.socialnetworks.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sergbek.socialnetworks.interfaces.GetTaskCallbackInterface;
import com.example.sergbek.socialnetworks.vk.VKFragment;
import com.example.sergbek.socialnetworks.facebook.TaskFacebookFragment;
import com.example.sergbek.socialnetworks.facebook.FacebookFragment;
import com.example.sergbek.socialnetworks.R;
import com.example.sergbek.socialnetworks.facebook.TaskLoginFacebookFragment;
import com.example.sergbek.socialnetworks.global.Constants;
import com.example.sergbek.socialnetworks.google_plus.GooglePlusFragment;
import com.example.sergbek.socialnetworks.interfaces.PublisherActionListener;
import com.example.sergbek.socialnetworks.interfaces.PublisherInterface;
import com.example.sergbek.socialnetworks.twitter.TwitterFragment;
import com.facebook.FacebookCallback;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        PublisherInterface, FacebookFragment.OnAddAndRemoveListener, GetTaskCallbackInterface {

    private Button mBtnGooglePlus;
    private Button mBtnTitter;
    private Button mBtnFacebook;
    private Button mBtnVK;
    private List<PublisherActionListener> listeners = new ArrayList<>();
    private FragmentManager mFm;

    private TaskFacebookFragment mTaskFacebookFragment_1;
    private TaskFacebookFragment mTaskFacebookFragment_2;
    private TaskLoginFacebookFragment mTaskLoginFacebookFragment;

    private static final int CONTAINER_FRAGMENT = R.id.container_AM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defineComponents();
        setListeners();

        mFm = getFragmentManager();
        addTaskFragments();
    }

    private void defineComponents() {
        mBtnGooglePlus = (Button) findViewById(R.id.btn_googlePlus_AM);
        mBtnTitter = (Button) findViewById(R.id.btn_twitter_AM);
        mBtnFacebook = (Button) findViewById(R.id.btn_facebook_AM);
        mBtnVK = (Button) findViewById(R.id.btn_vk_AM);
    }

    private void setListeners() {
        mBtnGooglePlus.setOnClickListener(this);
        mBtnTitter.setOnClickListener(this);
        mBtnFacebook.setOnClickListener(this);
        mBtnVK.setOnClickListener(this);
    }

    private void addTaskFragments() {
        mTaskFacebookFragment_1 = (TaskFacebookFragment)
                mFm.findFragmentByTag(Constants.TAG_TASK_FRAGMENT_1);
        mTaskFacebookFragment_2 = (TaskFacebookFragment)
                mFm.findFragmentByTag(Constants.TAG_TASK_FRAGMENT_2);
        mTaskLoginFacebookFragment = (TaskLoginFacebookFragment)
                mFm.findFragmentByTag(Constants.TAG_TASK_LOGIN_FRAGMENT);

        if (mTaskFacebookFragment_1 == null) {
            mTaskFacebookFragment_1 = new TaskFacebookFragment();
            mFm.beginTransaction().add(mTaskFacebookFragment_1,
                    Constants.TAG_TASK_FRAGMENT_1).commit();
        }

        if (mTaskFacebookFragment_2 == null) {
            mTaskFacebookFragment_2 = new TaskFacebookFragment();
            mFm.beginTransaction().add(mTaskFacebookFragment_2,
                    Constants.TAG_TASK_FRAGMENT_2).commit();
        }

        if (mTaskLoginFacebookFragment == null) {
            mTaskLoginFacebookFragment = new TaskLoginFacebookFragment();
            mFm.beginTransaction().add(mTaskLoginFacebookFragment,
                    Constants.TAG_TASK_LOGIN_FRAGMENT).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_googlePlus_AM:
                GooglePlusFragment loginGoogleFragment = new GooglePlusFragment();
                mFm.beginTransaction().replace(CONTAINER_FRAGMENT,
                        loginGoogleFragment, Constants.TAG_FRAGMENT_GOOGLE).commit();
                break;
            case R.id.btn_twitter_AM:
                TwitterFragment twitterFragment = new TwitterFragment();
                mFm.beginTransaction().replace(CONTAINER_FRAGMENT,
                        twitterFragment, Constants.TAG_FRAGMENT_TWITTER).commit();
                break;
            case R.id.btn_facebook_AM:
                FacebookFragment facebookFragment = new FacebookFragment();
                mFm.beginTransaction().replace(CONTAINER_FRAGMENT,
                        facebookFragment, Constants.TAG_FRAGMENT_FACEBOOK).commit();
                break;
            case R.id.btn_vk_AM:
                VKFragment vkFragment = new VKFragment();
                mFm.beginTransaction().replace(CONTAINER_FRAGMENT,
                        vkFragment, Constants.TAG_FRAGMENT_VK).commit();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RC_SIGN_IN_GOOGLE_PLUS) {
            GooglePlusFragment fragment = (GooglePlusFragment)
                    mFm.findFragmentByTag(Constants.TAG_FRAGMENT_GOOGLE);
            fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.RC_SIGN_IN_TWITTER) {
            TwitterFragment twitterFragment = (TwitterFragment)
                    mFm.findFragmentByTag(Constants.TAG_FRAGMENT_TWITTER);
            twitterFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.RC_SIGN_IN_FACEBOOK) {
            FacebookFragment facebookFragment = (FacebookFragment)
                    mFm.findFragmentByTag(Constants.TAG_FRAGMENT_FACEBOOK);
            facebookFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.RC_SIGN_IN_VK) {
            VKFragment vkFragment = (VKFragment)
                    mFm.findFragmentByTag(Constants.TAG_FRAGMENT_VK);
            vkFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void addListener(PublisherActionListener listener) {
        listeners.add(listener);
        if (mTaskFacebookFragment_1.getResponse() != null)
            notifySubscribers(mTaskFacebookFragment_1.getResponse());


        if (mTaskFacebookFragment_2.getResponse() != null)
            notifySubscribers(mTaskFacebookFragment_2.getResponse());


        if (mTaskLoginFacebookFragment.getLoginResult() != null)
            notifySubscribers(mTaskLoginFacebookFragment.getLoginResult());

    }

    @Override
    public void removeListener(PublisherActionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifySubscribers(GraphResponse response) {
        for (PublisherActionListener actionListener : listeners) {
            actionListener.doAction(response);
        }
    }

    @Override
    public void notifySubscribers(LoginResult loginResult) {
        for (PublisherActionListener actionListener : listeners) {
            actionListener.doAction(loginResult);
        }
    }

    @Override
    public void onStartAddListener(PublisherActionListener listener) {
        addListener(listener);
    }

    @Override
    public void onStartRemoveListener(PublisherActionListener listener) {
        removeListener(listener);
    }

//    public GraphRequest.Callback getCallbackText() {
//        return mTaskFacebookFragment_1;
//    }
//
//    public GraphRequest.Callback getCallbackPhoto() {
//        return mTaskFacebookFragment_2;
//    }
//
//    public FacebookCallback<LoginResult> getCallbackResult() {
//        return mTaskLoginFacebookFragment;
//    }


    @Override
    public GraphRequest.Callback getGraphRequestCallbackText() {
        return mTaskFacebookFragment_1;
    }

    @Override
    public TaskFacebookFragment getGraphRequestCallbackPhoto() {
        return mTaskFacebookFragment_2;
    }

    @Override
    public FacebookCallback<LoginResult> getFacebookCallback() {
        return mTaskLoginFacebookFragment;
    }
}
