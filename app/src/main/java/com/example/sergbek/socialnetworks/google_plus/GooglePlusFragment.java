package com.example.sergbek.socialnetworks.google_plus;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergbek.socialnetworks.R;
import com.example.sergbek.socialnetworks.global.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;


public class GooglePlusFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mSignInIntent;
    private int mSignInProgress;

    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mBtnShare;
    private EditText mEditTextShare;
    private TextView mStatus;
    private TextView mTvAboutMe;
    private ImageView mImageView;

    
    private static final int STATE_SIGNING_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = buildGoogleApiClient();
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_MOMENTS))

                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_plus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        defineComponents(view);
        setListeners();
    }

    private void defineComponents(View view) {
        mSignInButton = (SignInButton) view.findViewById(R.id.btn_signIn_FGP);
        mSignOutButton = (Button) view.findViewById(R.id.btn_signOut_FGP);
        mBtnShare = (Button) view.findViewById(R.id.btn_share_FGP);
        mEditTextShare = (EditText) view.findViewById(R.id.ed_textShare_FGP);
        mStatus = (TextView) view.findViewById(R.id.tv_statusLabels_FGP);
        mImageView = (ImageView) view.findViewById(R.id.iv_photoUser_FGP);
        mTvAboutMe = (TextView) view.findViewById(R.id.tv_aboutMe_FGP);

        mSignInButton.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_DARK);
    }

    private void setListeners() {
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.RC_SIGN_IN_GOOGLE_PLUS:
                if (resultCode == Activity.RESULT_OK) {
                    mSignInProgress = STATE_SIGNING_IN;
                } else {
                    mSignInProgress = Constants.RC_SIGN_IN_GOOGLE_PLUS;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInButton.setEnabled(false);
        mSignOutButton.setEnabled(true);
        mTvAboutMe.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);

        mSignInProgress = Constants.RC_SIGN_IN_GOOGLE_PLUS;

        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String personPhoto = currentPerson.getImage().getUrl();

        mStatus.setText("Signed In to My App");
        setText(currentPerson);

        Picasso.with(getActivity())
                .load(personPhoto)
                .into(mImageView);

    }

    private void setText(Person person) {
        mTvAboutMe.setText("Nick name: " + person.getDisplayName() + "\n \n"
                + "Email address: " + Plus.AccountApi.getAccountName(mGoogleApiClient) + "\n \n"
                + "Url Google Plus Profile: " + person.getUrl() + "\n \n"
                + "Birthday: " + person.getBirthday());
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            switch (v.getId()) {
                case R.id.btn_signIn_FGP:
                    mStatus.setText("Signing In");
                    resolveSignInError();
                    break;
                case R.id.btn_signOut_FGP:
                    if (mGoogleApiClient.isConnected()) {
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        onSignedOut();
                    }
                    break;
                case R.id.btn_share_FGP:
                    Intent shareIntent = new PlusShare.Builder(getActivity())
                            .setType("text/plain")
                            .setText(mEditTextShare.getText())
                            .getIntent();
                    startActivityForResult(shareIntent, 0);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = connectionResult.getResolution();
            if (mSignInProgress == STATE_SIGNING_IN) {
                resolveSignInError();
            }
        }
        onSignedOut();
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            try {
                mSignInProgress = STATE_IN_PROGRESS;
                getActivity().startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        Constants.RC_SIGN_IN_GOOGLE_PLUS, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mSignInProgress = STATE_SIGNING_IN;
                mGoogleApiClient.connect();
            }
        }
    }

    private void onSignedOut() {
        mSignInButton.setEnabled(true);
        mSignOutButton.setEnabled(false);
        mImageView.setVisibility(View.GONE);
        mTvAboutMe.setVisibility(View.GONE);

        mStatus.setText("Signed out");
    }


}
