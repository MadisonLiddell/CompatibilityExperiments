package edu.rasmussen.Compatibility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles main interactions with app, starts activity to take image, saves image.
 */
public class MainActivity extends AppCompatActivity
{
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private FragmentRefreshListener fragmentRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // setup toolbar as actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (checkCameraHardware(getApplicationContext()))
            getMenuInflater().inflate(R.menu.menu, menu);
        else
            getMenuInflater().inflate(R.menu.menu_no_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_picture:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file up
                // launch camera taking activity
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        File mediaStorageDir;
        File mediaFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
             mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CompatibilityCamera");
            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("Camera", "failed to create directory");
                    return null;
                }
            } else {
                // Create a media file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(new Date());
                if (type == MEDIA_TYPE_IMAGE){
                    mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                                                 "IMG_"+ timeStamp + ".jpg");
                }else {
                    return null;
                }
            }
        } else
        {
            Log.d("Camera", "couldn't access external storage.");
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved", Toast.LENGTH_LONG).show();
                updateFragment();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
                Toast.makeText(this, "Failed to save image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateFragment()
    {
        if(getFragmentRefreshListener()!=null){
            getFragmentRefreshListener().onRefresh();
        }
//        // Reload current fragment
//        Fragment fragment;
//
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            fragment = getSupportFragmentManager().findFragmentById(R.id.imageListFragmentLand);
//
//            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.detach(fragment);
//            ft.attach(fragment);
//            ft.commit();
//        } else {
//            fragment = getSupportFragmentManager().findFragmentById(R.id.imageListFragmentPort);
//            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.detach(fragment);
//            ft.attach(fragment);
//            ft.commit();
//        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }
}
