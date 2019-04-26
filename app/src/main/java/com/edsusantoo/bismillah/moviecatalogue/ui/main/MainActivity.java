package com.edsusantoo.bismillah.moviecatalogue.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.edsusantoo.bismillah.moviecatalogue.R;
import com.edsusantoo.bismillah.moviecatalogue.ui.changelanguage.ChangeLanguage;
import com.edsusantoo.bismillah.moviecatalogue.ui.main.adapter.MainViewPagerAdapater;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE_CHANGE_LANGUAGE = 101;
    private String language = null;

    private TabLayout tabLayoutMain;
    private ViewPager viewPagerMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imgSetting = findViewById(R.id.img_setting);
        tabLayoutMain = findViewById(R.id.tab_main);
        viewPagerMain = findViewById(R.id.view_pager_main);

        setViewPagerMain();
        setTabLayoutMain();

        imgSetting.setOnClickListener(this);


    }

    private void setViewPagerMain() {
        MainViewPagerAdapater mainViewPagerAdapater = new MainViewPagerAdapater(getSupportFragmentManager());
        viewPagerMain.setAdapter(mainViewPagerAdapater);
    }

    private void setTabLayoutMain() {
        tabLayoutMain.setupWithViewPager(viewPagerMain);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_setting:
                Intent i = new Intent(MainActivity.this, ChangeLanguage.class);
                i.putExtra(ChangeLanguage.EXTRA_LANGUAGE, language);
                startActivityForResult(i, REQUEST_CODE_CHANGE_LANGUAGE);
                break;
        }
    }


    private void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHANGE_LANGUAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    language = data.getStringExtra(ChangeLanguage.EXTRA_LANGUAGE);
                    if (language != null) {
                        setLanguage(language);

                        //untuk refresh
                        setTabLayoutMain();
                        setViewPagerMain();
                    }
                }
            }
        }
    }

}
