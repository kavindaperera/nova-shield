package com.nova.android.shield.ui.home.tabs.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nova.android.shield.R;
import com.nova.android.shield.preferences.ShieldPreferencesHelper;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowBarcodeActivity extends AppCompatActivity {

    // bind variables
    @BindView(R.id.qrOutput)
    ImageView qrOutput;

    @BindView(R.id.show_qr_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_barcode);

        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // user uuid
        UUID uuid = ShieldPreferencesHelper.getUserUuid(getApplication());
        String string = uuid.toString();

        // initialize multi format writer
        MultiFormatWriter writer = new MultiFormatWriter();

        // initialize bit matrix
        try {
            BitMatrix matrix = writer.encode(string, BarcodeFormat.QR_CODE, 350, 350);

            BarcodeEncoder encoder = new BarcodeEncoder();

            // initialize bitmap
            Bitmap bitmap = encoder.createBitmap(matrix);

            // set bitmap on image view
            qrOutput.setImageBitmap(bitmap);

            // initialize input manager
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            // hide keyboard
            manager.hideSoftInputFromWindow(qrOutput.getApplicationWindowToken(), 0);

            // increase display brightness
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = 1F;
            getWindow().setAttributes(layout);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}