package com.example.sergbek.socialnetworks.vk;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sergbek.socialnetworks.R;
import com.example.sergbek.socialnetworks.global.Constants;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class VKFragment extends Fragment implements View.OnClickListener {

    private Button mBtnVkSignIn;
    private TextView mTvInfoUserVk;
    private ImageView mImvPhotoUser;

    public static final String TAG = VKFragment.class.getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKSdk.customInitialize(getActivity(), Constants.VK_API_KEY, String.valueOf(Constants.VK_API_KEY));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vk, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        defineComponents(view);
        setListeners();
        if (VKAccessToken.currentToken() != null)
            sendRequest();
    }

    private void setListeners() {
        mBtnVkSignIn.setOnClickListener(this);
    }

    private void defineComponents(View view) {
        mBtnVkSignIn = (Button) view.findViewById(R.id.btn_signIn_FV);
        mTvInfoUserVk = (TextView) view.findViewById(R.id.tv_textInfoUserVK_FV);
        mImvPhotoUser = (ImageView) view.findViewById(R.id.imv_photoUser_FV);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnVkSignIn) {
            VKSdk.login(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                sendRequest();
            }

            @Override
            public void onError(VKError error) {
                Log.d(TAG, "onResult");
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendRequest(){
        VKRequest request = VKApi.users().get(VKParameters.from
                (VKApiConst.USER_ID, null, VKApiConst.FIELDS, "photo_400_orig"));
        request.executeWithListener(new VKRequest.VKRequestListener() {

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                setInfo(response);

            }
        });
    }

    private void setInfo(VKResponse response) {
        JSONObject jsonObject;
        try {
            jsonObject = response.json.getJSONArray("response").getJSONObject(0);
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");

            String urlPhoto = jsonObject.getString("photo_400_orig");

            mTvInfoUserVk.setText("ID: " + id + "\n"
                    + "Name: " + name + "\n"
                    + "Last name: " + lastName);

            Picasso.with(getActivity())
                    .load(urlPhoto)
                    .into(mImvPhotoUser);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
