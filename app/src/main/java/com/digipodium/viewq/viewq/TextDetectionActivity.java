package com.digipodium.viewq.viewq;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class TextDetectionActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyAwxMAEWAfC5jmwaY1Ldo9TKGS2dNL4t6c";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = ObjectDetectionActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private ImageView mMainImage;
    private ProgressDialog dialog;
    private RecyclerView rv1;
    private ScrollView scrollview;
    private Bitmap myBitmap;
    private Button btn_screenshot1;
    private String imageName = "n&s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);

        Uri imageUri = getIntent().getData();

        rv1 = findViewById(R.id.rv1);
        rv1.setLayoutManager(new LinearLayoutManager(this));

        mMainImage = findViewById(R.id.main_image);
        if (imageUri != null) {
            uploadImage(imageUri);
        } else {
            Toast.makeText(this, "you are unworthy", Toast.LENGTH_SHORT).show();
        }

        btn_screenshot1 = findViewById(R.id.btn_Screenshot1);
        btn_screenshot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollview = findViewById(R.id.relative1);
                scrollview.post(new Runnable() {
                    public void run() {

                        //take screenshot
                        myBitmap = captureScreen(scrollview);

                        //   Toast.makeText(getApplicationContext(), "Screenshot captured..!", Toast.LENGTH_LONG).show();

                        try {
                            if (myBitmap != null) {
                                //save image to SD card

                                saveImage(imageName, myBitmap);
                            }
                            Toast.makeText(getApplicationContext(), "memory saved..!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public static Bitmap captureScreen(View v) {

        Bitmap screenshot = null;
        try {

            if (v != null) {

                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }

        } catch (Exception e) {
            Log.d("ScreenShotActivity", "Failed to capture screenshot because:" + e.getMessage());
        }

        return screenshot;
    }

    public void saveImage(String imgName, Bitmap bm) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "ViewQ" + "/" + "memory"); //Creates app specific folder
        path.mkdirs();
        File imageFile = new File(path, imgName + System.currentTimeMillis() + ".png");// Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(String.valueOf(path));
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

        } catch (Exception e) {
            throw new IOException();
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

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

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                //            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            //      Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }


    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
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
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(textDetection);
            }
            });

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class TextDetectionTask extends AsyncTask<Object, Void, BatchAnnotateImagesResponse> {
        private final WeakReference<TextDetectionActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        TextDetectionTask(TextDetectionActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected BatchAnnotateImagesResponse doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                convertResponseToString(response);
                return response;

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(BatchAnnotateImagesResponse result) {
            TextDetectionActivity activity = mActivityWeakReference.get();

            if (activity != null && !activity.isFinishing()) {
                hideDialog();
            }
            List<AnnotateImageResponse> responses = result.getResponses();
            List<EntityAnnotation> labels = responses.get(0).getTextAnnotations();
            if (labels!=null){
                TextAdapter adapter = new TextAdapter(TextDetectionActivity.this, R.layout.recycler_scan, labels);
                rv1.setAdapter(adapter);
            }else{
                Toast.makeText(activity, "No details found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, BatchAnnotateImagesResponse> labelDetectionTask = new TextDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
            showDialog("scanning image ");
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
            try {
                showDialog("failed to scan");
            } catch (Exception e2) {
            }
        }
    }

    private void convertResponseToString(BatchAnnotateImagesResponse response) {

    }


    public void showDialog(String message) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
           try{
               dialog.dismiss();
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

}