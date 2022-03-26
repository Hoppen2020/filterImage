package co.hoppen.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
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
   private Bitmap bitmap,filterBitmap;
   private boolean filter = false;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_filter);
      filterHelper = new FilterHelper(this);
      initView();
      //局部 part_1647853734199.jpg  part_1647853736167.jpg  part_1647853739178.jpg  part_1647853742169.jpg
      // 好的RGB 鼻子 part_1648174912400

      //全脸 1646292908475
      // 敏感 1646292916231

      //毛孔 part_1648086669926.jpg

      //局部 平衡偏振 part_1648032452811.jpg  part_1648032454435.jpg part_1648032457679.jpg part_1648032460162.jpg

      bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/test/part_1648086669926.jpg");
      filterView.setImageBitmap(bitmap);
      try {
         filterHelper.execute(FilterType.FACE_FOLLICLE_CLEAN_DEGREE,bitmap,0);
      } catch (Exception e) {
         e.printStackTrace();
      }
      filterView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            filterView.setImageBitmap(filter?bitmap:filterBitmap);
            filter=!filter;
         }
      });
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
      filterBitmap = filterInfoResult.getFilterBitmap();
      filterView.setImageBitmap(filterBitmap);
      filter = true;
   }


}
