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
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Random;

public class EnkripsiActivity extends AppCompatActivity implements View.OnClickListener{
    Random rand = new Random();
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_PUBKEY_REQUEST = 2;

    ImageView ivFile;
    Button inFile, genHill, encHill, inPubKey, encKey, randR;
    TextView pubKey, imgHeight, imgWidth, rtGenMat;
    TextView tvp, tva, tvb, tvG, tvn, tvh;
    Bitmap bitmap, cryptImage;
    TextView[][] hillKey = new TextView[4][4];
    EditText namaCipher, namaKunci, bilR;
    boolean backEnable = true;

    HillCipher hill;
    ECC ecc;
    Elgamal elgamal;
    int RandomBil;

    String namaCF, namaK, ekstFileCitra;
    int[] cipherKey;

    long runTimeEncImg, runTimeEncKey, runTimeGenMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enkripsi);

        inFile = findViewById(R.id.btn_inFile);
        inFile.setOnClickListener(this);
        ivFile = findViewById(R.id.ivFile);

        genHill = findViewById(R.id.btn_genHill);
        genHill.setOnClickListener(this);
        encHill = findViewById(R.id.btn_encHill);
        encHill.setOnClickListener(this);
        inPubKey = findViewById(R.id.btn_inPubKey);
        inPubKey.setOnClickListener(this);
        encKey = findViewById(R.id.btn_encKey);
        encKey.setOnClickListener(this);
        randR = findViewById(R.id.btn_randBil);
        randR.setOnClickListener(this);

        bilR = findViewById(R.id.et_randBil);
        namaCipher = findViewById(R.id.et_namaFileCipher);
        namaKunci = findViewById(R.id.et_namaKeyCipher);

        imgWidth = findViewById(R.id.tv_imgWidth);
        imgHeight = findViewById(R.id.tv_imgHeight);
        rtGenMat = findViewById(R.id.tv_rtGenMat);

        tvp = findViewById(R.id.ec_penc);
        tva = findViewById(R.id.ec_aenc);
        tvb = findViewById(R.id.ec_benc);
        tvG = findViewById(R.id.ec_Genc);
        tvn = findViewById(R.id.ec_nenc);
        tvh = findViewById(R.id.ec_henc);

        pubKey = findViewById(R.id.tv_PubKey);
        hillKey[0][0] = findViewById(R.id.tv_HillKey11);
        hillKey[0][1] = findViewById(R.id.tv_HillKey12);
        hillKey[0][2] = findViewById(R.id.tv_HillKey13);
        hillKey[0][3] = findViewById(R.id.tv_HillKey14);
        hillKey[1][0] = findViewById(R.id.tv_HillKey21);
        hillKey[1][1] = findViewById(R.id.tv_HillKey22);
        hillKey[1][2] = findViewById(R.id.tv_HillKey23);
        hillKey[1][3] = findViewById(R.id.tv_HillKey24);
        hillKey[2][0] = findViewById(R.id.tv_HillKey31);
        hillKey[2][1] = findViewById(R.id.tv_HillKey32);
        hillKey[2][2] = findViewById(R.id.tv_HillKey33);
        hillKey[2][3] = findViewById(R.id.tv_HillKey34);
        hillKey[3][0] = findViewById(R.id.tv_HillKey41);
        hillKey[3][1] = findViewById(R.id.tv_HillKey42);
        hillKey[3][2] = findViewById(R.id.tv_HillKey43);
        hillKey[3][3] = findViewById(R.id.tv_HillKey44);

        bilR.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            public void afterTextChanged(Editable s) {
                encKey.setEnabled(true);
                namaKunci.setEnabled(true);
            }
        });
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
                genHill.setEnabled(true);
                inPubKey.setEnabled(false);
            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }else  if (requestCode == PICK_PUBKEY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                try {
                    String[] textPubKey = text.toString().split(" ");
                    String[] titikG = textPubKey[3].split("[\\(||\\)||\\,]");
                    String[] titikPubKey = textPubKey[6].split("[\\(||\\)||\\,]");

                    ecc = new ECC(Integer.parseInt(textPubKey[0]), Integer.parseInt(textPubKey[1]), Integer.parseInt(textPubKey[2]),
                            new Titik(Integer.parseInt(titikG[1]), Integer.parseInt(titikG[2])), Integer.parseInt(textPubKey[4]),
                            Integer.parseInt(textPubKey[5]));
                    elgamal = new Elgamal(ecc, new Titik(Integer.parseInt(titikPubKey[1]), Integer.parseInt(titikPubKey[2])));
                    randR.setEnabled(true);
                    bilR.setEnabled(true);
                    bilR.setHint("Bilangan r (1 - " + (ecc.n - 1) + " )");

                    tvp.setText(String.valueOf(ecc.p));
                    tva.setText(String.valueOf(ecc.a));
                    tvb.setText(String.valueOf(ecc.b));
                    tvG.setText(ecc.G.toString());
                    tvn.setText(String.valueOf(ecc.n));
                    tvh.setText(String.valueOf(ecc.h));
                    pubKey.setText("Public Key : " + elgamal.publicKey.toString());
                }catch (Exception e){
                Toast.makeText(this, "input tidak sesuai", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClick(View view) {
        if(view.getId()==R.id.btn_inFile){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }else if(view.getId()==R.id.btn_genHill){
            long timeAwal = System.currentTimeMillis();
            hill = new HillCipher();
            long timeAkhir = System.currentTimeMillis();
            runTimeGenMat = timeAkhir - timeAwal;
            rtGenMat.setText("Running Time : "+runTimeGenMat+" ms");
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    hillKey[i][j].setText(String.valueOf(hill.getKey()[i][j]));
                }
            }
            namaCipher.setEnabled(true);
            encHill.setEnabled(true);
        }else if(view.getId()==R.id.btn_encHill){
            namaCF = String.valueOf(namaCipher.getText());
            if(namaCF.matches("")){
                namaCipher.setError("Nama Kosong");
            }else{
                cryptImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                new ProsesImageEncrypt(this).execute();
            }
        }else if(view.getId()==R.id.btn_inPubKey){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
            intent.setDataAndType(uri, "text/plain");
            startActivityForResult(Intent.createChooser(intent, "Select Key"), PICK_PUBKEY_REQUEST);
        }else if(view.getId()==R.id.btn_randBil){
            bilR.setText(String.valueOf(rand.nextInt(ecc.n - 1) + 1));
            encKey.setEnabled(true);
            namaKunci.setEnabled(true);
        }else if(view.getId()==R.id.btn_encKey){
            namaK = String.valueOf(namaKunci.getText());
            String sR = String.valueOf(bilR.getText());
            if(sR.matches("")) {
                bilR.setError("R Kosong");
            }else {
                RandomBil = Integer.parseInt(sR);
                if (namaK.matches("")) {
                    namaKunci.setError("Nama Kosong");
                } else if (RandomBil < 1 || RandomBil > ecc.n - 1) {
                    bilR.setError("Input Salah");
                } else {
                    new ProsesKeyEncrypt(this, RandomBil).execute();
                }
            }
        }
    }

    public void saveCipher(){
        try {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File myDir = new File(root + "/Skripsi");
            myDir.mkdirs();

            File cFile = new File(myDir, "CipherImage_"+namaCF+"."+ekstFileCitra);
            if (cFile.exists()) cFile.delete();
            FileOutputStream outFile = new FileOutputStream(cFile);
            cryptImage.compress(Bitmap.CompressFormat.PNG, 100, outFile);
            outFile.flush();
            outFile.close();
            String text = "";
            for(int i=0;i<18;i++){
                text += cipherKey[i] + " ";

            }
            File cKey = new File(myDir, "CipherKey_"+namaK+".txt");
            if (cKey.exists()) cKey.delete();
            FileOutputStream outKey = new FileOutputStream(cKey, true);
            outKey.write(text.getBytes());

            backEnable = true;
            inPubKey.setEnabled(false);
            namaKunci.setEnabled(false);
            encKey.setEnabled(false);
            randR.setEnabled(false);
            bilR.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Proses Selesai");
            builder.setMessage("Running Time : \n" +
                    "Generate Matriks : "+runTimeGenMat+" ms\n"+
                    "Enkripsi Citra   : "+runTimeEncImg+" ms\n"+
                    "Enkripsi Kunci   : "+runTimeEncKey+" ms\n"+
                    "Keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EnkripsiActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });
            builder.show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void afterImgEncrypt(){
        ivFile.setImageBitmap(cryptImage);
        genHill.setEnabled(false);
        namaCipher.setEnabled(false);
        encHill.setEnabled(false);
        inPubKey.setEnabled(true);
        inFile.setEnabled(false);
        backEnable = false;
    }

    private class ProsesImageEncrypt extends AsyncTask<Void,Void,Void> {
        private ProgressDialog prgd;
        long timeAwal, timeAkhir;
        TextView rtEncImg;

        public ProsesImageEncrypt(EnkripsiActivity activity) {
            prgd = new ProgressDialog(activity);
            rtEncImg = findViewById(R.id.tv_rtEncImg);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread() {
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    cryptImage = hill.encryption(cryptImage);
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
            prgd.setMessage("Masih Enkripsi Citra");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            timeAkhir = System.currentTimeMillis();
            runTimeEncImg = timeAkhir - timeAwal;
            rtEncImg.setText("Running Time : "+runTimeEncImg+" ms");
            afterImgEncrypt();
            prgd.dismiss();
        }
    }

    private class ProsesKeyEncrypt extends AsyncTask<Void,Void,Void> {
        private ProgressDialog prgd;
        int RandomBil;
        long timeAwal, timeAkhir;
        TextView rtEncKey;

        public ProsesKeyEncrypt(EnkripsiActivity activity, int RandomBil) {
            prgd = new ProgressDialog(activity);
            this.RandomBil = RandomBil;
            rtEncKey = findViewById(R.id.tv_rtEncKey);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread tr = new Thread() {
                @Override
                public void run() {
                    timeAwal = System.currentTimeMillis();
                    cipherKey = elgamal.encryption(hill.getKey(),RandomBil);
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
            prgd.setMessage("Masih Enkripsi Kunci EC Elgamal");
            prgd.setCanceledOnTouchOutside(false);
            prgd.setCancelable(false);
            prgd.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            timeAkhir = System.currentTimeMillis();
            runTimeEncKey = timeAkhir - timeAwal;
            rtEncKey.setText("Running Time : "+runTimeEncKey+" ms");
            saveCipher();
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
                    EnkripsiActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { }
            });
            builder.show();
        }else{
            EnkripsiActivity.super.onBackPressed();
        }
    }

    public String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }
}
