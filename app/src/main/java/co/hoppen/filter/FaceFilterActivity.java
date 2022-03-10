package co.hoppen.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import co.hoppen.filterlib.FilterHelper;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;
import co.hoppen.filterlib.OnFilterListener;

/**
 * Created by YangJianHui on 2022/3/3.
 */
public class FaceFilterActivity extends AppCompatActivity implements OnFilterListener {
   private ImageView filterView;
   private FilterHelper filterHelper;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_filter);
      filterHelper = new FilterHelper(this);
      initView();
      Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/test/1646293161472.jpg");
      filterView.setImageBitmap(bitmap);
      try {
         filterHelper.execute(FilterType.FACE_SKIN_EPIDERMIS_SPOTS,bitmap,0);


      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onResume() {
      super.onResume();
   }

   private void initView() {
      filterView = findViewById(R.id.iv_filter);
   }

   @Override
   public void OnFilter(FilterInfoResult filterInfoResult) {
      Bitmap filterBitmap = filterInfoResult.getFilterBitmap();
      filterView.setImageBitmap(filterBitmap);
   }


}
