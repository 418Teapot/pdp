package dk.http418.oconn;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import dk.http418.oconn.*;
import dk.http418.oconn.Device;

public class Main extends Activity {
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final long SCAN_PERIOD = 3000;
	private Dialog mDialog;
	public static List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
	public static Main instance = null;

	private String scannedDevName = null;

	private boolean canProceed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		scannedDevName = getIntent().getStringExtra("devName");

		System.out.println("Forbinder til dev: "+scannedDevName);

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// kan brugeren få lov til at hente en pose?
		new GetPickup(){

			@Override
			protected void onPostExecute(Boolean canWe) {
				//System.out.println("Return bool: "+canWe);
				canProceed = canWe;
				System.out.println("Can we proceed?: "+canProceed);
				if(!canWe){
					Intent notAllowed = new Intent(getApplicationContext(), NotAllowed.class);
					notAllowed.putExtra("loggedUser", getIntent().getExtras().getString("loggedUser"));
					startActivity(notAllowed);
					finish();
				}
			}

		}.execute(getIntent().getStringExtra("loggedUser"));

		final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		/*Button btn = (Button)findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// hvis vi re-scanner efter devices (e.g.) nogen har booket et der er registreret
				// på denne måde fjernes det fra listen!

				if(mDevices.size() > 0){
					mDevices.clear();
				}
				scanLeDevice();

				showRoundProcessDialog(Main.this, R.layout.loading_process_dialog_anim);

				Timer mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						Intent deviceListIntent = new Intent(getApplicationContext(),
								dk.http418.oconn.Device.class);
						startActivity(deviceListIntent);
						mDialog.dismiss();
					}
				}, SCAN_PERIOD);
			}
		}); */

		scanLeDevice();

		//showRoundProcessDialog(Main.this, R.layout.loading_process_dialog_anim);

		//Timer mTimer = new Timer();
		/* mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Intent deviceListIntent = new Intent(getApplicationContext(),
						dk.http418.oconn.Device.class);
				startActivity(deviceListIntent);
				//mDialog.dismiss();
			}
		}, SCAN_PERIOD); */

		instance = this;
	}

	public void showRoundProcessDialog(Context mContext, int layout) {
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		};

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);
//		mDialog.show();
		// 娉ㄦ��姝ゅ��瑕���惧��show涔���� ������浼���ュ��甯�
		mDialog.setContentView(layout);
	}

	private void scanLeDevice() {
		new Thread() {

			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);

				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				if (mDevices.size() <= 0) {
					Intent noDev = new Intent(getApplicationContext(), NoWeightFound.class);
					startActivity(noDev);
					finish();
				} else if(mDevices.size() > 0 && canProceed){
					// vi fandt vores device!
					System.out.println("Checkin!");
					BluetoothDevice device = mDevices.get(0);
					if(device != null && scannedDevName.equalsIgnoreCase(device.getName())) {
						Intent selVelInt = new Intent(getApplicationContext(), SelectVeggie.class);
						selVelInt.putExtra("EXTRA_DEVICE_ADDRESS", device.getAddress());
						selVelInt.putExtra("EXTRA_DEVICE_NAME", device.getName());

						startActivity(selVelInt);
						Main.instance.finish();
						finish();
					} else {
						Intent noDev = new Intent(getApplicationContext(), NoWeightFound.class);
						startActivity(noDev);
						finish();
					}
				}
			}
		}.start();
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					//System.out.println("RUNNIN'! "+scanRecord.toString());
					if (scannedDevName != null) {
						if (device != null && scannedDevName.equalsIgnoreCase(device.getName())) {
							if (mDevices.indexOf(device) == -1)
								mDevices.add(device);

						}
					}
				}
			});
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		//System.exit(0);
	}
}
