package project.bits.com.recandup;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button video;
    FloatingActionButton upload;

    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int FILE_CODE = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        video = (Button) findViewById(R.id.take_video);
        upload = (FloatingActionButton) findViewById(R.id.upload);

        video.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    static Uri capturedMediaUri=null;

    @Override
    public void onClick(View view) {
        Calendar cal;
        File folder;
        switch (view.getId()){
            case R.id.take_video:
                cal = Calendar.getInstance();
                folder = new File(Environment.getExternalStorageDirectory(), "PROJECT_MULTIMEDIA");
                folder.mkdirs();
                File video = new File(folder,cal.getTimeInMillis()+".mp4");
                if(!video.exists()){
                    try {
                        video.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }else{
                    video.delete();
                    try {
                        video.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                capturedMediaUri = Uri.fromFile(video);
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedMediaUri);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                break;
            case R.id.upload:
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(this, FilePickerActivity.class);
                    intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                    intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                    File file = new File(Environment.getExternalStorageDirectory(), "PROJECT_MULTIMEDIA");
                    intent.putExtra(FilePickerActivity.EXTRA_START_PATH, file.getAbsolutePath());
                    startActivityForResult(intent, FILE_CODE);
                }else {
                    Toast.makeText(this,"Check Internet Connection...",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://bits-project-1c0d0.appspot.com/");
                StorageReference mediaRef = storageRef.child("media/"+uri.getLastPathSegment());
                final UploadTask uploadTask = mediaRef.putFile(uri);
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Uploading Please Wait...");
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        uploadTask.cancel();
                        Toast.makeText(MainActivity.this,"Upload cancelled!!!",Toast.LENGTH_LONG).show();
                    }
                });
                dialog.show();

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(MainActivity.this,"Failed to upload",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        dialog.dismiss();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(MainActivity.this,"Upload Successful",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
