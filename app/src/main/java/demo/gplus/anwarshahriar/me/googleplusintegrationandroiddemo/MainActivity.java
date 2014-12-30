package demo.gplus.anwarshahriar.me.googleplusintegrationandroiddemo;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;


public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>{

    private GoogleApiClient apiClient;
    private boolean intentInProgress;

    TextView tvPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPerson = (TextView) findViewById(R.id.tvPerson);

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Plus.PeopleApi.loadVisible(apiClient, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!intentInProgress && result.hasResolution()) {
            try {
                intentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        0, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                apiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            intentInProgress = false;

            if (!apiClient.isConnecting()) {
                apiClient.connect();
            }
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();

            try {
                int count = personBuffer.getCount();
                String nameAndEmail = "";
                for (int i = 0; i < count; i++) {
                    nameAndEmail += personBuffer.get(i).getDisplayName() + "\n";
                }
                tvPerson.setText(nameAndEmail);
            } finally {
                personBuffer.close();
            }
        }
    }
}
