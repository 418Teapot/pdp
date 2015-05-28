package dk.http418.oconn;

import java.util.ArrayList;

import android.app.Activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SelectVeggie extends Activity {
	private final static String TAG = dk.http418.oconn.SelectVeggie.class.getSimpleName();

	public static final String PREFS_NAME = "oconnSettings";
	public static final String LOGON_STR = "loggedOn";

	private ListView lv = null;
	private String mDeviceName;
	private String mDeviceAddress;

	private VeggieAdapter v_adapter;
	private ArrayList<Veggie> vegetables;

	private Context ctx;

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

		getActionBar().setDisplayHomeAsUpEnabled(false);

		Button chkout_btn = (Button) findViewById(R.id.checkout_knap);
		if(chkout_btn != null){
			chkout_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
						
				}
			});
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_selveggie, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			//System.exit(0);
		}

		if(item.getItemId() == R.id.logout) {

			SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor edit = settings.edit();
			edit.putBoolean(LOGON_STR, false);
			edit.commit();

			// gå til login screen
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


}
