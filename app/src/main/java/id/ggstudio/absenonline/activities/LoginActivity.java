package id.ggstudio.absenonline.activities;

import androidx.appcompat.app.AppCompatActivity;
import id.ggstudio.absenonline.MainActivity;
import id.ggstudio.absenonline.R;
import id.ggstudio.absenonline.databinding.ActivityLoginBinding;
import id.ggstudio.absenonline.model.Company;
import id.ggstudio.absenonline.service.AbsenService;
import id.ggstudio.absenonline.util.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private Retrofit retrofit;
    private AbsenService absenService;


    private SharedPreferences sp;

    private static final String MYPREF = "absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = ApiClient.getInstance();
        absenService = retrofit.create(AbsenService.class);

        sp = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);


        absenService.getCompanies().enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(Call<List<Company>> call, Response<List<Company>> response) {
                if (response.code() == 200) {
                    List<Company> companies = response.body();

                    ArrayAdapter<Company> adapter = new ArrayAdapter<>(LoginActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, companies);

                    binding.spinnerCompany.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Company>> call, Throwable t) {

            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(binding.inputUsername.getText())) {
                    binding.inputUsername.setError("Required");
                    return;
                }

                Company company = (Company) binding.spinnerCompany.getSelectedItem();

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", binding.inputUsername.getText().toString());
                editor.putString("company", String.valueOf(company.getNameCompany()));
                editor.putString("abbrCompany", String.valueOf(company.getAbbrCompany()));
                editor.apply();

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Glide.with(this)
                .load(R.drawable.splash_screen_logo)
                .fitCenter()
                .into(binding.loginImg);
    }
}