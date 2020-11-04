package id.ggstudio.absenonline;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import id.ggstudio.absenonline.activities.ProgressActivity;
import id.ggstudio.absenonline.databinding.ActivityMainBinding;
import id.ggstudio.absenonline.model.Absen;
import id.ggstudio.absenonline.service.AbsenService;
import id.ggstudio.absenonline.util.ApiClient;
import id.ggstudio.absenonline.util.GeoLocation;
import id.ggstudio.absenonline.util.ImageCompression;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PermissionListener, View.OnClickListener {

    private ActivityMainBinding binding;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath;
    private String mCompressedPath;

    private Retrofit retrofit;
    private AbsenService absenService;
    private String encodedImage;

    private String latitude;
    private String longitude;

    private SharedPreferences sp;

    private static final String MYPREF = "absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sp = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check();

        retrofit = ApiClient.getInstance();
        absenService = retrofit.create(AbsenService.class);

        binding.btnSendAbsen.setOnClickListener(this);
        binding.cam.setOnClickListener(this);

        String username = sp.getString("username","-");
        String lastAttend = sp.getString("last_attend","-");

        binding.txtUsername.setText(username);
        binding.txtLastAttend.setText(lastAttend);

        Glide.with(this)
                .load(R.drawable.cam)
                .into(binding.cam);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Settings");


        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(binding.mainToolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        item1,
                        item2
                        //pass your items here
                )
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_absen:

                if (TextUtils.isEmpty(mCurrentPhotoPath)) {
                    Toast.makeText(this, "Image Not Detected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Absen absen = new Absen();
                absen.setId(UUID.randomUUID().toString());
                absen.setImage(encodedImage);
                absen.setLatitude(latitude);
                absen.setLongitude(longitude);
                absen.setNik(sp.getString("username",""));
                absen.setCompany(sp.getString("abbrCompany",""));

                Intent intent = new Intent(this, ProgressActivity.class);
                intent.putExtra("photoPath", mCompressedPath);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);

                startActivity(intent);
                finish();

                break;
            case R.id.cam:
                Dexter.withContext(this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(this)
                        .check();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap data1 = (Bitmap) extras.get("data");

            File file = new File(mCurrentPhotoPath);

            ImageCompression compression = new ImageCompression(this);
            mCompressedPath = compression.compressImage(mCurrentPhotoPath);


            Uri contentURI = Uri.fromFile(file);
            Glide.with(this)
                    .load(contentURI)
                    .into(binding.mainImage);

            binding.cam.setVisibility(View.GONE);
            binding.txtTap.setVisibility(View.GONE);

        }
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
        switch (permissionGrantedResponse.getPermissionName()) {
            case Manifest.permission.CAMERA:
                dispatchTakePictureIntent();
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                GeoLocation geoLocation = new GeoLocation(this);
                longitude = String.valueOf(geoLocation.getLongitude());
                latitude = String.valueOf(geoLocation.getLatitude());
                break;
            default:
                break;
        }
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

    }

    private String encodeImageToBase64(String fileName) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(fileName); // You can get an inputStream using any I/O API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        bytes = output.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void sendAbsen() {

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;

    }

    @Override
    protected void onStart() {
        super.onStart();

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check();
    }
}