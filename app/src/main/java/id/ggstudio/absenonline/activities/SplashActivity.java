package id.ggstudio.absenonline.activities;

import androidx.appcompat.app.AppCompatActivity;
import id.ggstudio.absenonline.MainActivity;
import id.ggstudio.absenonline.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sp;

    private static final String MYPREF = "absensi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences(MYPREF, Context.MODE_PRIVATE);

        String username = sp.getString("username", "");

        if (TextUtils.isEmpty(username)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        finish();


    }
}