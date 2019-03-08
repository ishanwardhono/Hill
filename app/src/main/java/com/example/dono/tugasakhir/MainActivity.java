package com.example.dono.tugasakhir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btn_gen, btn_enk, btn_dek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_gen = findViewById(R.id.btn_generate);
        btn_gen.setOnClickListener(this);
        btn_enk = findViewById(R.id.btn_enkripsi);
        btn_enk.setOnClickListener(this);
        btn_dek = findViewById(R.id.btn_dekripsi);
        btn_dek.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_generate){
            Intent i = new Intent(MainActivity.this,GenerateActivity.class);
            startActivity(i);
        }else if(view.getId()==R.id.btn_enkripsi){
            Intent i = new Intent(MainActivity.this,EnkripsiActivity.class);
            startActivity(i);
        }else if(view.getId()==R.id.btn_dekripsi){
            Intent i = new Intent(MainActivity.this,DekripsiActivity.class);
            startActivity(i);
        }
    }
}
