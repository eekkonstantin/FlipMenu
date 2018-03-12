package com.kkontagion.flipmenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

//translate
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.kkontagion.flipmenu.objects.HistoryItem;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConfirmActivity extends AppCompatActivity {
    //Text detection variables
    TextView tvImageDetails;
    ImageView imgPreview;
    //Translation variables
    TextView tvTranslated, tvLoading, statusText;
    final Handler textViewHandler = new Handler();

    //Cloudvision API variables
    private static final String CLOUD_VISION_API_KEY = "AIzaSyAHRYnKmAX72ofF7HVTSvFkSmcrNRvP8BM";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private String jsonTextDetect;
    private ArrayList<String> textTranslated, transHolder;

    ImageButton btCfm, btReject;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    Uri imageUri;
    Boolean isEmpty = false;
    String chosenLang, filename,status,message = "", spName;
    AlertDialog.Builder builder;
    RelativeLayout rlLoading;
    EditText etLocation;

    boolean fromHistory = false;
    HistoryItem hItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        //Spinner
        spinner = findViewById(R.id.sp_lang);
//        adapter = ArrayAdapter.createFromResource(this,
//                R.array.list_preference_language, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

        etLocation = findViewById(R.id.et_location);

        tvTranslated =  findViewById(R.id.tv_translated);
        rlLoading = findViewById(R.id.rl_loading);
        tvLoading =  findViewById(R.id.tv_loading);
        tvLoading.setText(R.string.progress_detect);

        //Buttons
        btCfm = findViewById(R.id.bt_confirm);
        btReject = findViewById(R.id.bt_reject);
        btCfm.setEnabled(false);

        statusText =  findViewById(R.id.tv_final);

        imgPreview = findViewById(R.id.img_preview);
        tvImageDetails = findViewById(R.id.tv_before);


        filename = getIntent().getStringExtra("filepath");
        imageUri = Uri.fromFile(new File(filename));
        spName = imageUri.getLastPathSegment().split("\\.")[0];

        fromHistory = getIntent().getBooleanExtra("fromHistory", false);
        if (fromHistory) {
            this.hItem = getIntent().getParcelableExtra("historyItem");
            if (hItem.getLocation().equalsIgnoreCase(getString(R.string.history_location)))
                etLocation.setHint(hItem.getLocation());
            else
                etLocation.setText(hItem.getLocation());
            Glide.with(this).load(new File(filename)).into(imgPreview);
            this.message = hItem.getFullText();
            this.jsonTextDetect = hItem.getJsonData().toString();
            formatDetectedText();
            detectDone();
        } else {
            //Image handling
            Log.d(getClass().getSimpleName(), "onCreate: " + imageUri.getLastPathSegment());
            uploadImage(imageUri);
        }
//        btReject.setEnabled(false);

//        setupActions();
    }

    private void setupActions() {
        btCfm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show progressbar
                tvLoading.setText(R.string.progress_translate);
                rlLoading.setVisibility(View.VISIBLE);
                etLocation.setEnabled(false);

                // Select Language
                chosenLang = getResources().getStringArray(R.array.list_preference_language_values)[spinner.getSelectedItemPosition()];
                Log.d(getClass().getSimpleName(), "onClick: " + chosenLang);

                String loc = etLocation.getText().toString();
                Log.d(getClass().getSimpleName(), "onClick: Saving updated location.");
                getSharedPreferences(spName, MODE_PRIVATE).edit()
                        .putString("location", (loc.length() > 0 ? loc : getString(R.string.history_location)))
                        .apply();

                translateText(transHolder);
            }
        });

        btReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
    }

    private void confirmDialog(){
        builder = new AlertDialog.Builder(ConfirmActivity.this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to delete this image?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                File deleteImg = new File(imageUri.getPath());
                if (deleteImg.exists()) {
                    if (deleteImg.delete()) {
                        System.out.println("Image Deleted :" + imageUri.getPath());
                    } else {
                        System.out.println("Image not Deleted :" + imageUri.getPath());
                    }
                }
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (!fromHistory) confirmDialog();
        else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                imgPreview.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d("ConfirmActivity", "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("Confirm Activity", "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        tvImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d("hello", "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    jsonTextDetect = response.toString();
                    convertResponseToString(response);
                    textViewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            detectDone();
                        }
                    });
                    return response.toString();

                } catch (GoogleJsonResponseException e) {
                    Log.d("hello", "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d("hello", "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                tvImageDetails.setText(result);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void translateText(final List<String> init_txt){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TranslateOptions options = TranslateOptions.newBuilder()
                        .setApiKey(CLOUD_VISION_API_KEY)
                        .build();
                Translate translate = options.getService();
                final List<Translation> translation =
                        translate.translate(init_txt,
                                Translate.TranslateOption.targetLanguage(chosenLang));

                formatTranslationResult(translation);
                textViewHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        if (tvTranslated != null) {
//                            tvTranslated.setText(translation.getTranslatedText());
//                        }
                        allDone();
                    }
                });
                return null;
            }
        }.execute();
    }

    private void formatTranslationResult(List<Translation> translations) {
        textTranslated = new ArrayList<>();
        for (Translation t : translations)
            textTranslated.add(t.getTranslatedText());
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {


        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            // Get main text
            message += String.format(Locale.US, "%s", labels.get(0).getDescription());
//            for (EntityAnnotation label : labels) {
//                message += String.format(Locale.US, "%s", label.getDescription());
//                message += "\n";
//                // TODO json, locationXY
//            }
            formatDetectedText();
        } else {
            isEmpty = true;
            message = getString(R.string.error_nodetect);
        }

        Log.e(getClass().getSimpleName(), "convertResponseToString: " + message);

        return message;
    }

    // Kon

    /**
     * Format:
     *  HEADER
     *  Item Name
     *  Item description
     */
    private void formatDetectedText() {
        String[] sp = message.split("\n");
        transHolder = new ArrayList<>();

        StringBuilder sb = new StringBuilder(sp[0]);
        sb.append(" === ");
        transHolder.add(sp[0]);

        for (int i=1; i<sp.length - 1; i+= 2) {
            sb.append(sp[i] + " /// ");
            sb.append(sp[i+1] + " //// ");
            transHolder.add(sp[i] + " /// " + sp[i+1]);
        }

        message = sb.toString();
    }


    public void detectDone() {
        if(isEmpty){
            tvLoading.setText("Error");
            btCfm.setVisibility((View.GONE));
            status = "No text detected, please try again";
        }
        else{
            btCfm.setEnabled(true);
            tvLoading.setText(R.string.progress_done);
            status = "";
        }

        new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {
                //do nothing
            }

            public void onFinish() {
                rlLoading.setVisibility(View.GONE);
                statusText.setText(status);

                // get location
                String loc = etLocation.getText().toString();

                Log.d(getClass().getSimpleName(), "onFinish: from history? " + fromHistory);

                if (!fromHistory) {
                    // save data

                    SharedPreferences.Editor spE = getSharedPreferences(spName, MODE_PRIVATE).edit();
                    spE.putString("jsondata", jsonTextDetect)
                            .putString("location", (loc.length() > 0 ? loc : getString(R.string.history_location)))
                            .putLong("datetime", Calendar.getInstance().getTimeInMillis())
                            .apply();
                }
            }
        }.start();

        setupActions();
    }

    private void allDone() {

        Log.e(getClass().getSimpleName(), "allDone: " + textTranslated);
        // Close progressbar
        rlLoading.setVisibility(View.GONE);

        // Set intent data and open activity.
        Intent i = new Intent(getBaseContext(), MenuActivity.class);

        i.putExtra("detectedJSON", jsonTextDetect);
        i.putExtra("detectedText", transHolder);
        i.putExtra("translatedText", textTranslated);
        i.putExtra("imgpath", imageUri.getPath());
        startActivity(i);
    }

}
