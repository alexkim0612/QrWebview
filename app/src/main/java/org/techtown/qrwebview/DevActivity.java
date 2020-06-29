package org.techtown.qrwebview;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class DevActivity extends Activity {
    EditText et_ip, dev_id;
    String savedIp = "";
    SharedPreferences appData_dev;
    private boolean saveLoginData_dev;
    Button btn_login;
    String id, ip;
    String validIp = "^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        hideNavigationBar();
        appData_dev = getSharedPreferences("appData", 0);
        load();

        et_ip = (EditText) findViewById(R.id.et_ip);
        if (saveLoginData_dev) {
            et_ip.setText(savedIp);
        }

        dev_id = (EditText) findViewById(R.id.dev_id);


        btn_login = (Button) findViewById(R.id.dev_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = et_ip.getText().toString();
                id = dev_id.getText().toString();
                if (Pattern.matches(validIp, ip)) {
                    int id_int = 0,nullcheck = 0;
                    try {
                        id_int = Integer.parseInt(id.trim());
                    } catch (NumberFormatException e) {
                        nullcheck = 1;
                    } 

                    if (id_int == 1234) {
                        SharedPreferences.Editor editor = appData_dev.edit();
                        editor.putBoolean("SAVE_LOGIN_DATA_DEV", true);
                        editor.putString("IP", ip.trim());
                        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
                        editor.commit();
                        Toast.makeText(DevActivity.this, "IP주소가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), StartActivity.class);
                        startActivity(intent);

                    }   else if (nullcheck==1) {
                        Toast.makeText(DevActivity.this, "DEV CODE ERROR.", Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(DevActivity.this, "IP주소의 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData_dev = appData_dev.getBoolean("SAVE_LOGIN_DATA_DEV", false);
        savedIp = appData_dev.getString("IP", "1.221.186.67");
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
