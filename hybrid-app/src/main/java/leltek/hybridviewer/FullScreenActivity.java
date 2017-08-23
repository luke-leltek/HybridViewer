package leltek.hybridviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//import com.puma.leltek.Base.BaseActivity;
//import com.puma.leltek.R;

//import butterknife.Bind;
//import butterknife.ButterKnife;

public class FullScreenActivity extends AppCompatActivity implements View.OnClickListener {

//    @Bind(R.id.imgBack) ImageView ImgBack;
private UsImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        mImageView = findViewById(R.id.image_view);

//        ButterKnife.bind(this);

//        loadLayout();
    }

//    private void loadLayout(){
//        ImgBack.setOnClickListener(this);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgBack:
                finish();
                break;
        }
    }
}
