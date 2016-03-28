package com.chernowii.playmote;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AppConfig extends AppCompatActivity {
    String IpAddress = "";
    String Port = "";
    String toastStatus = "";
    public static final String PREFS_NAME = "Preferences";
    public static final String ipsetting = "ip_address";
    public static final String portsetting = "ip_port";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_config);
        Button config = (Button) findViewById(R.id.start);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configure();
            }
        });
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState()== SupplicantState.COMPLETED) {
            TextView wifi = (TextView) findViewById(R.id.wifi);
            wifi.setText(wifiInfo.getSSID());
        }

    }
    public void configure(){
        EditText IPADDRESS = (EditText) findViewById(R.id.ipbox);
        IpAddress = IPADDRESS.getText().toString();
        EditText PORT = (EditText) findViewById(R.id.portbox);
        Port = PORT.getText().toString();

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(ipsetting, IpAddress);
        editor.putString(portsetting, Port);
        editor.commit();
        Toast.makeText(getApplicationContext(),"All set! " + IpAddress, Toast.LENGTH_SHORT).show();

    }
}
