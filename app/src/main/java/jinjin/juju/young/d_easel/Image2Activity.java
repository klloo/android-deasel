package jinjin.juju.young.d_easel;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Image2Activity extends AppCompatActivity {

    private static final String TAG = "AndroidOpenCv";
    private static final int REQ_CODE_CAMERA_IMAGE = 100;

    private Bitmap input;
    private Bitmap original;
    private Bitmap output;

    private ImageView originalView;
    private ImageView edgeView;

    private boolean mIsOpenCVReady = false;

    public native void detectEdgeJNI(long inputImage, long outputImage, int th1, int th2);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }



    public void detectEdgeUsingJNI() {
        if (!mIsOpenCVReady) {
            return;
        }
        Mat src = new Mat();
        Utils.bitmapToMat(input, src);
        originalView.setImageBitmap(original);
        Mat edge = new Mat();
        detectEdgeJNI(src.getNativeObjAddr(), edge.getNativeObjAddr(), 50, 150);
        Utils.matToBitmap(edge, input);
        edgeView.setImageBitmap(input);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image2);
        originalView = findViewById(R.id.origin);
        edgeView = findViewById(R.id.edge);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_CODE_CAMERA_IMAGE);
                }
            }
        }
        else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())!= null){
                startActivityForResult(intent, REQ_CODE_CAMERA_IMAGE);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mIsOpenCVReady = true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_CAMERA_IMAGE || resultCode == Activity.RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                original= imageBitmap;
                input = imageBitmap;

                if (input != null) { // 가져온 이미지가 null 이 아니면 엣지 디텍팅
                    detectEdgeUsingJNI();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void onDestroy() {
        super.onDestroy();

        input.recycle();
        if (input != null) {
            input = null;
        }
    }

    // 버튼 클릭 리스너
    public void onButtonClicked2(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("image", input);
        startActivity(intent);
    }

    // permission
    static final int PERMISSIONS_REQUEST_CODE = 1001;
    String[] PERMISSIONS = {"android.permission.CAMERA"};


    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    // permission
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("실행을 위해 권한 허가가 필요합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Image2Activity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}
