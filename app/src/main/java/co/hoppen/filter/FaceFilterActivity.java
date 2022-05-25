package co.hoppen.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;

import co.hoppen.filterlib.FilterHelper;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;
import co.hoppen.filterlib.OnFilterListener;
import co.hoppen.filterlib.filter.FaceSensitive;

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
      //敏感 1646292916231

      //毛孔 part_1648086669926.jpg

      //局部 平衡偏振 part_1648032452811.jpg  part_1648032454435.jpg part_1648032457679.jpg part_1648032460162.jpg

      //mlkit test2  测试 part_1648291671107.png

      //黑眼圈 test2 part_1648435139676.png;

      //hsv test t1.png

      //佩纯RGB局部，test2  part_1648518780296.png part_1648518783108.png part_1648518785449.png
      //part_1648522389880.png
      //  1648519736041


      //建材 RGB FaceDemo/1648519733193.jpg
      //建材 PL FaceDemo/1648519736041.jpg

      //分步骤 红色区 红色炎症底图 FaceDemo/1650507220656.jpg
      //红色炎症底图 加强 FaceDemo/1650526385412.jpg
      //pl原图 1648519736041
      // think view pl /FaceDemo/view2.jpg"
      // think view FaceDemo/1650524656726.jpg

      bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + "/FaceDemo/1648519733193.jpg");//part_1648543308051
      filterView.setImageBitmap(bitmap);
      executeFilter();
      filterView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            filterView.setImageBitmap(filter?bitmap:filterBitmap);
            filter=!filter;
         }
      });
      filterView.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {
//            filterView.setImageBitmap(filter?bitmap:filterBitmap);
//            filter=!filter;
//            executeFilter();
            if (filterBitmap!=null){
               save(filterBitmap);
            }
            return true;
         }
      });
   }

   private void executeFilter(){
      try {
         filterHelper.execute(FilterType.FACE_ACNE,bitmap,0);
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
   public void onFilter(FilterInfoResult filterInfoResult) {
      filterBitmap = filterInfoResult.getFilterBitmap();
      filterView.setImageBitmap(filterBitmap);
      filter = true;
   }

   public void save(Bitmap bitmap){
      try {
         File parentFile = new File(Environment.getExternalStorageDirectory().getPath()+"/FaceDemo");
         if (!parentFile.exists()) {
            parentFile.mkdirs();
         }
         String name = String.valueOf(System.currentTimeMillis()) + ".jpg";
         File file = new File(parentFile.getPath(), name);
         LogUtils.e(file.getPath());
         FileOutputStream outputStream = new FileOutputStream(file);
         bitmap.compress(Bitmap.CompressFormat.JPEG,
                 100, outputStream);
         outputStream.flush();
         outputStream.close();
         //保存完 执行下一个
         Toast.makeText(FaceFilterActivity.this,"save success",Toast.LENGTH_SHORT).show();
      }catch (Exception e){
      }
   }


}
