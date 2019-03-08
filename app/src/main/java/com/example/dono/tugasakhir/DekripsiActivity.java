package com.example.dono.tugasakhir;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class DekripsiActivity extends AppCompatActivity implements View.OnClickListener{
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_PRIKEY_REQUEST = 2;
    private int PICK_CIPHERKEY_REQUEST = 3;
    Button inPriK, inCipherK, decKey, inCipherF, decFile, invMat;
    TextView tvp, tva, tvb, tvG, tvn, tvh;
    TextView tvPriKey, imgHeight, imgWidth;
    EditText namaFile;
    TextView[][] hillKey = new TextView[4][4];
    TextView[][] invHillKey = new TextView[4][4];
    Bitmap bitmap, cryptImage;
    ImageView ivFile;

    int[] cipherKey = new int[18];
    int[][] plainKey = new int[4][4];
    ECC ecc;
    Elgamal elgamal;
    HillCipher hill;
    boolean backEnable = true;
    String namaPF, ekstFileCitra;

    long runTimeDecImg, runTimeDecKey, runTimeInvMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dekripsi);

        inPriK = findViewById(R.id.inputPriKey);
        inPriK.setOnClickListener(this);
        inCipherK = findViewById(R.id.inputCipherKey);
        inCipherK.setOnClickListener(this);
        decKey = findViewById(R.id.btn_DecKey);
        decKey.setOnClickListener(this);
        inCipherF = findViewById(R.id.btn_inputCipherFile);
        inCipherF.setOnClickListener(this);
        decFile = findViewById(R.id.btn_decCipherFile);
        decFile.setOnClickListener(this);
        invMat = findViewById(R.id.btn_inversMatriks);
        invMat.setOnClickListener(this);

        tvp = findViewById(R.id.ec_pdec);
        tva = findViewById(R.id.ec_adec);
        tvb = findViewById(R.id.ec_bdec);
        tvG = findViewById(R.id.ec_Gdec);
        tvn = findViewById(R.id.ec_ndec);
        tvh = findViewById(R.id.ec_hdec);
        tvPriKey = findViewById(R.id.tv_elPriK);
        namaFile = findViewById(R.id.et_namaFile);
        ivFile = findViewById(R.id.ivFile);
        imgWidth = findViewById(R.id.tv_imgWidth);
        imgHeight = findViewById(R.id.tv_imgHeight);

        hillKey[0][0] = findViewById(R.id.tv_decHillKey11);
        hillKey[0][1] = findViewById(R.id.tv_decHillKey12);
        hillKey[0][2] = findViewById(R.id.tv_decHillKey13);
        hillKey[0][3] = findViewById(R.id.tv_decHillKey14);
        hillKey[1][0] = findViewById(R.id.tv_decHillKey21);
        hillKey[1][1] = findViewById(R.id.tv_decHillKey22);
        hillKey[1][2] = findViewById(R.id.tv_decHillKey23);
        hillKey[1][3] = findViewById(R.id.tv_decHillKey24);
        hillKey[2][0] = findViewById(R.id.tv_decHillKey31);
        hillKey[2][1] = findViewById(R.id.tv_decHillKey32);
        hillKey[2][2] = findViewById(R.id.tv_decHillKey33);
        hillKey[2][3] = findViewById(R.id.tv_decHillKey34);
        hillKey[3][0] = findViewById(R.id.tv_decHillKey41);
        hillKey[3][1] = findViewById(R.id.tv_decHillKey42);
        hillKey[3][2] = findViewById(R.id.tv_decHillKey43);
        hillKey[3][3] = findViewById(R.id.tv_decHillKey44);

        invHillKey[0][0] = findViewById(R.id.tv_invHillKey11);
        invHillKey[0][1] = findViewById(R.id.tv_invHillKey12);
        invHillKey[0][2] = findViewById(R.id.tv_invHillKey13);
        invHillKey[0][3] = findViewById(R.id.tv_invHillKey14);
        invHillKey[1][0] = findViewById(R.id.tv_invHillKey21);
        invHillKey[1][1] = findViewById(R.id.tv_invHillKey22);
        invHillKey[1][2] = findViewById(R.id.tv_invHillKey23);
        invHillKey[1][3] = findViewById(R.id.tv_invHillKey24);
        invHillKey[2][0] = findViewById(R.id.tv_invHillKey31);
        invHillKey[2][1] = findViewById(R.id.tv_invHillKey32);
        invHillKey[2][2] = findViewById(R.id.tv_invHillKey33);
        invHillKey[2][3] = findViewById(R.id.tv_invHillKey34);
        invHillKey[3][0] = findViewById(R.id.tv_invHillKey41);
        invHillKey[3][1] = findViewById(R.id.tv_invHillKey42);
        invHillKey[3][2] = findViewById(R.id.tv_invHillKey43);
        invHillKey[3][3] = findViewById(R.id.tv_invHillKey44);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                ekstFileCitra = getMimeType(this,uri);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivFile.setImageBitmap(bitmap);
                imgWidth.setText("Width : "+String.valueOf(bitmap.getWidth()));
                imgHeight.setText("Height : "+String.valueOf(bitmap.getHeight()));
                decFile.setEnabled(true);
                namaFile.setEnabled(true);
            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == PICK_PRIKEY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            String checkPath = uri.getPath();
            if(checkPath.contains(":")){
                String[] partPath = checkPath.split("\\:");
                path = root + "/"+ partPath[1];
            }
            try {
                File file = new File(path);
                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String textFile;
                while ((textFile = br.readLine()) != null) {
                    text.append(textFile);
                }
                br.close();
                try{
                    String[] textPriKey = text.toString().split(" ");
                    String[] titikG =textPriKey[3].split("[\\(||\\)||\\,]");
                    ecc = new ECC(Integer.parseInt(textPriKey[0]),Integer.parseInt(textPriKey[1]),Integer.parseInt(textPriKey[2]),
                            new Titik(Integer.parseInt(titikG[1]),Integer.parseInt(titikG[2])), Integer.parseInt(textPriKey[4]),
                            Integer.parseInt(textPriKey[5]));
                    tvp.setText(String.valueOf(ecc.p));
                    tva.setText(String.valueOf(ecc.a));
                    tvb.setText(String.valueOf(ecc.b));
                    tvG.setText(ecc.G.toString());
                    tvn.setText(String.valueOf(ecc.n));
                    tvh.setText(String.valueOf(ecc.h));

                    int priKey = Integer.parseInt(textPriKey[6]);
                    elgamal = new Elgamal(ecc,priKey);
                    tvPriKey.setText(String.valueOf(priKey));
                    inPriK.setEnabled(false);
                    inCipherK.setEnabled(true);
                    backEnable = false;
                }catch (Exception e){
                    Toast.makeText(this, "input tidak sesuai", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == PICK_CIPHERKEY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            String checkPath = uri.getPath();
            if(checkPath.contains(":")){
                String[] partPath = checkPath.split("\\:");
                path = root + "/"+ partPath[1];
            }
            try {
                File file = new File(path);
                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String textFile;
                while ((textFile = br.readLine()) != null) {
                    text.append(textFile);
                }
                br.close();
                try{
                    String[] textCipherKey = text.toString().split(" ");
                    for(int i=0;i<18;i++){
                        cipherKey[i] = Integer.parseInt(textCipherKey[i].toString());
                    }
                    inCipherK.setEnabled(false);
                    decKey.setEnabled(true);
                }catch (Exception e){
                    Toast.makeText(this, "input tidak sesuai", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Gagal Input", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.inputPriKey){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
            intent.setDataAndType(uri, "text/plain");
            startActivityForResult(Intent.createChooser(intent, "Select Private Key"), PICK_PRIKEY_REQUEST);
        }else if(view.getId()==R.id.inputCipherKey){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
            intent.setDataAndType(uri, "text/plain");
            startActivityForResult(Intent.createChooser(intent, "Select Cipher Key"), PICK_CIPHERKEY_REQUEST);
        }else if(view.getId()==R.id.btn_DecKey){
            new ProsesKeyDecrypt(this).execute();
        }else if(view.getId()==R.id.btn_inputCipherFile){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }else if(view.getId()==R.id.btn_inversMatriks){
            new ProsesInversMatriks(this).execute();
        }else if(view.getId()==R.id.btn_decCipherFile){
            namaPF = String.valueOf(namaFile.getText());
            if(namaPF.matches("")){
                namaFile.setError("Nama Kosong");
            }else{
                try{
                    cryptImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    new ProsesImageDecrypt(this).execute();
                } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void afterDecKey(){
        hill = new HillCipher(plainKey);
        decKey.setEnabled(false);
        backEnable = false;
        invMat.setEnabled(true);
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                hillKey[i][j].setText(String.valueOf(hill.getKey()[i][j]));
            }
        }
    }

    public void afterInvMat(){
        invMat.setEnabled(false);
        inCipherF.setEnabled(true);
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                invHillKey[i][j].setText(String.valueOf(hill.getInvKey()[i][j]));
            }
        }
    }

    public void savePlain(){
        try{
            ivFile.setImageBitmap(cryptImage);
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File myDir = new File(root + "/Skripsi");
            myDir.mkdirs();
            File pFile = new File(myDir, "PlainImage_"+namaPF+"."+ekstFileCitra);
            if (pFile.exists()) pFile.delete();
            FileOutputStream outFile = new FileOutputStream(pFile);
            cryptImage.compress(Bitmap.CompressFormat.PNG, 100, outFile);
            outFile.flush();
            outFile.close();
            namaFile.setEnabled(false);
            inCipherF.setEnabled(false);
            decFile.setEnabled(false);
            backEnable = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Proses Selesai");
            builder.setMessage("Running Time : \n" +
                    "Dekripsi Kunci : "+runTimeDecKey+" ms\n"+
                    "Invers Matriks : "+runTimeInvMat+" ms\n"+
                    "Dekripsi Citra : "+runTimeDecImg+" ms\n"+
                    "Keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    DekripsiActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class ProsesImageDecrypt extends AsyncTask<Void,Void,Void> {
        private ProgressDialog prgd;
        long timeAwal, timeAkhir;
        TextView rtDecImg;

        public ProsesImageDecrypt(DekripsiActivity activity) {
            prgd = new ProgressDialog(activity);
            rtDecImg = findViewById(R.id.tv_rtDecImg);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread() {
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    cryptImage = hill.decryption(cryptImage);
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
            prgd.setMessage("Masih Dekripsi Citra");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            timeAkhir = System.currentTimeMillis();
            runTimeDecImg = timeAkhir - timeAwal;
            rtDecImg.setText("Running Time : "+runTimeDecImg+" ms");
            savePlain();
            prgd.dismiss();
        }
    }

    private class ProsesKeyDecrypt extends AsyncTask<Void,Void,Void> {
        private ProgressDialog prgd;
        long timeAwal, timeAkhir;
        TextView rtDecKey;

        public ProsesKeyDecrypt(DekripsiActivity activity) {
            prgd = new ProgressDialog(activity);
            rtDecKey = findViewById(R.id.tv_rtDecKey);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread() {
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    plainKey = elgamal.decryption(cipherKey);
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
            prgd.setMessage("Masih Dekripsi Kunci EC Elgamal");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            timeAkhir = System.currentTimeMillis();
            runTimeDecKey = timeAkhir - timeAwal;
            rtDecKey.setText("Running Time : "+runTimeDecKey+" ms");
            afterDecKey();
            prgd.dismiss();
        }
    }

    private class ProsesInversMatriks extends AsyncTask<Void,Void,Void> {
        private ProgressDialog prgd;
        long timeAwal, timeAkhir;
        TextView rtInvMat;

        public ProsesInversMatriks(DekripsiActivity activity) {
            prgd = new ProgressDialog(activity);
            rtInvMat = findViewById(R.id.tv_rtInvMat);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread() {
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    hill.inversMatriks();
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
            prgd.setMessage("Masih Invers Matriks Kunci");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            timeAkhir = System.currentTimeMillis();
            runTimeInvMat = timeAkhir - timeAwal;
            rtInvMat.setText("Running Time : "+runTimeInvMat+" ms");
            afterInvMat();
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
                    DekripsiActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });
            builder.show();
        }else{
            DekripsiActivity.super.onBackPressed();
        }
    }

    public String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }
}
