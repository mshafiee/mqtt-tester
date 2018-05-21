package persianswitch.com.mqtt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import persianswitch.com.mqtt.mqttClient.mqttClient;
import persianswitch.com.mqtt.mqttClient.mqttMessage;

/**
 * A placeholder fragment containing a simple view.
 */
public class LogFragment extends Fragment {
    private static final String ARG_LOG_LEVEL = "log_level";
    public static final int LOG_LEVEL_CONTROL_MESSAGES = 0;
    public static final int LOG_LEVEL_MESSAGES = 1;

    private static final String TAG = "mqttClient";
    private View rootView;
    private RecyclerView mRecyclerView;
    private LogAdapter mLogAdapter;

    public LogFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LogFragment newInstance(int logLevel) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOG_LEVEL, logLevel);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_log, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.log_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLogAdapter = new LogAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mLogAdapter);

        ConnectableObservable<mqttMessage> connectable = mqttClient.getInstance().getMqttConnectable();
        connectable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<mqttMessage>() {
                    @Override
                    public boolean test(mqttMessage mqttMessage) throws Exception {
                        switch (getArguments().getInt(ARG_LOG_LEVEL)) {
                            case LOG_LEVEL_CONTROL_MESSAGES:
                                return mqttMessage.getQos() == mqttClient.QOS_CONTROL;
                            case LOG_LEVEL_MESSAGES:
                                return mqttMessage.getQos() != mqttClient.QOS_CONTROL;
                        }
                        return mqttMessage.getQos() == mqttClient.QOS_CONTROL;
                    }
                })
                .subscribe(new Observer<mqttMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(mqttMessage mqttMessage) {
                        addToLog(mqttMessage);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        connectable.connect();

        return rootView;
    }


    private void addToLog(mqttMessage mqttMessage) {
        String log = "";

        if (mqttMessage.getMessageId() > -1) {
            log += " MsgID: " + mqttMessage.getMessageId();
        }

        log = mqttMessage.getTopic() + ": " + mqttMessage.getMessage();
        if (mqttMessage.getQos() > -1) {
            log += " Qos: " + mqttMessage.getQos();
        }
        mLogAdapter.add(log);
        mRecyclerView.smoothScrollToPosition(-10);

    }


}