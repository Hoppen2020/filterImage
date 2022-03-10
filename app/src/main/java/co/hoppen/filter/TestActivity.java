package co.hoppen.filter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YangJianHui on 2022/3/4.
 */
public class TestActivity extends AppCompatActivity {
   private TextView tv_time;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_test);
      tv_time = findViewById(R.id.tv_time);
   }

   public void set(View view){
      DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String format = parseFormat.format(new Date());
      modifySystemTime(format);
   }


   public  String execRootCmd(String cmd) {
      String result = "";
      DataOutputStream dos = null;
      DataInputStream dis = null;
      try {
         Process p = Runtime.getRuntime().exec("su");
         dos = new DataOutputStream(p.getOutputStream());
         dis = new DataInputStream(p.getInputStream());

         dos.writeBytes(cmd + "\n");
         dos.flush();
         dos.writeBytes("exit\n");
         dos.flush();
         String line = null;
         while ((line = dis.readLine()) != null) {
            LogUtils.e(line);
            tv_time.setText(tv_time.getText().toString() + line);
            result += line;
         }
         p.waitFor();
      } catch (Exception e) {
         tv_time.setText(tv_time.getText().toString() + e.toString());
         e.printStackTrace();
      } finally {
         if (dos != null) {
            try {
               dos.close();
            } catch (IOException e) {
               tv_time.setText(tv_time.getText().toString() + e.toString());
               e.printStackTrace();
            }
         }
         if (dis != null) {
            try {
               dis.close();
            } catch (IOException e) {
               tv_time.setText(tv_time.getText().toString() + e.toString());
               e.printStackTrace();
            }
         }
      }
      return result;
   }

   public  void modifySystemTime(String time){
      try {
         DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date date = parseFormat.parse(time);
         time = new SimpleDateFormat("MMddHHmmyyyy.ss").format(date);
         LogUtils.e(time);
         execRootCmd("date " + time
                 + "\n busybox hwclock -w\n");
      }catch (Exception e){
      }
   }

}
