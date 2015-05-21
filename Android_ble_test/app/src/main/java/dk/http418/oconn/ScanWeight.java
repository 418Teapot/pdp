package dk.http418.oconn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.Toast;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class ScanWeight extends Activity {

    // QR Code ting!
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_weight);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        scanIt();

    }

    public void scanIt(){
        // åbn barcode scanneren

        if(mCamera==null){

            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            autoFocusHandler = new Handler();
            mCamera = getCameraInstance();
            this.getWindowManager().getDefaultDisplay().getRotation();

            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            FrameLayout preview = (FrameLayout)findViewById(R.id.QRCamera);
            preview.addView(mPreview);
        }

    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    public void onResume(){
        super.onResume();

        try {
            if(mCamera==null){

                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                autoFocusHandler = new Handler();
                mCamera = getCameraInstance();
                this.getWindowManager().getDefaultDisplay().getRotation();

                scanner = new ImageScanner();
                scanner.setConfig(0, Config.X_DENSITY, 3);
                scanner.setConfig(0, Config.Y_DENSITY, 3);

                mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
                FrameLayout preview = (FrameLayout)findViewById(R.id.QRCamera);
                preview.addView(mPreview);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block

        }
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
            mPreview= null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    String res = sym.getData().toString();
                    if(res.contains("oconn")){
                        // nu har vi fanget en QR kode! der tilhører os!
                        System.out.println("HELL YEAH!");
                        barcodeScanned = true;

                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        intent.putExtra("devName", res);
                        startActivity(intent);
                        finish();

                    } else {
                        System.out.println("NOT HELL YEAH!");
                        barcodeScanned = false;
                        Context context = getApplicationContext();
                        CharSequence text = "Fejl i scan - prøver igen!";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        releaseCamera(); // fri giv så vi kan genscanne!

                        Intent intent = getIntent();
                        overridePendingTransition(0, 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);

                    }
                    //scanText.setText("barcode result " + sym.getData());
                    //barcodeScanned = true;
                }
            }
        }
    };


}
