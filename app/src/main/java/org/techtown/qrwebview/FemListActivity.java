package org.techtown.qrwebview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class FemListActivity extends Activity {
    WebView wv_list;
    ImageView btn_nav_home, btn_nav_qrcode, btn_nav_logout;
    private long backKeyPressedTime = 0;
    private Toast toast;
    private String empnum = "";
    private String name = "";
    private String address = "";
    private String ip = "";
    private SharedPreferences appData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_femlist);
        hideNavigationBar();

        appData = getSharedPreferences("appData", 0);
        load();

        address = "http://" + ip + "/ppts/qr/dept_fe_list.php?empnum=" + empnum;
        if (!address.startsWith("http://")) {
            address = "http://" + address;
        }
        Toast.makeText(FemListActivity.this, "소화기 관리페이지 \n접속자: " + name, Toast.LENGTH_SHORT).show();
        wv_list = (WebView) findViewById(R.id.wv_list);
        wv_list.loadUrl(address);
        btn_nav_home = (ImageView) findViewById(R.id.btn_nav_home);
        btn_nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
        btn_nav_qrcode = (ImageView) findViewById(R.id.btn_nav_qr);
        btn_nav_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btn_nav_logout = (ImageView) findViewById(R.id.btn_nav_logout);
        btn_nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FemListActivity.this);
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setTitle("경고")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                Intent intent = new Intent(getBaseContext(), StartActivity.class);
                                startActivity(intent);


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("경고");
                alert.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            ActivityCompat.finishAffinity(FemListActivity.this);
            System.exit(0);
            toast.cancel();
        }

    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        empnum = appData.getString("ID", "");
        name = appData.getString("NAME", "");
        ip = appData.getString("IP", "1.221.186.67");


    }

    private void hideNavigationBar() {
        this.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );
    }

}
