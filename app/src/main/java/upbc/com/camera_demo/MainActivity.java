package upbc.com.camera_demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.soundcloud.android.crop.Crop;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import upbc.com.camera_demo.volley.AppController;

public class MainActivity extends AppCompatActivity {
    Uri selectedImageUri, destination;
    ImageView iv_user_Profile;
    Button btn_Capture;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        iv_user_Profile = (ImageView) findViewById(R.id.iv_user_Profile);
        btn_Capture = (Button) findViewById(R.id.btn_capture);
        btn_Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    selectedImageUri = Uri.fromFile(new File(getApplicationContext().getExternalCacheDir().getPath(), "pickImageResults.jpeg"));
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                    startActivityForResult(captureIntent, Crop.REQUEST_PICK);
                } catch (Exception e) {

                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(selectedImageUri);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                bitmap = getBitmapFromUri(selectedImageUri);
                iv_user_Profile.setImageBitmap(bitmap);
                System.out.println("------selectedUri-----//" + bitmap);
            } catch (IOException e) {
                System.out.println("------exception-----//" + e);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
