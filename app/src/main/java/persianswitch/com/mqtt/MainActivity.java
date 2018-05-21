package persianswitch.com.mqtt;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import persianswitch.com.mqtt.mqttClient.mqttClient;
import persianswitch.com.mqtt.mqttClient.mqttClientOptions;
import persianswitch.com.mqtt.mqttClient.mqttTopic;


public class MainActivity extends AppCompatActivity {
    public static final String PREFERENCES_MQTT_SERVER_URI = "mqtt_server_uri";
    public static final String PREFERENCES_MQTT_CLIENT_ID = "mqtt_client_id";
    public static final String PREFERENCES_MQTT_USER_NAME_URI = "mqtt_user_name_uri";
    public static final String PREFERENCES_MQTT_PASSWORD_URI = "mqtt_password_uri";
    private SharedPreferences mPreferences;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Send a new Message", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        startMqttService();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient.getInstance() != null) {
            mqttClient.getInstance().unbind();
        }
    }

    private void startMqttService() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverUri = mPreferences.getString(PREFERENCES_MQTT_SERVER_URI, "tcp://91.232.66.65:8080");
        String clientId = mPreferences.getString(PREFERENCES_MQTT_CLIENT_ID, Build.MODEL);
        String userName = mPreferences.getString(PREFERENCES_MQTT_USER_NAME_URI, "test");
        String password = mPreferences.getString(PREFERENCES_MQTT_PASSWORD_URI, "test");

        ArrayList<mqttTopic> defaultTopics = new ArrayList<>();
        defaultTopics.add(new mqttTopic("TimeTopic", mqttClient.QOS_0));
        defaultTopics.add(new mqttTopic("TestTopic", mqttClient.QOS_1));
        defaultTopics.add(new mqttTopic(ChatFragment.CHAT_TOPIC, mqttClient.QOS_2));
        mqttClient.init(new mqttClientOptions(this, serverUri, userName, password, clientId, defaultTopics));

    }
}
