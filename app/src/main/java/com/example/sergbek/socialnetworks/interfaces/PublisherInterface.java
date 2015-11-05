package com.example.sergbek.socialnetworks.interfaces;


import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

public interface PublisherInterface {

    void addListener(PublisherActionListener listener);

    void removeListener(PublisherActionListener listener);

    void notifySubscribers(GraphResponse response);

    void notifySubscribers(LoginResult loginResult);
}
