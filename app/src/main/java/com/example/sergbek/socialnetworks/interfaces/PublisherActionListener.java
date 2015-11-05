package com.example.sergbek.socialnetworks.interfaces;

import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;


public interface PublisherActionListener {
    void doAction(GraphResponse response);

    void doAction(LoginResult loginResult);
}
