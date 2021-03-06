package com.edsusantoo.bismillah.moviecatalogue.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.edsusantoo.bismillah.moviecatalogue.BuildConfig;
import com.edsusantoo.bismillah.moviecatalogue.R;
import com.edsusantoo.bismillah.moviecatalogue.data.pref.SharedPref;
import com.edsusantoo.bismillah.moviecatalogue.service.MoviesReleaseWorkManager;
import com.edsusantoo.bismillah.moviecatalogue.service.ReleaseReminderReceiver;
import com.edsusantoo.bismillah.moviecatalogue.service.ReleaseReminderService;
import com.edsusantoo.bismillah.moviecatalogue.service.ReminderReceiver;
import com.edsusantoo.bismillah.moviecatalogue.utils.Constant;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.edsusantoo.bismillah.moviecatalogue.utils.Constant.EXTRAS_API_KEY;
import static com.edsusantoo.bismillah.moviecatalogue.utils.Constant.EXTRAS_LANGUAGE;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String EXTRA_LANGUAGE = "extra_language";
    public static final int REQUEST_CODE_DAILY_REMINDER = 100;
    public static final int REQUEST_CODE_RELEASE_REMINDER = 101;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rg_change_language)
    RadioGroup rgChangeLanguage;
    @BindView(R.id.rb_indonesia)
    RadioButton rbIndonesia;
    @BindView(R.id.rb_english)
    RadioButton rbEnglish;
    @BindView(R.id.sc_release_reminder)
    SwitchCompat scReleaseReminder;
    @BindView(R.id.sc_daily_reminder)
    SwitchCompat scDailyReminder;
    private String language = null;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntentDailyReminder;
    private PendingIntent pendingIntentReleaseReminder;

    private SettingsViewModel settingsViewModel;

    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishChangeLanguage();
                finish();
            }
        });

        //for set from session
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        //set radio button
        if (settingsViewModel.getLanguage() != null || settingsViewModel.getLanguage().isEmpty()) {
            setRadioButtonLanguage(settingsViewModel.getLanguage());
        }
        //set switch button
        setSwitchButton();


        //for set radiobutton intent data
        language = getIntent().getStringExtra(EXTRA_LANGUAGE);
        if (language != null && !language.isEmpty()) {
            setRadioButtonLanguage(language);
        }

        scDailyReminder.setOnCheckedChangeListener(this);
        scReleaseReminder.setOnCheckedChangeListener(this);

        //inisialisasi alarm
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //inisialisasiservice
        intentService = new Intent(this, ReleaseReminderService.class);
    }

    @Override
    public void onBackPressed() {
        finishChangeLanguage();
        super.onBackPressed();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sc_daily_reminder:
                if (isChecked) {
                    startDailyReminder();
                    saveStatusAlarm(Constant.PREF_STATUS_DAILY_REMINDER, true);
                } else {
                    stopAlarmDailyReminder();
                    saveStatusAlarm(Constant.PREF_STATUS_DAILY_REMINDER, false);
                }
                break;

            case R.id.sc_release_reminder:
                if (isChecked) {
                    startAlarmReleaseReminder();
                    saveStatusAlarm(Constant.PREF_STATUS_RELEASE_REMINDER, true);
                } else {
                    stopAlarmReleaseReminder();
                    saveStatusAlarm(Constant.PREF_STATUS_RELEASE_REMINDER, false);
                }
                break;
        }
    }

    /*
     *  Reminder
     *
     */

    //ini buat refresh data setiap jam 1 pagi jika ada baru diset alarm di releasereminderreceiver
    private void startAlarmReleaseReminder() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReleaseReminderReceiver.class);
        intent.putExtra(EXTRAS_API_KEY, BuildConfig.API_KEY);
        intent.putExtra(EXTRA_LANGUAGE, settingsViewModel.getLanguage());
        pendingIntentDailyReminder = PendingIntent.getBroadcast(this, REQUEST_CODE_RELEASE_REMINDER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentDailyReminder);
    }

    private void startServiceReleaseReminder() {
        intentService = new Intent(this, ReleaseReminderService.class);
        intentService.putExtra(EXTRAS_API_KEY, BuildConfig.API_KEY);
        intentService.putExtra(EXTRA_LANGUAGE, settingsViewModel.getLanguage());
        startService(intentService);
    }

    private void startWorkManagerMovies() {
        Data dataCity = new Data.Builder()
                .putString(EXTRAS_API_KEY, BuildConfig.API_KEY)
                .putString(EXTRAS_LANGUAGE, settingsViewModel.getLanguage())
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MoviesReleaseWorkManager.class, 15, TimeUnit.MINUTES)
                .setInputData(dataCity)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(MoviesReleaseWorkManager.TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    private void stopWorkManagerMovies() {
        WorkManager.getInstance().cancelAllWorkByTag(MoviesReleaseWorkManager.TAG);
    }

    private void stopServiceReleaseReminder() {
        stopService(intentService);
    }


    private void startDailyReminder() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra(Constant.INTENT_DATA_TITLE, "Daily Reminder");
        intent.putExtra(Constant.INTENT_DATA_MESSAGE, "Ayo lihat movies dan tvmovies");
        pendingIntentDailyReminder = PendingIntent.getBroadcast(this, REQUEST_CODE_DAILY_REMINDER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentDailyReminder);
    }


    private void stopAlarmReleaseReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_RELEASE_REMINDER, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);
    }

    private void stopAlarmDailyReminder() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_DAILY_REMINDER, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);
    }


    private void saveStatusAlarm(String name, boolean value) {
        SharedPref sharedPref = new SharedPref(this);
        sharedPref.modifyDataSharedPrefBoolean(name, value);
    }


    private void setSwitchButton() {
        if (settingsViewModel.getStatusDailyReminder()) {
            scDailyReminder.setChecked(true);
        } else {
            scDailyReminder.setChecked(false);
        }

        if (settingsViewModel.getStatusReleaseReminder()) {
            scReleaseReminder.setChecked(true);
        } else {
            scReleaseReminder.setChecked(false);
        }
    }

    /*
     *  Language
     *
     */

    private String getRadioChangeLanguage() {
        int checkedRadioButtonId = rgChangeLanguage.getCheckedRadioButtonId();
        language = null;
        if (checkedRadioButtonId != -1) {
            switch (checkedRadioButtonId) {
                case R.id.rb_indonesia:
                    language = "id";
                    break;
                case R.id.rb_english:
                    language = "en-US";
                    break;
            }
        }
        return language;
    }

    private void finishChangeLanguage() {
        saveLanguage(getRadioChangeLanguage());
        setLanguage();
    }

    private void setRadioButtonLanguage(String language) {
        if (language != null) {
            switch (language) {
                case "id":
                    rbIndonesia.setChecked(true);
                    break;
                case "en-US":
                    rbEnglish.setChecked(true);
                    break;
            }
        }
    }

    private void setLanguage() {
        SharedPref sharedPref = new SharedPref(this);
        Locale locale = new Locale(sharedPref.getSharedPref().getString(Constant.PREF_LANGUAGE, ""));
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void saveLanguage(String language) {
        SharedPref sharedPref = new SharedPref(this);
        sharedPref.modifyDataSharedPref(Constant.PREF_LANGUAGE, language);
    }

}
