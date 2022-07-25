package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class Camera extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraproviderfuture;
    PreviewView previewView;
    Preview preview;
    ImageCapture imageCapture;
    FirebaseVisionFaceDetector detector;
    FirebaseVisionImage visionImage;
    Bitmap image1;
    ImageView Switch;
    ProcessCameraProvider cameraProvider;
    private CameraSelector lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        Switch = findViewById(R.id.imageView);

        cameraproviderfuture = ProcessCameraProvider.getInstance(this);
        cameraproviderfuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraproviderfuture.get();
                    startCamera(cameraProvider);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, getExecutor());

        Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lensFacing==CameraSelector.DEFAULT_FRONT_CAMERA){
                    lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
                }
                else if(lensFacing == CameraSelector.DEFAULT_BACK_CAMERA){
                    lensFacing=CameraSelector.DEFAULT_FRONT_CAMERA;
                }
                startCamera(cameraProvider);
            }
        });

    }


    private void startCamera(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this,lensFacing,preview,imageCapture);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    public void onClick(View view) {
        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image)
            {
                Toast.makeText(Camera.this, "Wait, Data is loading :) ", Toast.LENGTH_SHORT).show();

                image1 = convertImageProxyToBitmap(image);

                FirebaseVisionFaceDetectorOptions options
                        = new FirebaseVisionFaceDetectorOptions
                        .Builder()
                        .setPerformanceMode(
                                FirebaseVisionFaceDetectorOptions.
                                        ACCURATE)
                        .setLandmarkMode(
                                FirebaseVisionFaceDetectorOptions
                                        .ALL_LANDMARKS)
                        .setClassificationMode(
                                FirebaseVisionFaceDetectorOptions
                                        .ALL_CLASSIFICATIONS)
                        .enableTracking()
                        .build();
                try{
                    visionImage = FirebaseVisionImage.fromBitmap(image1);
                    detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                detector.detectInImage(visionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces)
                    {

                        String resultText = "";
                        int i=1;
                        for(FirebaseVisionFace face:firebaseVisionFaces){
                            resultText=resultText.concat("\nFace Number "+i+" : ")
                                    .concat("\nSmile : "+ face.getSmilingProbability()* 100+ "%")
                                    .concat("\nLeft Eye Open : "+face.getLeftEyeOpenProbability()*100+"%")
                                    .concat("\nRight Eye Open : "+face.getRightEyeOpenProbability()*100+"%")
                                    .concat("\nBounding Box : "+face.getBoundingBox());
                            i++;
                        }
                        if(firebaseVisionFaces.size()==0){
                            Toast.makeText(Camera.this, "No Face Detected", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Bundle bundle = new Bundle();
                            bundle.putString(LCOFaceDetection.RESULT_TEXT,resultText);
                            DialogFragment resultDialog = new ResultDialog();
                            resultDialog.setArguments(bundle);
                            resultDialog.setCancelable(true);
                            resultDialog.show(getSupportFragmentManager(),LCOFaceDetection.RESULT_DIALOG);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Camera.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                    Toast.makeText(Camera.this, "Error : "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy image) {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
    }


}