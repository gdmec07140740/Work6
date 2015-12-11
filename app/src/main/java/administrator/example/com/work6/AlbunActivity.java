package administrator.example.com.work6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbunActivity extends AppCompatActivity {
    private ViewFlipper flipper;
    private Bitmap[] mBglist; //图片储存列表
    private long startTime = 0;
    private SensorManager sn;//重力感应硬件控制器
    private SensorEventListener sel;//重力感应监听

    /**
     * 加载相册
     */
    public  String[] loadAlbun(){
        String pathName = android.os.Environment.getExternalStorageDirectory()
                .getPath()+"/com.demo.pr4";
        //创建文件
        File file =new File(pathName);
        Vector<Bitmap> fileName = new Vector<>();
        if (file.exists() && file.isDirectory()){
            String[] str = file.list();
            for(String s: str){
                if(new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName+"/"+s));
                };
            }
            mBglist = fileName.toArray(new Bitmap[]{});
        }
        return  null;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albun);
        flipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
        loadAlbun();
        if(mBglist == null){
            Toast.makeText(this,"相册无照片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            for(int i =0;i<=mBglist.length -1 ;i++){
                flipper.addView(addImage(mBglist[i]),i,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
            }
        }
        //获得重力感应硬件控制器
        sn = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sn.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //添加重力感应侦听
        sel =new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[SensorManager.DATA_X];
                //float y = sensorEvent.values[SensorManager.DATA_Y];
                //float z = sensorEvent.values[SensorManager.DATA_Z];
                // System.currentTimeMillis()>startTime+1000 控制甩动的必须在1秒内只有一个甩动
                if(x<-10 && System.currentTimeMillis()>startTime+1000){//右甩动
                    //记录甩动的开始时间
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbunActivity.this,R.anim.push_left_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbunActivity.this,R.anim.push_left_out));
                    flipper.showNext();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        //注册Listener SENSOR_DELAY_GAME为检测的精确度
        sn.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_GAME);
    }
    protected  void onDestory(){
        super.onDestroy();//注销重力感应侦听
        sn.unregisterListener(sel);
    }
    public Bitmap loadImage(String pathName) {
        //读取相片，并对图片进行缩小
        BitmapFactory.Options options =new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        //此时返回birmap为空
        Bitmap bitmap = BitmapFactory.decodeFile(pathName,options);
        //获取屏幕的宽度
        WindowManager manager =getWindowManager();
        Display display =manager.getDefaultDisplay();
        //假设希望Bitmap的显示宽度为手机屏幕的宽度
        int screenWidth =display.getWidth();
        //int screenHeight =display.getHeight();
        //计算Bitmap的高度等比变化数值
        options.inSampleSize = options.outWidth / screenWidth;
        //将inJustDecodeBounds设置为false，以便于可以解码为Bitmap文件
        options.inJustDecodeBounds =false;
        //读取相片的Bitmap
        bitmap =BitmapFactory.decodeFile(pathName,options);
        return bitmap;
    }
    //添加显示图片View
    private View addImage(Bitmap bitmap){
        ImageView img =new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albun, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
