package com.example.sergbek.socialnetworks.facebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sergbek.socialnetworks.interfaces.PublisherInterface;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;


public class TaskFacebookFragment extends Fragment implements GraphRequest.Callback{

    private GraphResponse mResponse;
    private PublisherInterface mPublisherInterface;

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
    public void onDetach() {
        super.onDetach();
        mPublisherInterface = null;
    }

    @Override
    public void onCompleted(GraphResponse response) {
        setResponse(response);
        mPublisherInterface.notifySubscribers(response);
    }

    public GraphResponse getResponse() {
        return mResponse;
    }

    public void setResponse(GraphResponse response) {
        mResponse = response;
    }
}
