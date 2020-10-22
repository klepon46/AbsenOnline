package id.ggstudio.absenonline.activities;

import androidx.appcompat.app.AppCompatActivity;
import id.ggstudio.absenonline.MainActivity;
import id.ggstudio.absenonline.R;
import id.ggstudio.absenonline.databinding.ActivityLoginBinding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private SharedPreferences sp;

    private static final String MYPREF = "absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(binding.inputUsername.getText())) {
                    binding.inputUsername.setError("Required");
                    return;
                }

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", binding.inputUsername.getText().toString());
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