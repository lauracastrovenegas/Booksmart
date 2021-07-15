package com.example.booksmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.booksmart.helpers.BitmapScaler;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Camera {

    public static final String TAG = "camera";
    public static final String FAILURE_MSG = "Failed to make directory!";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int GET_FROM_GALLERY = 3;
    public static final int IMAGE_QUALITY = 80;
    public static final int SCALE_WIDTH = 200;
    public static final String PROVIDER = BuildConfig.APPLICATION_ID + ".provider";

    Context context;
    Activity activity;
    File photoFile;
    String photoFileName = LocalDate.now().toString();

    public Camera(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void onLaunchCamera(String photoFileName) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);

        // wrap file object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(context, PROVIDER, photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        activity.startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void onOpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GET_FROM_GALLERY);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, FAILURE_MSG);
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    /* Method taken from CodePath Guide: https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media */
    public File scaleImage(Bitmap rawTakenImage, int width) throws IOException {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, width);
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        File resizedFile = getPhotoFileUri(photoFileName + "_resized");
        try {
            resizedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = new FileOutputStream(resizedFile);
        // Write the bytes of the bitmap to file
        fos.write(bytes.toByteArray());
        fos.close();

        return resizedFile;
    }

    public File getPhotoFile(){
        return photoFile;
    }
}
