package com.example.videorecorder;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//private static final AutoFocusCallback AutoFocusCallback = null;
	private Camera mCamera;
	private MediaRecorder mediaRecorder;
	private Button bt_start, bt_stop;
	private LinearLayout cameraPreview;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean cameraFront = false;
	private Paint paint = new Paint();
	//------TimeSet------//
	//TextView mTextField;
	public CountDownTimer Timer1;
	long timestay = 16000;
	//------TimeSet------//
	Time time = new Time();
	boolean recording = false;
	public static boolean cancelfocus = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Log.e("mCamera_test_work","onCreate");
		cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
		surfaceView = new SurfaceView(this);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(surfaceCallback);
		cameraPreview.addView(surfaceView);
		bt_start = (Button) findViewById(R.id.bt_start);
		//bt_stop = (Button) findViewById(R.id.bt_stop);
		//mTextField = (TextView)findViewById(R.id.tv_clock);
		init();
		
	}
/*----------------------------------------------------------------------------*/		
/*----------------------------init()------------------------------------------*/	
	private void init() {
		// TODO Auto-generated method stub
		Log.e("mCamera_test_work","init");
		mediaRecorder = new MediaRecorder();
		if(mCamera==null){
			Log.e("mCamera_test_work","not yet");
		}
		else{
			Log.e("mCamera_test_work","init work");
			mCamera.unlock();
			mediaRecorder.setCamera(mCamera);
			mediaRecorder.setOrientationHint(90);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
			mediaRecorder.setVideoEncodingBitRate(2000000000);
			mediaRecorder.setVideoSize(640, 480);
			mediaRecorder.setVideoFrameRate(60);
			//mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
			mediaRecorder.setOutputFile(recordfilepath());
			try {
				mediaRecorder.prepare();
			} catch (IllegalStateException e) {
				releaseMediaRecorder();
			} catch (IOException e) {
				releaseMediaRecorder(); 
			}
		}
	}			
/*----------------------------init()------------------------------------------*/
/*----------------------------------------------------------------------------*/
	
	
	public void onRecorderStart(View view){
		Log.e("mCamera_test_work","onRecorderStart");
		if (recording) {
			// stop recording and release camera
			Log.e("mCamera_test_work","onRecorderStart recording = true");
			mediaRecorder.stop(); // stop the recording
			releaseMediaRecorder(); // release the MediaRecorder object
			cancelfocus = true;
			refreshCamera(mCamera);
			recording = false;
			bt_start.setText("Start");
			Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
			timestay = 16000;
		} else {
			Log.e("mCamera_test_work","onRecorderStart recording = false");
			if (!prepareMediaRecorder()) {
				Toast.makeText(MainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
				finish();
			}
			recording = true;
			bt_start.setText("Stop");
			// work on UiThread for better performance
			runOnUiThread(new Runnable() {
				public void run() {
					// If there are stories, add them to the table
					try {
						mediaRecorder.start();
					} catch (final Exception ex) {
						// Log.i("---","Exception in thread");
					}
				}
			});	
			subtracttime();
		}
		
	}
	private void subtracttime() {
		// TODO Auto-generated method stub
        new CountDownTimer(timestay,1000){
        	TextView mTextField = (TextView)findViewById(R.id.tv_clock);
            @Override
            public void onFinish() {
            	mTextField.setText("Done!");
            }

            @Override
            public void onTick(long millisUntilFinished) {
            	mTextField.setText("Second: "+millisUntilFinished/1000+" s");
            }

        }.start();
	}
	public void onRecorderStop(View view){
		
	}
	public void onRecorderSwitch(View view){
		
	}
	
/*----------------------------------------------------------------------------*/		
/*--------------------------Surfaceview---------------------------------------*/	
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.e("mCamera_test_work","surfaceDestroyed");

		}		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.e("mCamera_test_work","surfaceCreated");
			try{
	            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
	        }catch(RuntimeException e){
	            Log.e("opencamera", "init_camera: " + e);
	            return;
	        }
			refreshCamera(mCamera);
			/*setCameraParameter(mCamera);
			try {
				mCamera.setPreviewDisplay(surfaceHolder);
	            //camera.setDisplayOrientation(90);
	            mCamera.startPreview();
	            //camera.takePicture(shutter, raw, jpeg)
	        } catch (Exception e) {
	            Log.e("opencamera", "init_camera: " + e);
	            return;
	        }*/
			
		}		
		@Override 
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			Log.e("mCamera_test_work","surfaceChanged");
			refreshCamera(mCamera);
		}
	};
		
/*--------------------------Surfaceview---------------------------------------*/	
/*----------------------------------------------------------------------------*/
	
	


/*-------------------------------------------------------------------------------------*/	
/*--------------------------setCameraParameter-----------------------------------------*/		
	public void setCameraParameter(Camera camera) {
		//method to set a camera instance
		if(!cancelfocus){
			Log.e("mCamera_test_work","setCameraParameter cancelfocus true");
	        Camera.Parameters param;
	        param = camera.getParameters();
	        //param.setPreviewSize(640,480);
	        param.setPreviewFpsRange(60000, 60000);
	        camera.setParameters(param);
			camera.autoFocus(autoFocusCallback);
		}
		else{
			Log.e("mCamera_test_work","setCameraParameter cancelfocus false");
			camera.cancelAutoFocus();
	        Camera.Parameters param;
	        param = camera.getParameters();
	        //param.setPreviewSize(640,480);
	        param.setPreviewFpsRange(60000, 60000);
	        //modify parameter
	        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
	        camera.setParameters(param);
		}
		mCamera = camera;
	}
/*--------------------------setCameraParameter-----------------------------------------*/	
/*-------------------------------------------------------------------------------------*/	

	
	
/*-------------------------------------------------------------------------------------*/
/*--------------------------refreshCamera----------------------------------------------*/
	public void refreshCamera(Camera camera) {
		Log.e("mCamera_test_work","refreshCamera");
		if (surfaceHolder.getSurface() == null) {
			return;
		}
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
		}
		setCameraParameter(camera);
		try {
			mCamera.setPreviewDisplay(surfaceHolder);
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            	mCamera.setDisplayOrientation(90);
               } else {
            	   mCamera.setDisplayOrientation(0);
               }			
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d("VIEW_LOG_TAG", "Error starting camera preview: " + e.getMessage());
		}
	}
/*--------------------------refreshCamera----------------------------------------------*/	
/*-------------------------------------------------------------------------------------*/	
	
	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			Log.e("mCamera_test_work","onAutoFocus");
			if(success){
				mCamera=camera;
			}
		}
	};
	
		
/*---------------------------------------------------------------------------------------------*/
/*--------------------------prepareMediaRecorder----------------------------------------------*/	
	public boolean prepareMediaRecorder() {
		Log.e("mCamera_test_work","prepareMediaRecorder");
		cancelfocus = false;
		//init();
		refreshCamera(mCamera);
		mediaRecorder = new MediaRecorder();
		mCamera.unlock();
		mediaRecorder.reset();
		mediaRecorder.setOrientationHint(90);
		mediaRecorder.setCamera(mCamera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
		mediaRecorder.setVideoEncodingBitRate(2000000000);
		mediaRecorder.setVideoSize(640, 480);
		mediaRecorder.setVideoFrameRate(60);
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		mediaRecorder.setOutputFile(recordfilepath());

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder(); 
		}
		return true;

	}
/*--------------------------prepareMediaRecorder----------------------------------------------*/	
/*--------------------------------------------------------------------------------------------*/	
	
	private void releaseMediaRecorder() {
		Log.e("mCamera_test_work","releaseMediaRecorder");
		if (mediaRecorder != null) {
			mediaRecorder.reset(); // clear recorder configuration
			mediaRecorder.release(); // release the recorder object
			mediaRecorder = null;
			mCamera.lock(); // lock camera for later use

		}
	}
	private String recordfilepath() {
		Log.e("mCamera_test_work","recordfilepath");
		// TODO Auto-generated method stub
		//String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File sddir =  Environment.getExternalStorageDirectory();
		File vrdir = new File(sddir, "videooo");
		if(!vrdir.exists()){
			vrdir.mkdir();
		}
		time.setToNow();
		/*String month = String.valueOf(time.month + 1);
		String Date = String.valueOf(time.monthDay);
		String hour = String.valueOf(time.hour);
		String min = String.valueOf(time.minute);
		String sec = String.valueOf(time.second);
		String  showTimefile = month + "M" + Date + "D_" + hour + "h" + min + "m" + sec+"s";
		File file = new File(vrdir, showTimefile+" video.mp4 ");*/
		File file = new File(vrdir, "video.mp4");
		String filepath = file.getAbsolutePath();
		return filepath;
	}
	
	@Override
	protected void onPause() {
		Log.e("mCamera_test_work","onPause");
		super.onPause();
		// when on Pause, release camera in order to be used from other
		// applications
		releaseCamera();
	}
	private void releaseCamera() {
		Log.e("mCamera_test_work","releaseCamera");
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
}
