package com.chernowii.playmote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class PlayMoteWear extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private ImageButton VolUp;
    private ImageButton VolDown;
    private ImageButton Prev;
    private ImageButton Next;
    private ImageButton PlayPause;

    Node wearNode;
    GoogleApiClient wearGoogleApiClient;
    private boolean mResolvingError=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mote_wear);
        wearGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setAmbientEnabled();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                VolUp = (ImageButton) stub.findViewById(R.id.volup);
                VolDown = (ImageButton) stub.findViewById(R.id.voldown);
                Prev = (ImageButton) stub.findViewById(R.id.prev);
                Next = (ImageButton) stub.findViewById(R.id.next);
                PlayPause = (ImageButton) stub.findViewById(R.id.playpause);

                PlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCommand("/playpause");
                    }
                });

                Next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCommand("/next");
                    }
                });

                Prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCommand("/prev");
                    }
                });

                VolDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCommand("/voldown");
                    }
                });

                VolUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendCommand("/volup");
                    }
                });
            }
        });
    }
    private void sendCommand(String command) {
        if (wearNode != null && wearGoogleApiClient!=null && wearGoogleApiClient.isConnected()) {
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
            Wearable.MessageApi.sendMessage(
                    wearGoogleApiClient, wearNode.getId(), command, null).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            Toast.makeText(getApplicationContext(),
                    "No connection to phone", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),
                    "Connect watch to phone!", Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        ImageButton prev = (ImageButton) findViewById(R.id.prev);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        ImageButton volup = (ImageButton) findViewById(R.id.volup);
        ImageButton voldown = (ImageButton) findViewById(R.id.voldown);
        ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

        TextView AmbientText = (TextView) findViewById(R.id.ambient);
        next.setVisibility(View.INVISIBLE);
        prev.setVisibility(View.INVISIBLE);
        volup.setVisibility(View.INVISIBLE);
        voldown.setVisibility(View.INVISIBLE);
        playpause.setVisibility(View.INVISIBLE);

        AmbientText.setVisibility(View.VISIBLE);

    }
    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        ImageButton prev = (ImageButton) findViewById(R.id.prev);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        ImageButton volup = (ImageButton) findViewById(R.id.volup);
        ImageButton voldown = (ImageButton) findViewById(R.id.voldown);
        ImageButton playpause = (ImageButton) findViewById(R.id.playpause);

        TextView AmbientText = (TextView) findViewById(R.id.ambient);
        next.setVisibility(View.VISIBLE);
        prev.setVisibility(View.VISIBLE);
        volup.setVisibility(View.VISIBLE);
        voldown.setVisibility(View.VISIBLE);
        playpause.setVisibility(View.VISIBLE);

        AmbientText.setVisibility(View.GONE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            wearGoogleApiClient.connect();
        }
    }

    /*
    * Resolve the node = the connected device to send the message to
    */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(wearGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    wearNode = node;
                }
            }
        });
    }


    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),                     "Error, not connected to phone!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),                     "Error, not connected to phone!", Toast.LENGTH_SHORT).show();
    }
}
