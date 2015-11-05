package com.example.sergbek.socialnetworks.twitter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sergbek.socialnetworks.R;
import com.example.sergbek.socialnetworks.global.Constants;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;

import io.fabric.sdk.android.Fabric;


public class TwitterFragment extends Fragment implements View.OnClickListener {

    private TwitterLoginButton mTwitterLoginButton;
    private Button mBtnSendTweet;
    private TwitterSession mTwitterSession;
    private TextView mTvInfoUser;
    private ImageView mIvImageUser;
    private EditText mEdShareTwitter;

    private static final String TAG = TwitterFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY,
                Constants.TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_twitter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        defineComponents(view);
        setListeners();
        if (Twitter.getInstance().core.getSessionManager().getActiveSession() != null) {
            mTwitterSession = Twitter.getInstance().core.getSessionManager().getActiveSession();
            getUserInfo();
            mEdShareTwitter.setVisibility(View.VISIBLE);
            mBtnSendTweet.setVisibility(View.VISIBLE);
        }

        mTwitterLoginButton = (TwitterLoginButton) view.findViewById(R.id.btn_twitter_login_FT);
        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {
                if (isAdded()) {
                    mTwitterSession = result.data;
                    getUserInfo();
                    mEdShareTwitter.setVisibility(View.VISIBLE);
                    mBtnSendTweet.setVisibility(View.VISIBLE);
                    String msg = "@" + mTwitterSession.getUserName() + " logged in! (#"
                            + mTwitterSession.getUserId() + ")";
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(TwitterException exception) {
                if (isAdded()) {
                    Log.d(TAG, "Login with Twitter failure", exception);
                    Toast.makeText(getActivity(), "Login with Twitter failure", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void defineComponents(View view) {
        mBtnSendTweet = (Button) view.findViewById(R.id.btn_sendTweet_FT);
        mTvInfoUser = (TextView) view.findViewById(R.id.tv_textInfoUser_FT);
        mIvImageUser = (ImageView) view.findViewById(R.id.iv_imageAccountTwitter_FT);
        mEdShareTwitter = (EditText) view.findViewById(R.id.ed_textShareTwitter_FT);
    }

    private void setListeners() {
        mBtnSendTweet.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSendTweet) {
            publishTweet();
        }
    }

    private void getUserInfo() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(mTwitterSession);
        twitterApiClient.getAccountService().verifyCredentials(null, null, new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                if (isAdded()) {
                    setText(result.data);
                    String url = result.data.profileBannerUrl;
                    Picasso.with(getActivity())
                            .load(url)
                            .into(mIvImageUser);
                }
            }

            @Override
            public void failure(TwitterException e) {
                if (isAdded())
                    Log.d(TAG, e.toString());
            }
        });
    }

    private void setText(User data) {
        mTvInfoUser.setText("Name: " + data.name + "\n"
                + "id: " + data.id + "\n"
                + "ScreenName: " + data.screenName + "\n"
                + "TimeZone: " + data.timeZone + "\n"
                + "url: " + data.url);
    }

    private void publishTweet() {
        final StatusesService statusesService = TwitterCore.getInstance().
                getApiClient().getStatusesService();
        statusesService.update(mEdShareTwitter.getText().toString(), null, null, null, null, null,
                null, null, null, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> tweetResult) {
                        if (isAdded())
                            Toast.makeText(getActivity(), "Status published successfully",
                                    Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(TwitterException e) {
                        if (isAdded())
                            Toast.makeText(getActivity(), "Error!",
                                    Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
