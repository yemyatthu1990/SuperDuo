package it.jaschke.alexandria;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by yemyatthu on 7/18/15.
 */
public class ScanISBN extends DialogFragment implements ZXingScannerView.ResultHandler{
  public static final String BARCODE_SCANNED = "barcode_scanned";
  public static final String BARCODE_DATA = "barcode_data";
  private ZXingScannerView scannerView;
  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_scan_isbn,container,false);
    scannerView = (ZXingScannerView) view.findViewById(R.id.scanner_view);
    return view;
  }

  @Override public void handleResult(Result rawResult) {
    try {
      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
      r.play();
    } catch (Exception e) {}
    Toast.makeText(getActivity(),
        "Contents = " + rawResult.getText() + ", Format = " + rawResult.getBarcodeFormat()
            .toString(), Toast.LENGTH_LONG).show();
    Intent intent =  new Intent(BARCODE_SCANNED);
    intent.putExtra(BARCODE_DATA,rawResult.getText());
    getActivity().sendBroadcast(intent);
    dismiss();
  }

  @Override public void onResume() {
    super.onResume();
    scannerView.startCamera();
    scannerView.setResultHandler(this);
  }

  @Override public void onPause() {
    super.onPause();
    scannerView.stopCamera();
    scannerView.setAutoFocus(true);
  }
}
