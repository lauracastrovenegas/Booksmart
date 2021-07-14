package com.example.booksmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Camera {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int GET_FROM_GALLERY = 3;

    Context context;
    Activity activity;

    public Camera(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void onLaunchCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void onOpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GET_FROM_GALLERY);
    }
}
