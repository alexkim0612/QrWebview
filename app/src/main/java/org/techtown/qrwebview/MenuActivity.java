package org.techtown.qrwebview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends Activity {
    private String name, dept_name;
    private SharedPreferences appData;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    TextView tv_name, tv_dept_name;
    ImageView btn_logout;
    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        recyclerView = findViewById(R.id.recycler_view);
        hideNavigationBar();
        appData = getSharedPreferences("appData", 0);
        load();
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_dept_name = (TextView) findViewById(R.id.tv_dept_name);
        tv_name.setText(name);
        tv_dept_name.setText(dept_name);

        btn_logout = (ImageView) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
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


        // 리사이클러뷰의 notify()처럼 데이터가 변했을 때 성능을 높일 때 사용한다.
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[] textSet = {"Boiler Feed Pump - Pump \nB9:40:7F:30:F1", "Boiler Feed Pump - Turbine \n59:B0:7F:E6:45", "Boiler Feed Pump - Motor \nB21:B3:E5:05:01", "Boost-Up Fan - Fan \n21:C3:04:01:E2"};
        int[] imgSet = {R.drawable.wifi, R.drawable.wifi, R.drawable.wifi, R.drawable.wifi};
        int[] imgSet2 = {R.drawable.btn, R.drawable.btn, R.drawable.btn, R.drawable.btn};
        // 어댑터 할당, 어댑터는 기본 어댑터를 확장한 커스텀 어댑터를 사용할 것이다.
        adapter = new MyAdapter(textSet, imgSet, imgSet2);
        recyclerView.setAdapter(adapter);

    }

    public void cctv(View v) {
        Toast.makeText(getApplicationContext(), "아직 개발중인 기능입니다.", Toast.LENGTH_SHORT).show();
    }

    public void fem(View v) {
        Intent intent = new Intent(getBaseContext(), FemListActivity.class);
        startActivity(intent);
    }

    public void fire(View v) {
        Toast.makeText(getApplicationContext(), "아직 개발중인 기능입니다.", Toast.LENGTH_SHORT).show();
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
            ActivityCompat.finishAffinity(MenuActivity.this);
            System.exit(0);
            toast.cancel();
        }

    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        name = appData.getString("NAME", "");
        dept_name = appData.getString("DEPT_NAME", "");
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
