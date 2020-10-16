package id.ggstudio.absenonline.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import id.ggstudio.absenonline.MainActivity;
import id.ggstudio.absenonline.R;
import id.ggstudio.absenonline.databinding.ActivityMainBinding;
import id.ggstudio.absenonline.databinding.ActivityProgressBinding;
import id.ggstudio.absenonline.model.Absen;
import id.ggstudio.absenonline.service.AbsenService;
import id.ggstudio.absenonline.util.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ProgressActivity extends AppCompatActivity {

    private ActivityProgressBinding binding;

    private Retrofit retrofit;
    private AbsenService absenService;
    private Absen absen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String photoPath = getIntent().getStringExtra("photoPath");
        String latitude = getIntent().getStringExtra("latitude");
        String longitude = getIntent().getStringExtra("longitude");

        Bitmap bm = BitmapFactory.decodeFile(photoPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


        retrofit = ApiClient.getInstance();
        absenService = retrofit.create(AbsenService.class);

//        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
//        circularProgressDrawable.setStrokeWidth(5f);
//        circularProgressDrawable.setCenterRadius(30f);
//        circularProgressDrawable.start();

        Absen absen = new Absen();
        absen.setId(UUID.randomUUID().toString());
        absen.setImage(encodedImage);
        absen.setLongitude(longitude);
        absen.setLatitude(latitude);


        absenService.postAbsen(absen).enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                Toast.makeText(ProgressActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                binding.progress.setVisibility(View.GONE);
                binding.imgProgress.setVisibility(View.VISIBLE);

                Glide.with(ProgressActivity.this)
                        .load(R.drawable.success)
                        .into(binding.imgProgress);

                binding.txtStatus.setText("Succesfully Sending Data");
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(ProgressActivity.this, "Gagal", Toast.LENGTH_SHORT).show();

                binding.imgProgress.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(View.GONE);

                Glide.with(ProgressActivity.this)
                        .load(R.drawable.error)
                        .into(binding.imgProgress);

                binding.txtStatus.setText("Error Sending Data");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



    }
}