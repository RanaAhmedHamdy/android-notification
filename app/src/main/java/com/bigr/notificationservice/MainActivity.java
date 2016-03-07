package com.bigr.notificationservice;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private EditText note;
    private Button set, cancel;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        note = (EditText) findViewById(R.id.editText);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        set = (Button) findViewById(R.id.setnotification);
        cancel = (Button) findViewById(R.id.cancelnotification);

        myDatabase = new MyDatabase(getBaseContext());

        final Intent intent = new Intent();

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase sq = myDatabase.getWritableDatabase();

                String message = note.getText().toString();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                intent.setClass(getBaseContext(), MyService.class);
                intent.putExtra("message", message);

                int id = (int) System.currentTimeMillis();
                ContentValues cv = new ContentValues();
                cv.put(myDatabase.TABLE_ID, id);
                sq.insert(myDatabase.TABLE_NAME, myDatabase.TABLE_ID, cv);
                cv.clear();
                sq.close();
                myDatabase.close();

                PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(),id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                //startService(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAllIDs();
            }
        });
    }

    private void cancelAllIDs() {
        myDatabase = new MyDatabase(getBaseContext());
        SQLiteDatabase sq = myDatabase.getReadableDatabase();

        Cursor cursor = sq.query(myDatabase.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Integer> id = new ArrayList<>();

        while(cursor != null && cursor.moveToNext()) {
            id.add(cursor.getInt(0));
        }
        cursor.close();
        sq.close();

        for(int i = 0; i < id.size(); i++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getBaseContext(), MyService.class);
            PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(),id.get(i),intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

        sq = myDatabase.getWritableDatabase();
        sq.delete(myDatabase.TABLE_NAME, null, null);
        sq.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent();
        intent.setClass(getBaseContext(), MyService.class);
        stopService(intent);
    }
}
