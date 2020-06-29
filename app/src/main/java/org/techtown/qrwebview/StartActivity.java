package org.techtown.qrwebview;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class StartActivity extends Activity {
    String empnum = ""; //SharedPreference에 저장될 사번
    private Button btn_enter, btn_dev; // 로그인 버튼
    private EditText et_empnum; // 로그인 입력 폼
    private boolean saveLoginData; // 저장된 사번이 있는지 체크
    private String id, name, dept_name, ip; // Shared Preference로부터 사번, 이름, 부서명을 불러올 변수
    private CheckBox checkBox; // 로그인 정보 저장 체크박스
    private SharedPreferences appData; // SharedPreferences 사용을 위한 객체
    private long backKeyPressedTime = 0; // 뒤로가기 2회시 앱 종료 기능을 위한 시간
    private Toast toast; // 토스트 메세지 출력을 위한 객체
    int code = 0; // 웹으로부터 통신 메세지를 받을 변수
    String json; // Json을 파싱하여 저장할 변수
    final String PREF_FIRST_START = "AppFirstLaunch";

    @Override
    // onCreate 함수
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        hideNavigationBar();        // 상,하단 네비 숨김
        SharedPreferences settings = getSharedPreferences(PREF_FIRST_START, 0);
        if(settings.getBoolean("AppFirstLaunch", true)){  // 아이콘이 두번 추가 안되도록 하기 위해서 필요한 체크입니다.


            settings.edit().putBoolean("AppFirstLaunch", false).commit();

            if (ShortcutManagerCompat.isRequestPinShortcutSupported(this))
            {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(this, "#1")
                        .setIntent(new Intent(this, LoadingActivity.class).setAction(Intent.ACTION_MAIN)) // !!! intent's action must be set on oreo
                        .setShortLabel(getString(R.string.app_name)) //  아이콘에 같이 보여질 이름
                        .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher))  //아이콘에 보여질 이미지
                        .build();
                ShortcutManagerCompat.requestPinShortcut(this, shortcutInfo, null);
            }
            else
            {
                // Shortcut is not supported by your launcher
            }

        }



        et_empnum = (EditText) findViewById(R.id.et_empnum);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        appData = getSharedPreferences("appData", 0);
        load();         // SharedPreferences 값 불러옴
        et_empnum.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    btn_enter.callOnClick();
                }
                return false;
            }
        });
        if (saveLoginData) { // 이미 저장된 값이 있을경우
            et_empnum.setText(id); // id 입력
            checkBox.setChecked(saveLoginData); // 체크박스에 체크
        }
        btn_dev = (Button) findViewById(R.id.btn_dev);
        btn_dev.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getBaseContext(), DevActivity.class);
                startActivity(intent);
                return true;
            }
        });

        btn_enter = (Button) findViewById(R.id.btn_Enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                empnum = et_empnum.getText().toString(); // 입력폼에 입력된 값을 저장
                ContentValues values = new ContentValues();
                values.put("empnum", empnum); // SharedPreferences에 사번 저장
                values.put("IP", ip); // SharedPreferences에 IP 저장
                // URL 설정.
                String url = "http://" + ip + "/ppts/user/account_num.php";
                // AsyncTask를 통해 HttpURLConnection 수행.
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();



            }
        });

    }


    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id = appData.getString("ID", "");
        ip = appData.getString("IP", "1.221.186.67");
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            json = requestHttpURLConnection.request(url, values);
            try {
                JSONObject jsonObject = new JSONObject(json);
                code = jsonObject.getInt("code");
                name = jsonObject.getString("name");
                dept_name = jsonObject.getString("dept_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (code) {
                case 200:

                    SharedPreferences.Editor editor = appData.edit();

                    // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
                    // 저장시킬 이름이 이미 존재하면 덮어씌움

                    editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
                    editor.putString("ID", et_empnum.getText().toString().trim());
                    editor.putString("NAME", name.trim());
                    editor.putString("DEPT_NAME", dept_name.trim());
                    // apply, commit 을 안하면 변경된 내용이 저장되지 않음
                    editor.commit();
                    Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                    startActivity(intent);
                    break;
                case 401:

                    Toast.makeText(StartActivity.this, "사원번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    break;
                case 404:

                    Toast.makeText(StartActivity.this, "존재하지 않는 사원번호입니다.", Toast.LENGTH_SHORT).show();
                    break;
            }

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        }


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
            ActivityCompat.finishAffinity(StartActivity.this);
            System.exit(0);
            toast.cancel();
        }

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


