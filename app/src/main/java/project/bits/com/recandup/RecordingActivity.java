package project.bits.com.recandup;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText url;
    private Camera camera;
    private LinearLayout showCase;
    private Button start,done;
    private CameraPreview cameraPreview;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = "Recording Activity";

    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = (EditText) findViewById(R.id.url_link);
        if (Prefs.getUrlPref(this).isEmpty()){
            Toast.makeText(this, "Enter a url in the box ", Toast.LENGTH_LONG).show();
        }else{
            url.setText(Prefs.getUrlPref(this));
        }

        startService(new Intent(this,ServiceManager.class));

        //dim the screen
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = 0;
        getWindow().setAttributes(params);

        camera = getCameraInstance();
        cameraPreview = new CameraPreview(this,camera);

        start = (Button) findViewById(R.id.start_video);
        done = (Button) findViewById(R.id.done);

        showCase = (LinearLayout) findViewById(R.id.video_showcase_layout);
        showCase.addView(cameraPreview);

        // Add a listener to the Capture button
        start.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean prepareVideoRecorder(){
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();

        mMediaRecorder.setCamera(camera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        //setting capture rate
        mMediaRecorder.setCaptureRate(10);

        mMediaRecorder.setMaxDuration(5000);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(this.getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                if (i == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
//                    Toast.makeText(RecordingActivity.this,"Video Time Done",Toast.LENGTH_SHORT).show();
                    Log.e("Video Time Done","Video Time Done");
                    releaseMediaRecorder();
                    if (prepareVideoRecorder()) {
                        // Camera is available and unlocked, MediaRecorder is prepared,
                        // now you can start recording
                        mMediaRecorder.start();
                        // inform the user that recording has started
                        start.setText("STOP");
//                        Toast.makeText(RecordingActivity.this,"Video Started",Toast.LENGTH_SHORT).show();
                        Log.e("Video Started","Video Started");
                        isRecording = true;
                    } else {
                        // prepare didn't work, release the camera
                        releaseMediaRecorder();
                        // inform user
                    }
                }
            }
        });

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            mMediaRecorder.release();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(this.getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        manager = new DBManager(this);

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        manager.addVideoAddress(mediaFile.getPath(),System.currentTimeMillis());
        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    @Override
    public void onClick(View view) {
        manager = new DBManager(this);
        switch (view.getId()){
            case R.id.done:
                Prefs.setUrl_link(this,url.getText().toString());
                break;
            case R.id.start_video:
                if (!Prefs.getUrlPref(this).isEmpty()) {
                    if (view.getId() == R.id.start_video) {
                        if (isRecording) {
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder();// release the MediaRecorder object
                            camera.lock();         // take camera access back from MediaRecorder
                            // inform the user that recording has stopped
                            start.setText("START");
                            isRecording = false;
                        } else {
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                mMediaRecorder.start();
                                // inform the user that recording has started
                                start.setText("STOP");
                                isRecording = true;
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                            }
                        }
                    }
                }else {
                    Toast.makeText(this,"Enter a url :",Toast.LENGTH_LONG).show();
                }
        }
    }
}