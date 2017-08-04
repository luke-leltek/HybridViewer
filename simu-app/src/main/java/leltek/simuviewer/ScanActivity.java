package leltek.simuviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.corelibs.utils.ToastMgr;
import com.glidebitmappool.GlideBitmapPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leltek.viewer.model.Probe;
import leltek.viewer.model.SimuProbe;

public class ScanActivity extends AppCompatActivity
        implements Probe.ScanListener, Probe.CineBufferListener,
        Probe.BatteryListener, Probe.TemperatureListener,
        View.OnTouchListener {
    final static Logger logger = LoggerFactory.getLogger(ScanActivity.class);

    private Probe probe;
    private Button mToggleScan;
    private Button mFit;
    private ImageView mImageView;
    private TextView mCineBufferCount;
    private Button mTestConnectionError;
    private Button mTestOverHeated;
    private Button mTestBatteryLow;

    private Matrix zoomMatrix = new Matrix();
    private Matrix savedZoomMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();
    private float oriDis = 1f;
    private Matrix fitHeightMatrix;
    private Matrix fitWidthMatrix;
    private boolean isFitWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate() called");
        setContentView(R.layout.activity_scan);

        probe = SimuProbe.getDefault();
        probe.setScanListener(this);
        probe.setCineBufferListener(this);
        probe.setBatteryListener(this);
        probe.setTemperatureListener(this);

        mToggleScan = (Button) findViewById(R.id.toogle_scan);
        mToggleScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (probe.isLive())
                    probe.stopScan();
                else {
                    startScan();
                }
            }
        });

        isFitWidth = false;
        mFit = (Button) findViewById(R.id.fit);
        mFit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFit();
            }
        });

        mImageView = (ImageView) findViewById(R.id.bMode_image_view);
        mImageView.setOnTouchListener(this);

        mCineBufferCount = (TextView) findViewById(R.id.cine_buffer_count);

        mTestConnectionError = (Button) findViewById(R.id.test_conn_error);
        mTestConnectionError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SimuProbe) probe).testConnectionClosed();
            }
        });

        mTestOverHeated = (Button) findViewById(R.id.test_over_heated);
        mTestOverHeated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SimuProbe) probe).testOverHeated();
            }
        });


        mTestBatteryLow = (Button) findViewById(R.id.test_battery_low);
        mTestBatteryLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SimuProbe) probe).testBatteryLevelTooLow();
            }
        });
    }

    private void startScan() {
        if (isFitWidth)
            mImageView.setImageMatrix(fitWidthMatrix);
        else
            mImageView.setImageMatrix(fitHeightMatrix);
        probe.startScan();
    }

    private void switchFit() {
        if (isFitWidth) {
            isFitWidth = false;
            mImageView.setImageMatrix(fitHeightMatrix);
            mFit.setText(R.string.fit_width);
        } else {
            isFitWidth = true;
            mImageView.setImageMatrix(fitWidthMatrix);
            mFit.setText(R.string.fit_height);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        logger.debug("onStart() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.debug("onStop() called");

        probe.stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logger.debug("onDestroy() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.debug("onPause() called");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.debug("onResume() called");
    }

    @Override
    public void onModeSwitched(Probe.EnumMode mode) {
        startScan();
    }

    @Override
    public void onModeSwitchingError() {
        ToastMgr.show("Switch mode failed");
    }

    @Override
    public void onConnectionError() {
        ToastMgr.show("Connection Closed");
        Intent intent = MainActivity.newIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onScanStarted() {
        mToggleScan.setText(R.string.freeze);

        ToastMgr.show("Scan Started");
    }

    @Override
    public void onScanStopped() {
        mToggleScan.setText(R.string.live);

        ToastMgr.show("Scan Stopped");
    }

    @Override
    public void onNewFrameReady(Probe.Frame frame, Bitmap bitmap) {
        Bitmap oldBitmap = null;
        BitmapDrawable drawable = (BitmapDrawable)mImageView.getDrawable();
        if (drawable != null) {
            oldBitmap = drawable.getBitmap();
        }
        mImageView.setImageBitmap(bitmap);

        if (oldBitmap != null) {
            GlideBitmapPool.putBitmap(oldBitmap);
        }
     }

    @Override
    public void onDepthSet(Probe.EnumDepth newDepth) {

    }

    @Override
    public void onDepthSetError(Probe.EnumDepth oldDepth) {

    }

    @Override
    public void onColorPrfSet(Probe.EnumColorPrf newColorPrf) {

    }

    @Override
    public void onColorPrfSetError(Probe.EnumColorPrf oldColorPrf) {

    }

    @Override
    public void onColorSensitivitySet(Probe.EnumColorSensitivity newColorSensitivity) {

    }

    @Override
    public void onColorSensitivitySetError(Probe.EnumColorSensitivity oldColorSensitivity) {

    }

    @Override
    public void onColorAngleSet(Probe.EnumColorAngle newColorAngle) {

    }

    @Override
    public void onColorAngleSetError(Probe.EnumColorAngle oldColorAngle) {

    }

    @Override
    public void onImageBufferOverflow() {
        ToastMgr.show("This hardware is not capable of image processing.");
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, ScanActivity.class);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus)
            return;

        float viewWidth = (float) mImageView.getWidth();
        float viewHeight = (float) mImageView.getHeight();

        int width = probe.getImageWidthPx();
        int height = probe.getImageHeightPx();

        float scaleWidth = viewWidth / width;
        fitWidthMatrix = new Matrix();
        fitWidthMatrix.postScale(scaleWidth, scaleWidth);
        fitWidthMatrix.postTranslate((viewWidth - width * scaleWidth) / 2, 0);

        float scaleHeight = viewHeight / height;
        fitHeightMatrix = new Matrix();
        fitHeightMatrix.postScale(scaleHeight, scaleHeight);
        fitHeightMatrix.postTranslate((viewWidth - width * scaleHeight) / 2, 0);

        probe.swithToBMode();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                zoomMatrix.set(view.getImageMatrix());
                savedZoomMatrix.set(zoomMatrix);
                startPoint.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oriDis = distance(event);
                if (oriDis > 10f) {
                    savedZoomMatrix.set(zoomMatrix);
                    midPoint = middle(event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    zoomMatrix.set(savedZoomMatrix);
                    zoomMatrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                } else if (mode == ZOOM) {
                    float newDist = distance(event);
                    if (newDist > 10f) {
                        zoomMatrix.set(savedZoomMatrix);
                        float scale = newDist / oriDis;
                        zoomMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    }
                }
                break;
        }
        view.setImageMatrix(zoomMatrix);
        return true;
    }

    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    @Override
    public void onCineBufferCountIncreased(int newCineBufferCount) {
        mCineBufferCount.setText(String.valueOf(newCineBufferCount));
    }

    @Override
    public void onCineBufferCleared() {
        mCineBufferCount.setText(String.valueOf(0));
    }

    @Override
    public void onBatteryLevelChanged(int newBatteryLevel) {
        // update battey level displayed on UI
    }

    @Override
    public void onBatteryLevelTooLow(int BatteryLevel) {
        ToastMgr.show("Battery level too low, now is " + BatteryLevel + "%");
    }

    @Override
    public void onTemperatureChanged(int newTemperature) {
        // update temperature displayed on UI
    }

    @Override
    public void onTemperatureOverHeated(int temperature) {
        ToastMgr.show("Temperature over heated, now is " + temperature + " °");
    }
}
