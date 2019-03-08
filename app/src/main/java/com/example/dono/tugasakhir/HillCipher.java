package com.example.dono.tugasakhir;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;
import java.util.Random;

public class HillCipher extends Kriptografi {
    Random rand = new Random();
    private int keyHill[][];
    private int invKey[][] = new int[4][4];
    private int determinan;

    public HillCipher(){
        keyHill = generateKeyHill();
    }

    public HillCipher(int[][] keyHill){
        this.keyHill = keyHill;
    }

    public void inversMatriks(){
        determinan = determinant(keyHill,4);
        keyHill = cofactor(keyHill);
        keyHill = transpose(keyHill);
        invKey = inversMatriks(keyHill,determinan);
    }

    public int[][] getKey(){
        return keyHill;
    }
    public int[][] getInvKey() {
        return invKey;
    }

    public Bitmap encryption(Bitmap cryptImage){
        int nPixel = cryptImage.getWidth() * cryptImage.getHeight();
        int nRGB = nPixel * 3;
        int plainHeight = (int) Math.ceil((double)nRGB / 4);
        int[][] plainImage = new int[plainHeight][4];
        for(int i=0;i<plainImage.length;i++)
            Arrays.fill(plainImage[i],-1);
        int pix;
        int tempW = 0, tempH = 0;
        for(int i=0;i<cryptImage.getHeight();i++) {
            for (int j = 0; j < cryptImage.getWidth(); j++) {
                pix = cryptImage.getPixel(j,i);
                for(int tempRGB =0; tempRGB<3;tempRGB++) {
                    if (tempRGB == 0)
                        plainImage[tempH][tempW%4] = Color.red(pix);
                    else if (tempRGB == 1)
                        plainImage[tempH][tempW%4] = Color.green(pix);
                    else if (tempRGB == 2)
                        plainImage[tempH][tempW%4] = Color.blue(pix);
                    tempW++; if(tempW % 4 == 0) tempH++;
                }
            }
        }

        tempW = 0; tempH = 0; int red=0,green=0,blue=0;
        plainImage = kaliMatriks(plainImage,keyHill);
        for(int i=0;i<cryptImage.getHeight();i++) {
            for (int j = 0; j < cryptImage.getWidth(); j++) {
                pix = cryptImage.getPixel(j,i);
                for(int tempRGB =0; tempRGB<3;tempRGB++) {
                    if (tempRGB == 0)
                        red = plainImage[tempH][tempW % 4];
                    else if (tempRGB == 1)
                        green = plainImage[tempH][tempW % 4];
                    else if (tempRGB == 2)
                        blue = plainImage[tempH][tempW % 4];
                    tempW++;
                    if (tempW % 4 == 0) tempH++;
                }
                cryptImage.setPixel(j,i,Color.argb(Color.alpha(pix),red,green,blue));
            }
        }

        return cryptImage;
    }

    public Bitmap decryption(Bitmap cryptImage){
        int nPixel = cryptImage.getWidth() * cryptImage.getHeight();
        int nRGB = nPixel * 3;
        int plainHeight = (int) Math.ceil((double)nRGB / 4);
        int[][] cipherImage = new int[plainHeight][4];
        for(int i=0;i<cipherImage.length;i++)
            Arrays.fill(cipherImage[i],-1);
        int pix;
        int tempW = 0, tempH = 0;
        for(int i=0;i<cryptImage.getHeight();i++) {
            for (int j = 0; j < cryptImage.getWidth(); j++) {
                pix = cryptImage.getPixel(j,i);
                for(int tempRGB =0; tempRGB<3;tempRGB++) {
                    if (tempRGB == 0)
                        cipherImage[tempH][tempW%4] = Color.red(pix);
                    else if (tempRGB == 1)
                        cipherImage[tempH][tempW%4] = Color.green(pix);
                    else if (tempRGB == 2)
                        cipherImage[tempH][tempW%4] = Color.blue(pix);
                    tempW++; if(tempW % 4 == 0) tempH++;
                }
            }
        }

        tempW = 0; tempH = 0; int red=0,green=0,blue=0;
        cipherImage = kaliMatriks(cipherImage,invKey);
        for(int i=0;i<cryptImage.getHeight();i++) {
            for (int j = 0; j < cryptImage.getWidth(); j++) {
                pix = cryptImage.getPixel(j,i);
                for(int tempRGB =0; tempRGB<3;tempRGB++) {
                    if (tempRGB == 0)
                        red = cipherImage[tempH][tempW % 4];
                    else if (tempRGB == 1)
                        green = cipherImage[tempH][tempW % 4];
                    else if (tempRGB == 2)
                        blue = cipherImage[tempH][tempW % 4];
                    tempW++;
                    if (tempW % 4 == 0) tempH++;
                }
                cryptImage.setPixel(j,i,Color.argb(Color.alpha(pix),red,green,blue));
            }
        }

        return cryptImage;
    }

    public int[][] kaliMatriks(int[][] matFile, int[][] key){
        int row = matFile.length;
        int[][] hasil = new int[row][4];
        if(matFile[row-1][3] == -1){
            hasil[row-1] = matFile[row-1];
            row--;
        }
        for(int i=0;i<row;i++){
            for(int j=0;j<4;j++){
                for(int k=0;k<4;k++){
                    hasil[i][j] = (hasil[i][j] + matFile[i][k] * key[k][j])%256;
                }
            }
        }
        return hasil;
    }

    public int[][] generateKeyHill(){
        int A[][];
        int  modDet;
        do{
            A = setMatriks();
            determinan = determinant(A,4);
            modDet = modulus(determinan,256);
        }while(!cekInvMod(modDet,256) || determinan==0);
        return A;
    }

    public int[][] setMatriks(){
        int A[][] = new int[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                A[i][j] = rand.nextInt(256);
            }
        }
        return A;
    }

    public int[][] inversMatriks(int A[][], int det){
        int invmod = inverseModulo(det,256);
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                A[i][j] *= invmod;
                A[i][j] = modulus(A[i][j],256);
            }
        }
        return A;
    }

    public int determinant(int A[][],int N)
    {
        int det;
        if (N == 2)
        {
            det = A[0][0]*A[1][1] - A[1][0]*A[0][1];
        }
        else
        {
            det=0;
            for(int iAwal=0;iAwal<N;iAwal++)
            {
                int[][] m = new int[N-1][N-1];
                int temp = 0;
                for(int i=0;i<N;i++)
                {
                    if(iAwal == i)
                        continue;
                    for(int j=1;j<N;j++)
                    {
                        m[temp][j-1] = A[i][j];
                    }
                    temp++;
                }
                det += Math.pow(-1.0,iAwal)* A[iAwal][0] * determinant(m,N-1);
            }
        }
        return det;
    }

    public int[][] cofactor(int A[][]){
        int[][] b=new int[4][4], cofac = new int[4][4];
        for (int q = 0;q < 4; q++)
        {
            for (int p = 0;p < 4; p++)
            {
                int tempP = 0, tempQ = 0;
                for (int i = 0;i < 4; i++)
                {
                    for (int j = 0;j < 4; j++)
                    {
                        if (i != q && j != p)
                        {
                            b[tempP][tempQ] = A[i][j];
                            if (tempQ < 2)
                                tempQ++;
                            else
                            {
                                tempQ = 0;
                                tempP++;
                            }
                        }
                    }
                }
                cofac[q][p] = (int) (Math.pow(-1, q + p) * determinant(b, 3));
                cofac[q][p] = modulus(cofac[q][p],256);
            }
        }
        return cofac;
    }

    public int[][] transpose(int A[][]){
        int[][] trans = new int[4][4];
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                trans[i][j] = A[j][i];
            }
        }
        return trans;
    }
}
