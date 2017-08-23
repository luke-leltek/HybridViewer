package leltek.hybridviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;

import com.corelibs.utils.ToastMgr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leltek.viewer.model.Probe;
import leltek.viewer.model.WifiProbe;
import leltek.viewer.model.SimuProbe;
import leltek.viewer.model.SimuProbeLinear;

public class ScanActivity extends AppCompatActivity
        implements Probe.ScanListener, Probe.CineBufferListener,
        Probe.BatteryListener, Probe.TemperatureListener {
    final static Logger logger = LoggerFactory.getLogger(ScanActivity.class);

    private boolean mode_sim = false;
    private boolean mode_test = false;
    private Probe probe;
    private Button mToggleScan;
    private Button mBMode;
    private Button mCMode;
    private Button mFit;
    private UsImageView mImageView;
    private TextView mCineBufferCount;
    private Button mTestConnectionError;
    private Button mTestOverHeated;
    private Button mTestBatteryLow;
    private SeekBar mSeekBarGain;
    private SeekBar mSeekBarDr;
    private SeekBar mSeekBarTgc1;
    private SeekBar mSeekBarTgc2;
    private SeekBar mSeekBarTgc3;
    private SeekBar mSeekBarTgc4;
    private SeekBar mSeekBarPersistence;
    private SeekBar mSeekBarEnhancement;
    private Button mResetAllTgc;
    private NumberPicker mNumberPicker;
    private Spinner mSpinnerColorPrf;
    private SeekBar mSeekBarColorSensitivity;
    private Spinner mSpinnerColorAngle;

    private TextView txvAdv;
    private TextView txvCinebufcnt;
    private TextView txvGray;
    private TextView txvDr;
    private TextView txvTgc1;
    private TextView txvTgc2;
    private TextView txvTgc3;
    private TextView txvTgc4;

    private ImageView imgFull;

    private boolean mode_adv= false;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, ScanActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.debug("onCreate() called");
        setContentView(R.layout.activity_scan);

        probe = ProbeSelection.simu ? (ProbeSelection.simuLinear ? SimuProbeLinear.getDefault() : SimuProbe.getDefault()) : WifiProbe.getDefault();
        probe.setScanListener(this);
        probe.setCineBufferListener(this);
        probe.setBatteryListener(this);
        probe.setTemperatureListener(this);

	////////////////////////////////////////////////////////////////////////
	// main image area for bmode/color in the center
	////////////////////////////////////////////////////////////////////////
        mImageView = findViewById(R.id.image_view);

	////////////////////////////////////////////////////////////////////////
	// information area on the right
	////////////////////////////////////////////////////////////////////////
        imgFull = findViewById(R.id.imgFull);
	imgFull.setVisibility(View.VISIBLE);
        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	//	startActivity(new Intent(this, FullScreenActivity.class));
                gotoFullScreen();
            }
        });


	////////////////////////////////////////////////////////////////////////
	// control area on the left
	////////////////////////////////////////////////////////////////////////
        mToggleScan = findViewById(R.id.toogle_scan);
        mToggleScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (probe.isLive())
                    probe.stopScan();
                else {
                    probe.startScan();
                }
            }
        });

        mBMode = findViewById(R.id.bMode);
        mBMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                probe.switchToBMode();
            }
        });

        mCMode = findViewById(R.id.cMode);
        mCMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                probe.switchToCMode();
            }
        });


        mSeekBarGain = findViewById(R.id.seekBarGain);
        mSeekBarGain.setProgress(probe.getGain());
        mSeekBarGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setGain(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSeekBarPersistence = findViewById(R.id.seekBarPersistence);
        //mSeekBarPersistence.setProgress(probe.getPersistence());
        //mSeekBarPersistence.setProgress(2);
                probe.setPersistence(2);
        mSeekBarPersistence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setPersistence(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarEnhancement = findViewById(R.id.seekBarEnhancement);
        //mSeekBarEnhancement.setProgress(probe.getEnhanceLevel());
                probe.setEnhanceLevel(2);
        mSeekBarEnhancement.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setEnhanceLevel(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        mSpinnerColorPrf = findViewById(R.id.spinnerColorPrf);
        mSpinnerColorPrf.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Probe.EnumColorPrf.values()));

        mSpinnerColorPrf.setSelection(((ArrayAdapter<Probe.EnumColorPrf>) mSpinnerColorPrf.getAdapter())
                .getPosition(probe.getColorPrf()));

        mSpinnerColorPrf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Probe.EnumColorPrf newColorPrf = (Probe.EnumColorPrf) parent.getItemAtPosition(position);
                probe.setColorPrf(newColorPrf);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSeekBarColorSensitivity = findViewById(R.id.seekBarColorSensitivity);
        mSeekBarColorSensitivity.setProgress(probe.getColorSensitivity().getIntValue());
        mSeekBarColorSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for (Probe.EnumColorSensitivity s : Probe.EnumColorSensitivity.values()) {
                    if (s.getIntValue() == progress) {
                        probe.setColorSensitivity(s);
                        logger.debug("Color Sensitivity: {}", s.toString());
                        break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSpinnerColorAngle = findViewById(R.id.spinnerColorAngle);
        mSpinnerColorAngle.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Probe.EnumColorAngle.values()));

        mSpinnerColorAngle.setSelection(((ArrayAdapter<Probe.EnumColorAngle>) mSpinnerColorAngle.getAdapter())
                .getPosition(probe.getColorAngle()));

        mSpinnerColorAngle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Probe.EnumColorAngle newColorAngle = (Probe.EnumColorAngle) parent.getItemAtPosition(position);
                probe.setColorAngle(newColorAngle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	
	////////////////////////////////////////////////////////////////////////
	// txvAdv : 
	// . when click, the following setting could be visible for internal test only
	// . toggle between GONE and visible for these setting for clean screen
	// . let txvAdv GONE for customer release
	// . default GONE for the advanced setting
	////////////////////////////////////////////////////////////////////////
        txvAdv = findViewById(R.id.txvAdv);
	txvAdv.setVisibility(View.VISIBLE);
        txvAdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		    if(mode_adv ){
		    	mode_adv = false;
		    	//txvAdv.setVisibility(View.VISIBLE);
			txvCinebufcnt.setVisibility(View.VISIBLE);
			mCineBufferCount.setVisibility(View.VISIBLE);
		    	mNumberPicker.setVisibility(View.VISIBLE);
		    	txvGray.setVisibility(View.VISIBLE);
		    	txvDr.setVisibility(View.VISIBLE);
			mSeekBarDr.setVisibility(View.VISIBLE);
		    	txvTgc1.setVisibility(View.VISIBLE);
		    	txvTgc2.setVisibility(View.VISIBLE);
		    	txvTgc3.setVisibility(View.VISIBLE);
		    	txvTgc4.setVisibility(View.VISIBLE);
			mSeekBarTgc1.setVisibility(View.VISIBLE);
			mSeekBarTgc2.setVisibility(View.VISIBLE);
			mSeekBarTgc3.setVisibility(View.VISIBLE);
			mSeekBarTgc4.setVisibility(View.VISIBLE);
			mFit.setVisibility(View.VISIBLE);
			mResetAllTgc.setVisibility(View.VISIBLE);
			mTestConnectionError.setVisibility(View.VISIBLE);
			mTestOverHeated.setVisibility(View.VISIBLE);
			mTestBatteryLow.setVisibility(View.VISIBLE);
		    } else {
		    	mode_adv = true;
		    	//txvAdv.setVisibility(View.VISIBLE);
			txvCinebufcnt.setVisibility(View.GONE);
			mCineBufferCount.setVisibility(View.GONE);
		    	mNumberPicker.setVisibility(View.GONE);
		    	txvGray.setVisibility(View.GONE);
		    	txvDr.setVisibility(View.GONE);
			mSeekBarDr.setVisibility(View.GONE);
		    	txvTgc1.setVisibility(View.GONE);
		    	txvTgc2.setVisibility(View.GONE);
		    	txvTgc3.setVisibility(View.GONE);
		    	txvTgc4.setVisibility(View.GONE);
			mSeekBarTgc1.setVisibility(View.GONE);
			mSeekBarTgc2.setVisibility(View.GONE);
			mSeekBarTgc3.setVisibility(View.GONE);
			mSeekBarTgc4.setVisibility(View.GONE);
			mFit.setVisibility(View.GONE);
			mResetAllTgc.setVisibility(View.GONE);
			mTestConnectionError.setVisibility(View.GONE);
			mTestOverHeated.setVisibility(View.GONE);
			mTestBatteryLow.setVisibility(View.GONE);
		    }
            }
        });

	// CineBufferCount display 
        txvCinebufcnt = findViewById(R.id.txvCinebufcnt);
	txvCinebufcnt.setVisibility(View.GONE);
        mCineBufferCount = findViewById(R.id.cine_buffer_count);
	mCineBufferCount.setVisibility(View.GONE);

	// dynamice range
        txvDr = findViewById(R.id.txvDr);
	txvDr.setVisibility(View.GONE);
        mSeekBarDr = findViewById(R.id.seekBarDr);
	mSeekBarDr.setVisibility(View.GONE);
        mSeekBarDr.setProgress(probe.getDr());
        mSeekBarDr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setDr(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


	// gray map
        txvGray = findViewById(R.id.txvGray);
	txvGray.setVisibility(View.GONE);
        txvGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mNumberPicker = findViewById(R.id.np);
	mNumberPicker.setVisibility(View.GONE);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(probe.getGrayMapMaxValue());
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setValue(probe.getGrayMap());
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                probe.setGrayMap(newVal);
            }
        });

	///////////////
	// digital tgc
	///////////////
        txvTgc1 = findViewById(R.id.txvTgc1);
        txvTgc2 = findViewById(R.id.txvTgc2);
        txvTgc3 = findViewById(R.id.txvTgc3);
        txvTgc4 = findViewById(R.id.txvTgc4);

        mSeekBarTgc1 = findViewById(R.id.seekBarTgc1);
        mSeekBarTgc1.setProgress(probe.getTgc1());
        mSeekBarTgc1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setTgc1(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarTgc2 = findViewById(R.id.seekBarTgc2);
        mSeekBarTgc2.setProgress(probe.getTgc2());
        mSeekBarTgc2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setTgc2(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarTgc3 = findViewById(R.id.seekBarTgc3);
        mSeekBarTgc3.setProgress(probe.getTgc3());
        mSeekBarTgc3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setTgc3(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarTgc4 = findViewById(R.id.seekBarTgc4);
        mSeekBarTgc4.setProgress(probe.getTgc4());
        mSeekBarTgc4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                probe.setTgc4(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mResetAllTgc = findViewById(R.id.resetAllTgc);
        mResetAllTgc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                probe.resetAllTgc();
                mSeekBarTgc1.setProgress(probe.getTgc1());
                mSeekBarTgc2.setProgress(probe.getTgc2());
                mSeekBarTgc3.setProgress(probe.getTgc3());
                mSeekBarTgc4.setProgress(probe.getTgc4());
            }
        });

	// fit for width or height
//    if(mode_test==true) {
        mFit = findViewById(R.id.fit);
        mFit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFit();
            }
        });
//    }  // if(mode_test==true) {



	///////////////
	// test mode
	///////////////
        mTestConnectionError = findViewById(R.id.test_conn_error);
        mTestConnectionError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		if(ProbeSelection.simu) {
		    if(ProbeSelection.simuLinear) {
			((SimuProbeLinear) probe).testConnectionClosed();
		    } else {
			((SimuProbe) probe).testConnectionClosed();
		    }
		} else {
		    ((WifiProbe) probe).testConnectionClosed();
		}
            }
        });

        mTestOverHeated = findViewById(R.id.test_over_heated);
        mTestOverHeated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		if(ProbeSelection.simu) {
		    if(ProbeSelection.simuLinear) {
			((SimuProbeLinear) probe).testOverHeated();
		    } else {
			((SimuProbe) probe).testOverHeated();
		    }
		} else {
		    ((WifiProbe) probe).testOverHeated();
		}
            }
        });


        mTestBatteryLow = findViewById(R.id.test_battery_low);
        mTestBatteryLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		if(ProbeSelection.simu) {
		    if(ProbeSelection.simuLinear) {
			((SimuProbeLinear) probe).testBatteryLevelTooLow();
		    } else {
			((SimuProbe) probe).testBatteryLevelTooLow();
		    }
		} else {
		    ((WifiProbe) probe).testBatteryLevelTooLow();
		}
            }
        });

	// initial gone
	txvTgc1.setVisibility(View.GONE);
	txvTgc2.setVisibility(View.GONE);
	txvTgc3.setVisibility(View.GONE);
	txvTgc4.setVisibility(View.GONE);
	mSeekBarTgc1.setVisibility(View.GONE);
	mSeekBarTgc2.setVisibility(View.GONE);
	mSeekBarTgc3.setVisibility(View.GONE);
	mSeekBarTgc4.setVisibility(View.GONE);
	mFit.setVisibility(View.GONE);
	mResetAllTgc.setVisibility(View.GONE);
	mTestConnectionError.setVisibility(View.GONE);
	mTestOverHeated.setVisibility(View.GONE);
	mTestBatteryLow.setVisibility(View.GONE);

    }

    private void switchFit() {
        mImageView.switchFit();
        if (mImageView.isFitWidth()) {
            mFit.setText(R.string.fit_height);
        } else {
            mFit.setText(R.string.fit_width);
        }
    }

    private void gotoFullScreen(){

        //Intent intent = new Intent(this, PatientActivity.class);
        //startActivity(intent);
        startActivity(new Intent(this, FullScreenActivity.class));
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
        if (probe.getMode() == Probe.EnumMode.MODE_C) {
            probe.switchToBMode();
        } if (probe.getMode() == Probe.EnumMode.MODE_B && !probe.isLive()) {
            probe.startScan();
        }
    }

    @Override
    public void onModeSwitched(Probe.EnumMode mode) {
        if (mode == Probe.EnumMode.MODE_B) {
            ToastMgr.show("switched to B mode");
        }
        else if (mode == Probe.EnumMode.MODE_C) {
            ToastMgr.show("switched to Color mode");
        }
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
        if (frame.mode == probe.getMode()) {
            mImageView.setImageBitmap(bitmap);
        }

        Probe.CModeFrameData cModeFrameData = frame.cModeFrameData;
        if (cModeFrameData != null) {
            mImageView.setParams(cModeFrameData.originXPx, cModeFrameData.originYPx,
                    cModeFrameData.rPx);
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
        mSpinnerColorPrf.setSelection(((ArrayAdapter<Probe.EnumColorPrf>) mSpinnerColorPrf.getAdapter())
                .getPosition(oldColorPrf));
    }

    @Override
    public void onColorSensitivitySet(Probe.EnumColorSensitivity newColorSensitivity) {

    }

    @Override
    public void onColorSensitivitySetError(Probe.EnumColorSensitivity oldColorSensitivity) {
        mSeekBarColorSensitivity.setProgress(oldColorSensitivity.getIntValue());
    }

    @Override
    public void onColorAngleSet(Probe.EnumColorAngle newColorAngle) {

    }

    @Override
    public void onColorAngleSetError(Probe.EnumColorAngle oldColorAngle) {
        mSpinnerColorAngle.setSelection(((ArrayAdapter<Probe.EnumColorAngle>) mSpinnerColorAngle.getAdapter())
                .getPosition(oldColorAngle));
    }

    @Override
    public void onImageBufferOverflow() {
        ToastMgr.show("This hardware is not capable of image processing.");
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
