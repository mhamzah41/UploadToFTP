package com.adeel.easyftp;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.adeel.library.easyFTP;

import java.io.InputStream;


public class demo extends ActionBarActivity {
    private ProgressDialog prg;
    private final int SELECT_PHOTO = 1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        imageView = (ImageView)findViewById(R.id.imageView);

        Button pickImage = (Button) findViewById(R.id.btn_pick);
        pickImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public  void up(View v){
        String address="167.205.7.230",u="vidyanusa_projects",p="123456!a",directory="BackEnd";
        uploadTask async=new uploadTask();
        async.execute(address,u,p,directory);//Passing arguments to AsyncThread
    }

    class uploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            prg = new ProgressDialog(demo.this);
            prg.setMessage("Uploading...");
            prg.show();
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                easyFTP ftp = new easyFTP();
                InputStream is=getResources().openRawResource(+R.drawable.easyftptest);
                ftp.connect(params[0],params[1],params[2]);
                boolean status=false;
                if (!params[3].isEmpty()){
                    status=ftp.setWorkingDirectory(params[3]);
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                ftp.uploadFile(is,timeStamp +".png");
                return new String("Upload Successful");
            }catch (Exception e){
                String t="Failure : " + e.getLocalizedMessage();
                return t;
            }
        }



            @Override
        protected void onPostExecute(String str) {
            prg.dismiss();
            Toast.makeText(demo.this,str,Toast.LENGTH_LONG).show();
        }
    }

}
