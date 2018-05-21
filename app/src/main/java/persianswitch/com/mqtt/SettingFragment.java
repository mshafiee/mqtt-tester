package persianswitch.com.mqtt;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import persianswitch.com.mqtt.mqttClient.OnConnectionChangeListener;
import persianswitch.com.mqtt.mqttClient.mqttClient;
import persianswitch.com.mqtt.mqttClient.mqttClientOptions;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "mqttClient";
    public static final String PREFERENCES_MQTT_SERVER_URI = "mqtt_server_uri";
    public static final String PREFERENCES_MQTT_CLIENT_ID = "mqtt_client_id";
    public static final String PREFERENCES_MQTT_USER_NAME_URI = "mqtt_user_name_uri";
    public static final String PREFERENCES_MQTT_PASSWORD_URI = "mqtt_password_uri";
    private View rootView;

    private EditText etServerUri;
    private EditText etClientName;
    private EditText etUserName;
    private EditText etPassword;
    private SharedPreferences mPreferences;
    private Button btnConnect;
    private Button btnDisconnect;

    public SettingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String serverUri = mPreferences.getString(PREFERENCES_MQTT_SERVER_URI, "tcp://91.232.66.65:8080");
        String clientId = mPreferences.getString(PREFERENCES_MQTT_CLIENT_ID, Build.MODEL);
        String userName = mPreferences.getString(PREFERENCES_MQTT_USER_NAME_URI, "test");
        String password = mPreferences.getString(PREFERENCES_MQTT_PASSWORD_URI, "test");

        etServerUri = (EditText) rootView.findViewById(R.id.etServerUri);
        etServerUri.setText(serverUri);


        etClientName = (EditText) rootView.findViewById(R.id.etClientName);
        etClientName.setText(clientId);

        etUserName = (EditText) rootView.findViewById(R.id.etUserName);
        etUserName.setText(userName);

        etPassword = (EditText) rootView.findViewById(R.id.etPassword);
        etPassword.setText(userName);

        btnConnect = (Button) rootView.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefsEditor = mPreferences.edit();
                prefsEditor.putString(PREFERENCES_MQTT_SERVER_URI, etServerUri.getText().toString());
                prefsEditor.putString(PREFERENCES_MQTT_CLIENT_ID, etClientName.getText().toString());
                prefsEditor.putString(PREFERENCES_MQTT_USER_NAME_URI, etUserName.getText().toString());
                prefsEditor.putString(PREFERENCES_MQTT_PASSWORD_URI, etPassword.getText().toString());
                prefsEditor.commit();
                mqttClient.getInstance().connect(new mqttClientOptions(getContext(), etServerUri.getText().toString(),
                        etUserName.getText().toString(), etPassword.getText().toString(),
                        etClientName.getText().toString()));
            }
        });

        btnDisconnect = (Button) rootView.findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttClient.getInstance().disconnect();
            }
        });


        btnDisconnect.setEnabled(mqttClient.getInstance().isConnected());
        btnConnect.setEnabled(!mqttClient.getInstance().isConnected());

        mqttClient.getInstance().setmOnConnectListener(new OnConnectionChangeListener() {
            @Override
            public void onConnectionChane(boolean status) {
                btnDisconnect.setEnabled(status);
                btnConnect.setEnabled(!status);
            }

        });

        return rootView;
    }


}