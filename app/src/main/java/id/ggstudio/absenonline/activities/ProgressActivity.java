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

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ProgressActivity extends AppCompatActivity {

    private ActivityProgressBinding binding;

    private Retrofit retrofit;
    private AbsenService absenService;
    private Absen absen;

    private SharedPreferences sp;
    private static final String MYPREF = "absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sp = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

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

        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        int idCompany = sp.getInt("idCompany",9999);

        Absen absen = new Absen();
        absen.setId(UUID.randomUUID().toString());
        absen.setImage(encodedImage);
        absen.setLongitude(longitude);
        absen.setLatitude(latitude);
        absen.setNik(sp.getString("username","-"));
        absen.setCompany(String.valueOf(idCompany));

        absenService.postAbsen(absen).enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                Toast.makeText(ProgressActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                binding.progress.setVisibility(View.GONE);
                binding.imgProgress.setVisibility(View.VISIBLE);

                Glide.with(ProgressActivity.this)
                        .load(R.drawable.success)
                        .into(binding.imgProgress);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                String date = sdf.format(new Date());


                editor.putString("last_attend", date);
                editor.apply();

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