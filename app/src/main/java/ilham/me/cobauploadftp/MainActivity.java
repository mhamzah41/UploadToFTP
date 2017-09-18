package ilham.me.cobauploadftp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.adeel.library.easyFTP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog prg;
    private final int SELECT_PHOTO = 1;
    private ImageView imageView;
    private  Bitmap selectedImage;
    private InputStream imageStream;
    private ClipDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);

        Button pickImage = (Button) findViewById(R.id.btn_pick);
        pickImage.setOnClickListener(new View.OnClickListener() {

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
                        imageStream = getContentResolver().openInputStream(imageUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                        //drawable = (ClipDrawable) imageView.getDrawable();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public  void up(View v){
        String address="167.205.7.230",u="ftp.vidyanusa.id|vidyanusaftp",p="VvIiDdYyAa123!",directory="tes_upload_dari_android";
        //String address="167.205.7.230",u="vidyanusa_projects",p="123456!a",directory="BackEnd";
        uploadTask async=new uploadTask();
        async.execute(address,u,p,directory);//Passing arguments to AsyncThread
    }

    class uploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            prg = new ProgressDialog(MainActivity.this);
            prg.setMessage("Uploading...");
            prg.show();
        }
        @Override
        protected String doInBackground(String... params) {

            try {
                easyFTP ftp = new easyFTP();

                //Convert Bitmap to Input Stream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);


                //InputStream is=getResources().openRawResource(+R.drawable.easyftptest);


                ftp.connect(params[0],params[1],params[2]);
                boolean status=false;
                if (!params[3].isEmpty()){
                    status=ftp.setWorkingDirectory(params[3]);
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                ftp.uploadFile(bs,timeStamp +".png");
                return new String("Upload Successful");
            }catch (Exception e){
                String t="Failure : " + e.getLocalizedMessage();
                return t;
            }
        }



        @Override
        protected void onPostExecute(String str) {
            prg.dismiss();
            Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();
        }
    }


}
