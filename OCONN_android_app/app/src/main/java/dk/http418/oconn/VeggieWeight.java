package dk.http418.oconn;

import android.app.ActionBar;
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
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

    private Context context = this;

    private boolean overWeight;

    private AlertDialog.Builder owd;

    private int amountPacked = 0;
    private boolean isConnected = false;

    private int compensWeight = 0;

    private Intent intent;

    private String username = "";

    private boolean gotExtra = false;

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
            if(mBluetoothLeService.connect(mDevAdr)) {
                //sendToDevice(mBluetoothLeService.getSupportedGattService().getCharacteristic(RBLService.UUID_BLE_SHIELD_TX), "1");
                isConnected = true;
                System.out.println("CONNECTED MOFO!");
            }

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
                //sendToDevice(mBluetoothLeService.getSupportedGattService().getCharacteristic(RBLService.UUID_BLE_SHIELD_TX), "0");
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBluetoothLeService.getSupportedGattService());

                //if(isConnected)
                    //sendToDevice(mBluetoothLeService.getSupportedGattService().getCharacteristic(RBLService.UUID_BLE_SHIELD_TX), "1");

            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            } else if (RBLService.ACTION_GATT_CONNECTED.equals(action)) {
               // sendToDevice(mBluetoothLeService.getSupportedGattService().getCharacteristic(RBLService.UUID_BLE_SHIELD_TX), "1");
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
        compensWeight = intent.getIntExtra("compensate", 0);
        username = intent.getStringExtra("loggedUser");

        owd = new AlertDialog.Builder(this)
                    .setTitle("For meget vægt!")
                    .setMessage("Du har taget for meget! Fjern lidt fra vægten!")
                    .setIcon(android.R.drawable.ic_dialog_alert);


        overWeight = false;



        confBtn = (Button) findViewById(R.id.confirmWeightBtn);

        // display
        //Veggie veg = (Veggie) intent.getSerializableExtra("veggie");
        final String veggie_name = intent.getStringExtra("Name");
        final int veggie_amt = intent.getIntExtra("Amt", 0);

        final int extraVeggies = intent.getIntExtra("extraVeggies", 0);

        getActionBar().setTitle("Afvej "+veggie_name);
        getActionBar().setDisplayHomeAsUpEnabled(false);



        currAmt = (TextView) findViewById(R.id.currWeight);
        if(currAmt != null){
            System.out.println("ALL SYSTEMS GO!");
        } else {
            System.out.println("INGEN CURR AMOUNT WTF!");
            finish();
        }
        pb = (ProgressBar) findViewById(R.id.weightProgress);

        TextView nameView = (TextView) findViewById(R.id.veggieName);
        TextView amtView = (TextView) findViewById(R.id.totalWeight);

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
            } else if (veggie_name.equals("Jordskokker")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.artichoke));
            } else if (veggie_name.equals("1 stk Salathoved")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.salat));
            } else if (veggie_name.equals("Kartofler")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.kartofler));
            } else if (veggie_name.equals("1 bundt Rabarber")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.rabarber));
            } else if (veggie_name.equals("2 poser Brændenælde")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.naelder));
            } else if (veggie_name.equals("1 Peberrod")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.peberrod));
            } else if (veggie_name.equals("Peberfrugt")) {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.peberfrugt));
            }  else {
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.test));
            }
        }



        // greenify
        pb.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

        // hookup til vægten
        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if(extraVeggies > 50){
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.veggieWeightLayout);
            final Button iWantExtras = new Button(this);
            iWantExtras.setText("Jeg vil gerne ha' lidt ekstra");
            //iWantExtras.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, R.id.confirmWeightBtn);
            p.addRule(RelativeLayout.CENTER_IN_PARENT);
            iWantExtras.setLayoutParams(p);

            layout.addView(iWantExtras);

            // click
            iWantExtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotExtra = true;
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.veggieWeightLayout);
                    layout.removeView(view);

                    TextView nameView = (TextView) findViewById(R.id.veggieName);
                    TextView amtView = (TextView) findViewById(R.id.totalWeight);

                    if(nameView != null){
                        nameView.setText(veggie_name);
                    }

                    if(amtView != null){
                        amtView.setText("/" + (veggie_amt+extraVeggies) + "g.");
                    }

                    allowedAmt = (veggie_amt+extraVeggies);

                }
            });

        }

        // hookup til knappen
        confBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                packit(veggie_name, false, gotExtra);

            }
        });

        //System.out.println("ADR: "+mDevAdr);
        // send tænd signal til vægten
        //sendToDevice("1");

    }

    private void packit(String nm, boolean resetExtra, boolean gotExtraStuff){
        int threshold = new Double(allowedAmt*0.98).intValue();

        final String veggie_name = nm;

        if(gotExtra){
            new SetExtra(){

                @Override
                protected void onPostExecute(Boolean success) {
                    //System.out.println("Return bool: "+canWe);

                    if(success){
                        System.out.println("Extra was added!");
                    }
                }

            }.execute(veggie_name, ""+(allowedAmt-amountPacked), "reset");
        }

        if(amountPacked < threshold && !gotExtra){
            // vi donerer lige nu!

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context  );

            // set title
            alertDialogBuilder.setTitle("Er du sikker på at du har nok?" +
                    "");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Du må gerne ta' lidt mere - men du kan også donere dit overskud til fællesskabet :)")
                    .setCancelable(false)
                    .setPositiveButton("Donér",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            new SetExtra(){

                                @Override
                                protected void onPostExecute(Boolean success) {
                                    //System.out.println("Return bool: "+canWe);

                                    if(success){
                                        System.out.println("Extra was added!");
                                    }
                                }

                            }.execute(veggie_name, ""+(allowedAmt-amountPacked), "");


                            packVeggie(veggie_name, true);
                        }
                    })
                    .setNegativeButton("Vej mere!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();


            Button donateBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button repackBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if(donateBtn != null && repackBtn != null){
                donateBtn.setBackground(getResources().getDrawable(R.drawable.green_button));
                repackBtn.setBackground(getResources().getDrawable(R.drawable.red_button));
            }

            // show it
            //ealertDialog.show();

        } else {

            packVeggie(veggie_name, false);
        }
    }

    private void packVeggie(String veggie_name, boolean hasExtra){

        System.out.println("JEG HAR NOK MAYN!");

        // sluk vægten!
        BluetoothGattCharacteristic characteristic = map
                .get(RBLService.UUID_BLE_SHIELD_TX);

        String offSign = "0";
        byte b = 0x00;
        byte[] tmp = offSign.getBytes();
        byte[] tx = new byte[tmp.length + 1];
        tx[0] = b;
        for (int i = 1; i < tmp.length; i++) {
            tx[i] = tmp[i - 1];
        }

        if(characteristic != null) {
            characteristic.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
        // unbind vores service
        unbindService(mServiceConnection);

        Intent data = new Intent();

        data.putExtra("veg_name", veggie_name);
        data.putExtra("packedAmt", amountPacked);
        data.putExtra("compensate", amountPacked);
        data.putExtra("loggedUser", username);

        if(hasExtra) {
            data.putExtra("hasExtra", true);
        }

        if(getParent()== null){
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }

        finish();
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
        System.out.println("onBack returned!");
        //unbindService(mServiceConnection);
        //finish();
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

        //System.out.println("Compensating with "+compensWeight);

        if (byteArray != null) {
            String data = new String(byteArray);
            Scanner in = new Scanner(data).useDelimiter("[^0-9]+");
            int parsed = in.nextInt();

            if(currAmt != null){
                currAmt.setText("" + (parsed-compensWeight));

                int prog = (int) Math.round((parsed-compensWeight) * 100.0/allowedAmt);

                if(pb != null) {
                    pb.setProgress(prog);
                }

                if(prog < 100) {
                    pb.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    amountPacked = parsed-compensWeight;
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

    private void sendToDevice(BluetoothGattCharacteristic characteristic, String out){


        // send tænd signal til vægten
         //characteristic = mBluetoothLeService.getSupportedGattService()
           //     .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);

        System.out.println(map.toString());
        characteristic = map.get(0);


        String onStr = out;
        byte b = 0x00;
        byte[] tmp = onStr.getBytes();
        byte[] tx = new byte[tmp.length + 1];
        tx[0] = b;
        for (int i = 1; i < tmp.length + 1; i++) {
            tx[i] = tmp[i - 1];
        }

        characteristic.setValue(tx);
        System.out.println("WRITING! "+tx);
        mBluetoothLeService.writeCharacteristic(characteristic);

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
