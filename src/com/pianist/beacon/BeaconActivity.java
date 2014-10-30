package com.pianist.beacon;

import java.util.HashMap;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.os.Build;

public class BeaconActivity extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static TextView textView;
	private static final String ADDRESS_A = "D7:83:D7:1D:A1:D9";
	private HashMap<String, Integer> strengths = new HashMap<String, Integer>();
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
	        new BluetoothAdapter.LeScanCallback() {
	    @Override
	    public void onLeScan(final BluetoothDevice device, final int rssi,
	            byte[] scanRecord) {
	        runOnUiThread(new Runnable() {
	           @Override
	           public void run() {
	        	   System.out.println("device");
	        	   
	        	   if (device.getAddress().equals(ADDRESS_A)) {
	        		   if (strengths.get("A") == null) {
	        			   strengths.put("A", rssi);
	        		   } else {
	        			   strengths.put("A", (int)(strengths.get("A") * 0.92 + rssi * 0.08));
	        		   }
	        	   } else {
	        		   if (strengths.get("B") == null) {
	        			   strengths.put("B", rssi);
	        		   } else {
	        			   strengths.put("B", (int)(strengths.get("B") * 0.92 + rssi * 0.08));
	        		   }
	        	   }
	        	   
	        	   if (strengths.get("A") < strengths.get("B")) {
	        		   textView.setText("B");
	        		   textView.setTextColor(Color.RED);
	        	   } else {
	        		   textView.setText("A");
	        		   textView.setTextColor(Color.BLUE);
	        	   }
	           }
	       });
	   }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_beacon);
		
		strengths.put("A", Integer.MIN_VALUE);
		strengths.put("B", Integer.MIN_VALUE);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		final BluetoothManager bluetoothManager =
		        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		if (mBluetoothAdapter == null) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		    mBluetoothAdapter = bluetoothManager.getAdapter();
		} else if (!mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.enable();
		}

		scanLeDevice(true);
	}
	
	 private void scanLeDevice(final boolean enable) {
	    if (enable) {
	        mBluetoothAdapter.startLeScan(mLeScanCallback);
	    } else {
	        mBluetoothAdapter.stopLeScan(mLeScanCallback);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beacon, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_beacon,
					container, false);
			textView = (TextView)rootView.findViewById(R.id.beacon_text);
			return rootView;
		}
	}

}
