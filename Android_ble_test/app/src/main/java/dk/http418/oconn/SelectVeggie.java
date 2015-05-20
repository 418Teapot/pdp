package dk.http418.oconn;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import dk.http418.oconn.Device;
import dk.http418.oconn.R;
import dk.http418.oconn.RBLService;

public class SelectVeggie extends Activity {
	private final static String TAG = dk.http418.oconn.SelectVeggie.class.getSimpleName();

	public static final String EXTRAS_DEVICE = "EXTRAS_DEVICE";
	private TextView tv = null;
	private EditText et = null;
	private Button btn = null;
	private ListView lv = null;
	private String mDeviceName;
	private String mDeviceAddress;
	private RBLService mBluetoothLeService;
	private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

	private VeggieAdapter v_adapter;
	private ArrayList<Veggie> vegetables;

	private Context ctx;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((RBLService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}

			// if dev is demo

			if(mDeviceName != null) {
				if (mDeviceName.equals("DemoDevice")) {
					System.out.println("APP: Demo");
				}
			} else {

				mBluetoothLeService.connect(mDeviceAddress);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
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


	private void getVeggies() {
		new GetVeggies(){
			@Override
			public void onPostExecute(ArrayList<Veggie> veggies){
				vegetables = veggies;
				Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
				for(Veggie v : vegetables){
					v.setStatusImg(transparentDrawable);
				}
				lv = (ListView) findViewById(R.id.veggieList);
				v_adapter = new VeggieAdapter(ctx, R.layout.veggie_list, vegetables);
				lv.setAdapter(v_adapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

						Veggie selVeggie = (Veggie) lv.getItemAtPosition(i);

						if(selVeggie.isPacked())
						Toast.makeText(getApplicationContext(),
								"Du har valgt: "+selVeggie.getName(), Toast.LENGTH_LONG).show();

						Intent veggieIntent = new Intent(ctx, VeggieWeight.class);
						//veggieIntent.putExtras()
						veggieIntent.putExtra("Name", selVeggie.getName());
						veggieIntent.putExtra("Amt", selVeggie.getAmount());
						//veggieIntent.putExtra("veggie", selVeggie);
						veggieIntent.putExtra("btName", mDeviceName);
						veggieIntent.putExtra("btAdr", mDeviceAddress);
						startActivityForResult(veggieIntent, 1);

					}
				});
				for(Veggie v : vegetables){
					if(v.getName().equals("Æbler")){
						v.setImg(getResources().getDrawable(R.drawable.aebler));
					} else if(v.getName().equals("Ærter")){
						v.setImg(getResources().getDrawable(R.drawable.aerter));
					} else if(v.getName().equals("Cheese")){
						v.setImg(getResources().getDrawable(R.drawable.cheese));
					} else if(v.getName().equals("Kaffe")){
						v.setImg(getResources().getDrawable(R.drawable.kaffe));
				 	} else if(v.getName().equals("Peberfrugt")){
						v.setImg(getResources().getDrawable(R.drawable.peberfrugt));
					} else {
						v.setImg(getResources().getDrawable(R.drawable.test));
					}
				}
			}

		}.execute("");
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data){
		for(Veggie v : vegetables){
			if(v.getName().equals(data.getStringExtra("veg_name"))){
				v.setWasPacked(true);
				v.setCollected(data.getIntExtra("packedAmt", 0));
				v.setStatusImg(getResources().getDrawable(R.drawable.checkmark));
				System.out.println("Vi har opdateret " + data.getStringExtra("veg_name") + " med " + data.getIntExtra("packedAmt", 0) + " g");
				v_adapter.notifyDataSetChanged();

			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);

		ctx = this;
		getVeggies();

		Intent intent = getIntent();

		mDeviceAddress = intent.getStringExtra(Device.EXTRA_DEVICE_ADDRESS);
		mDeviceName = intent.getStringExtra(Device.EXTRA_DEVICE_NAME);

		getActionBar().setTitle("Vægt "+mDeviceName+" - Vælg Grøntsag");
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent gattServiceIntent = new Intent(this, RBLService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
			System.exit(0);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mBluetoothLeService.disconnect();
		mBluetoothLeService.close();

		System.exit(0);
	}

	private void displayData(byte[] byteArray) {
		/*
		if (byteArray != null) {
			String data = new String(byteArray);
			tv.append(data);
			// find the amount we need to scroll. This works by
			// asking the TextView's internal layout for the position
			// of the final line and then subtracting the TextView's height
			final int scrollAmount = tv.getLayout().getLineTop(
					tv.getLineCount())
					- tv.getHeight();
			// if there is no need to scroll, scrollAmount will be <=0
			if (scrollAmount > 0)
				tv.scrollTo(0, scrollAmount);
			else
				tv.scrollTo(0, 0);
		}*/
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