package jinjin.juju.young.d_easel

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //카메라
        btn_camera.setOnClickListener { view ->

            val intent : Intent = Intent( this, Image2Activity::class.java)
            startActivity(intent)

        }

        //갤러리 호출
        btn_gallery.setOnClickListener { view ->

            val intent : Intent = Intent( this, ImageActivity::class.java)
            startActivity(intent)

        }

    }


    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
