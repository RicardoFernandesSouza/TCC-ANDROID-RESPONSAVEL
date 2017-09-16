package com.example.ricardofernandes.tohomeresponsavel;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class TiraFoto extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private File f;
    public File getAlbumDir()
    {

        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                "BAC/"
        );
        // Create directories if needed
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return storageDir;
    }
    private File createImageFile() throws IOException {
        // Create an image file name

        String imageFileName =getAlbumDir().toString() +"/image.jpg";
        File image = new File(imageFileName);
        return image;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tira_foto);
        this.imageView = (ImageView)this.findViewById(R.id.imageView);
        Button photoButton = (Button) this.findViewById(R.id.button);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    f = createImageFile();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }



            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Bitmap photo = BitmapFactory.decodeFile(f.getAbsolutePath());
//            imageView.setImageBitmap(photo);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"raul.pop90@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Prima poza");
            i.putExtra(Intent.EXTRA_TEXT   , "body of email");

            Uri uri = Uri.fromFile(f);
            i.putExtra(Intent.EXTRA_STREAM, uri);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(TiraFoto.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}