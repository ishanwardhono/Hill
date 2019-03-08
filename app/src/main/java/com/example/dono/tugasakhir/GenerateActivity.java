package com.example.dono.tugasakhir;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class GenerateActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvp, tva, tvb, tvG, tvn, tvh, tvpubK, tvpriK;
    Button genECC, randK, genElgamal, saveKey;
    ECC ecc;
    Elgamal elgamal;
    EditText priKey, namaKey;
    Random rand = new Random();
    boolean backEnable = true;

    long runTimeGenEcc, runTimeGenElgamal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        tvp = findViewById(R.id.ec_p);
        tva = findViewById(R.id.ec_a);
        tvb = findViewById(R.id.ec_b);
        tvG = findViewById(R.id.ec_G);
        tvn = findViewById(R.id.ec_n);
        tvh = findViewById(R.id.ec_h);
        genECC = findViewById(R.id.btn_genECC);
        genECC.setOnClickListener(this);

        tvpubK = findViewById(R.id.elgamal_PubKey);
        tvpriK = findViewById(R.id.elgamal_PriKey);

        randK = findViewById(R.id.btn_random);
        randK.setOnClickListener(this);
        genElgamal = findViewById(R.id.btn_genElgamalKey);
        genElgamal.setOnClickListener(this);
        saveKey = findViewById(R.id.btn_saveKunci);
        saveKey.setOnClickListener(this);

        priKey = findViewById(R.id.et_privateKey);
        namaKey = findViewById(R.id.et_namaKunci);
        setElgamalComp_false();
    }

    public void setElgamalComp_false(){
        tvpubK.setEnabled(false);
        tvpriK.setEnabled(false);
        randK.setEnabled(false);
        genElgamal.setEnabled(false);
        saveKey.setEnabled(false);
        priKey.setEnabled(false);
        namaKey.setEnabled(false);
    }

    public void setElgamalComp_true(){
        tvpubK.setEnabled(true);
        tvpriK.setEnabled(true);
        randK.setEnabled(true);
        genElgamal.setEnabled(true);
        priKey.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_genECC){
            try{
                new ProsesGenerateCurve(this,tvp,tva,tvb,tvG,tvn,tvh,priKey).execute();
                setElgamalComp_true();
                backEnable = false;
            }catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }else if(view.getId()==R.id.btn_random){
            priKey.setText(String.valueOf(rand.nextInt(ecc.n - 1) + 1));
        }else if(view.getId()==R.id.btn_genElgamalKey){
            int etPK_input = Integer.parseInt(String.valueOf(priKey.getText()));
            if(etPK_input < 1 || etPK_input > ecc.n -1){
                priKey.setError("Input Salah");
            }else{
                try{
                    new ProsesGenerateElgamalKey(this, etPK_input).execute();
                }catch (Exception e){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }else if(view.getId()==R.id.btn_saveKunci){
            String namaK = String.valueOf(namaKey.getText());
            if(namaK.matches("")){
                namaKey.setError("Nama Kosong");
            }else{

                String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                File myDir = new File(root + "/Skripsi");
                myDir.mkdirs();

                String namaPubKey = "PublicKey_"+namaK+".txt";
                File filePubKey = new File (myDir, namaPubKey);
                if (filePubKey.exists ()) filePubKey.delete ();
                String namaPriKey = "PrivateKey_"+namaK+".txt";
                File filePriKey = new File (myDir, namaPriKey);
                if (filePriKey.exists ()) filePriKey.delete ();

                String textPubKey = "";
                textPubKey += String.valueOf(ecc.p) + " ";
                textPubKey += String.valueOf(ecc.a) + " ";
                textPubKey += String.valueOf(ecc.b) + " ";
                textPubKey += ecc.G.toString() + " ";
                textPubKey += String.valueOf(ecc.n) + " ";
                textPubKey += String.valueOf(ecc.h) + " ";
                textPubKey += elgamal.publicKey.toString() + " ";

                String textPriKey = "";
                textPriKey += String.valueOf(ecc.p) + " ";
                textPriKey += String.valueOf(ecc.a) + " ";
                textPriKey += String.valueOf(ecc.b) + " ";
                textPriKey += ecc.G.toString() + " ";
                textPriKey += String.valueOf(ecc.n) + " ";
                textPriKey += String.valueOf(ecc.h) + " ";
                textPriKey += String.valueOf(elgamal.getPrivateKey()) + " ";
                try{
                    FileOutputStream fileOutputStreamPubKey = new FileOutputStream(filePubKey,true);
                    fileOutputStreamPubKey.write(textPubKey.getBytes());
                    FileOutputStream fileOutputStreamPriKey = new FileOutputStream(filePriKey,true);
                    fileOutputStreamPriKey.write(textPriKey.getBytes());
                    Toast.makeText(this, "Kunci Tersimpan", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
                setElgamalComp_false();
                genECC.setEnabled(false);
                backEnable = true;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Proses Selesai");
                builder.setMessage("Running Time : \n" +
                        "Generate Kurva Eliptik : "+runTimeGenEcc+" ms\n"+
                        "Generate Kunci Elgamal : "+runTimeGenElgamal+" ms\n"+
                        "Keluar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GenerateActivity.super.onBackPressed();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
            }
        }
    }

    private class ProsesGenerateCurve extends AsyncTask<Void,Void,Void>{
        private ProgressDialog prgd;
        TextView tvp_at, tva_at, tvb_at, tvG_at, tvn_at, tvh_at, rtGenECC;
        EditText et_PK;
        long timeAwal,timeAkhir;

        public ProsesGenerateCurve(GenerateActivity activity, TextView tvp,TextView tva,TextView tvb,TextView tvG,TextView tvn,TextView tvh, EditText etPK){
            prgd = new ProgressDialog(activity);
            tvp_at = tvp;
            tva_at = tva;
            tvb_at = tvb;
            tvG_at = tvG;
            tvn_at = tvn;
            tvh_at = tvh;
            et_PK = etPK;
            rtGenECC = findViewById(R.id.tv_rtGenECC);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread(){
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    ecc = new ECC();
                }
            };
            tr.start();
            try {
                tr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //Create progress dialog here and show it
            prgd.setTitle("Tunggu");
            prgd.setMessage("Masih Generate Kurva Eliptik");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            timeAkhir = System.currentTimeMillis();
            runTimeGenEcc = timeAkhir - timeAwal;
            rtGenECC.setText("Running Time : "+runTimeGenEcc+" ms");

            tvp_at.setText(String.valueOf(ecc.p));
            tva_at.setText(String.valueOf(ecc.a));
            tvb_at.setText(String.valueOf(ecc.b));
            tvG_at.setText(ecc.G.toString());
            tvn_at.setText(String.valueOf(ecc.n));
            tvh_at.setText(String.valueOf(ecc.h));
            et_PK.setHint("Private Key (1 - "+(ecc.n-1)+" )");
            prgd.dismiss();
        }
    }

    private class ProsesGenerateElgamalKey extends AsyncTask<Void,Void,Void> {
        private ProgressDialog prgd;
        int etPK_input;
        EditText namaKey,priKey;
        TextView tvpriK, tvpubK, rtGenElgamal;
        Button saveKey;
        boolean flag = false;
        long timeAwal,timeAkhir;

        public ProsesGenerateElgamalKey(GenerateActivity activity,int etPK_input) {
            prgd = new ProgressDialog(activity);
            this.etPK_input = etPK_input;

            tvpubK = findViewById(R.id.elgamal_PubKey);
            tvpriK = findViewById(R.id.elgamal_PriKey);
            saveKey = findViewById(R.id.btn_saveKunci);
            namaKey = findViewById(R.id.et_namaKunci);
            priKey = findViewById(R.id.et_privateKey);
            rtGenElgamal = findViewById(R.id.tv_rtGenElgamal);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread() {
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    elgamal = new Elgamal(ecc);
                    flag = elgamal.generateKey(etPK_input);
                }
            };
            tr.start();
            try {
                tr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            //Create progress dialog here and show it
            prgd.setTitle("Tunggu");
            prgd.setMessage("Masih Generate Kunci Elgamal");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            timeAkhir = System.currentTimeMillis();
            runTimeGenElgamal = timeAkhir - timeAwal;
            rtGenElgamal.setText("Running Time : "+runTimeGenElgamal+" ms");
            if(flag){
                tvpriK.setText(String.valueOf(elgamal.getPrivateKey()));
                tvpubK.setText(elgamal.publicKey.toString());

                saveKey.setEnabled(true);
                namaKey.setEnabled(true);
            }else{
                priKey.setError("Kunci Publik Tidak Sesuai");
            }
            prgd.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(!backEnable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Proses Belum Selesai");
            builder.setMessage("Proses Akan Dibatalkan Jika Anda Keluar.\nAnda Yakin Mau Keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GenerateActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });
            builder.show();
        }else{
            GenerateActivity.super.onBackPressed();
        }
    }
}
