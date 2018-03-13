package com.example.ibrahim_pc.texrecog;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnImag;
    TextView textView;
    private static final int requst = 100;
    Uri imageUri;
    private StringBuilder str;
    //speak
    TextToSpeech toSpeech;
    int result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textview);

        btnImag = findViewById(R.id.btn_pike);
        btnImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open sd to get photo
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, requst);

            }
        });


        //speak
        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = toSpeech.setLanguage(Locale.US);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requst && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            extractTextFromImage();


        }
    }


    private void extractTextFromImage() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {

            Log.e("Erorr", "Detector debent are not avialble");

        } else {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                str = new StringBuilder();

                for (int i = 0; i < items.size(); i++) {
                    TextBlock item = items.valueAt(i);
                    str.append(item.getValue());
                    str.append("\n");
                }
                //scroll text
                textView.setText(str.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //speak
    public void onClickTextToSpeech(View view) {
        switch (view.getId()) {
            case R.id.Speak_id:
                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(), "Choose  Image to Read", Toast.LENGTH_SHORT).show();

                } else {
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Feature not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        if (str.toString().isEmpty()) {
                            toSpeech.speak("No Text In This Photo ... ", TextToSpeech.QUEUE_FLUSH, null);

                        } else {
                            toSpeech.speak(str.toString()+" thanks ... ibrahim ", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                    break;
                }
            case R.id.Stop_id:
                if (textView == null) {
                    Toast.makeText(getApplicationContext(), "No Text To Read", Toast.LENGTH_SHORT).show();

                }
                if (toSpeech != null) {
                    toSpeech.stop();
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toSpeech != null) {
            toSpeech.stop();
            toSpeech.shutdown();
        }
    }


}
