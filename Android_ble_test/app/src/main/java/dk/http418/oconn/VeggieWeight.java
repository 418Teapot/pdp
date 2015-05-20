package dk.http418.oconn;

import android.app.Activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


public class VeggieWeight extends Activity {

    public final static String TAG = VeggieWeight.class.getSimpleName();

    // setup bluetooth
    private String mDevAdr;
    private RBLService mBluetoothLeService;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

    private TextView currAmt;

    private ProgressBar pb;
    private int wProg = 0;
    private int allowedAmt = 0;

    private Button confBtn;

    private boolean overWeight;

    private AlertDialog.Builder owd;

    private int amountPacked = 0;

    private Intent intent;

    private final ServiceConnection mServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service){

            mBluetoothLeService = ((RBLService.LocalBinder) service)
                    .getService();

            if(!mBluetoothLeService.initialize()){
                Log.e(TAG, "Kunne ikke init() Bluetooth");
                finish();
            }

            // forbind
            mBluetoothLeService.connect(mDevAdr);
            System.out.println("DBG OUT: Connecting to BLE on adr: "+mDevAdr);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName){
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veggie_weight);

        // vi henter vores data fra putExtra
        intent = getIntent();

        //blueTooth
        mDevAdr = intent.getStringExtra("btAdr");


        owd = new AlertDialog.Builder(this)
                    .setTitle("For meget vægt!")
                    .setMessage("Du har taget for meget! Fjern lidt fra vægten!")
                    .setIcon(android.R.drawable.ic_dialog_alert);


        overWeight = false;

        confBtn = (Button) findViewById(R.id.confirmWeightBtn);

        // display
        //Veggie veg = (Veggie) intent.getSerializableExtra("veggie");
        final String veggie_name = intent.getStringExtra("Name");
        int veggie_amt = intent.getIntExtra("Amt", 0);

        getActionBar().setTitle("Afvej "+veggie_name);

        TextView nameView = (TextView) findViewById(R.id.veggieName);
        TextView amtView = (TextView) findViewById(R.id.totalWeight);

        currAmt = (TextView) findViewById(R.id.currWeight);
        if(currAmt != null){
            System.out.println("ALL SYSTEMS GO!");
        } else {
            System.out.println("INGEN CURR AMOUNT WTF!");
            finish();
        }
        pb = (ProgressBar) findViewById(R.id.weightProgress);

        if(nameView != null){
            nameView.setText(veggie_name);
        }

        if(amtView != null){
            amtView.setText("/" + veggie_amt + "g.");
        }
        allowedAmt = veggie_amt;
        ImageView imgView = (ImageView) findViewById(R.id.veggieW_img);

        if(imgView != null) {
            // img
            if (veggie_name.equals("Æbler")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.aebler));
            } else if (veggie_name.equals("Ærter")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.aerter));
            } else if (veggie_name.equals("Cheese")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.cheese));
            } else if (veggie_name.equals("Kaffe")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.kaffe));
            } else {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.test));
            }
        }

        // greenify
        pb.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

        // hookup til vægten
        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // hookup til knappen
        confBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("JEG HAR NOK MAYN!");
                unbindService(mServiceConnection);

                Intent data = new Intent();

                data.putExtra("veg_name", veggie_name);
                data.putExtra("packedAmt", amountPacked);
                if(getParent()== null){
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();

            }
        });

    }

    private void updVeggie(){

    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_veggie_weight, menu);
        return true;
    }

    // handle back by unbinding bluetooth service!
    @Override
    public void onBackPressed(){
        unbindService(mServiceConnection);
        finish();
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
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mGattUpdateReceiver);
    }


    private void displayData(byte[] byteArray) {

        //System.out.println("DISPLAYING DATA!");
        // vis vægten på skærmen

        if (byteArray != null) {
            String data = new String(byteArray);
            Scanner in = new Scanner(data).useDelimiter("[^0-9]+");
            int parsed = in.nextInt();

            if(currAmt != null){
                currAmt.setText("" + parsed);

                int prog = (int) Math.round(parsed * 100.0/allowedAmt);

                if(pb != null) {
                    pb.setProgress(prog);
                }

                if(prog < 100) {
                    pb.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    amountPacked = parsed;
                    //overWeight = false;
                    if(confBtn != null && !confBtn.isEnabled()){
                        confBtn.setText("Jeg har nok!");
                        confBtn.setEnabled(true);
                    }
                } else {
                    pb.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    //overWeight = true;
                    if(confBtn != null && confBtn.isEnabled()){
                        confBtn.setText("Hov! Der er for meget på vægten!");
                        confBtn.setEnabled(false);
                    }
                }
            }
            //System.out.println("DBG OUT: "+parsed);

        }
    }

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
                true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

}
