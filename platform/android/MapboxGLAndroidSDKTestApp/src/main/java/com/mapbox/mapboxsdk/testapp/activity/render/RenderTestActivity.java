package com.mapbox.mapboxsdk.testapp.activity.render;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.snapshotter.MapSnapshotter;
import com.mapbox.mapboxsdk.testapp.R;

import java.io.File;
import java.io.FileOutputStream;

import timber.log.Timber;

public class RenderTestActivity extends AppCompatActivity {

  private Bitmap bitmap;
  private ImageView imageView;
  private MapSnapshotter mapSnapshotter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(imageView = new ImageView(RenderTestActivity.this));
    addSnapshot();
  }

  private void addSnapshot() {
    MapSnapshotter.Options options = new MapSnapshotter
      .Options(512, 512)
      .withCameraPosition(
        new CameraPosition.Builder()
          .target(new LatLng(50.846728, 4.352429))
          .zoom(12)
          .build()
      );

    mapSnapshotter = new MapSnapshotter(RenderTestActivity.this, options);
    mapSnapshotter.start(new MapboxMap.SnapshotReadyCallback() {
      @Override
      public void onSnapshotReady(final Bitmap snapshot) {
        imageView.setImageBitmap(snapshot);

        if (isExternalStorageWritable()) {
          try {
            File testResultDir = new File(Environment.getExternalStorageDirectory() + "/mapbox");
            if (!testResultDir.exists()) {
              if (!testResultDir.mkdirs()) {
                throw new RuntimeException("can't create directory");
              }
            }

            String filePath = testResultDir.getAbsolutePath() + "/actual.png";
            Timber.e("FilePath: %s", filePath);

            FileOutputStream out = new FileOutputStream(filePath);
            snapshot.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
          } catch (Exception e) {
            Timber.e(e);
          }
        }
        bitmap = snapshot;
      }
    }, new MapSnapshotter.ErrorHandler() {
      @Override
      public void onError(String error) {
        Timber.e(error);
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapSnapshotter.cancel();
  }

  private boolean isExternalStorageWritable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  @VisibleForTesting
  public Bitmap getBitmap() {
    return bitmap;
  }
}
