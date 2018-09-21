package com.manav.smsgrouping;

import android.Manifest;
import android.app.LauncherActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.BoolRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvList;
    Boolean permission = false;
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 99;
    public static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 199;

    private int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 15;
    LinearLayoutManager linearLayoutManager;
    private int pageNo;
    private List<ListItem> list;
    HashMap<Long, List<ListItem>> groupedHashMap;
    SortedSet<Long> keys;
    String notif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvList = (RecyclerView) findViewById(R.id.rvList);
        notif= getIntent().getStringExtra("Notif");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = checkSmsPermission();
        //    checkSmsReceivePermission();
            if (permission) {
                list = new ArrayList();
                ListItem listItem;
                Uri uriSms = Uri.parse("content://sms/inbox");
                final Cursor cursor = getContentResolver().query(uriSms,
                        new String[]{"_id", "address", "date", "body"}, null, null, "date DESC");

                while (cursor.moveToNext()) {
                    String address = cursor.getString(1);
                    String msg = cursor.getString(3);
                    String date = cursor.getString(2);
                    listItem = new ListItem();
                    listItem.setNumber(address);
                    listItem.setMessage(msg);
                    listItem.setDate(date);
                    list.add(listItem);
                }
                ListAdapter listAdapter = new ListAdapter(getConsolidatedList(list),
                        getBaseContext(),notif);
                linearLayoutManager = new LinearLayoutManager(getBaseContext());
                rvList.setLayoutManager(linearLayoutManager);
                rvList.setAdapter(listAdapter);
            }
        }

        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

                if (dy > 0) {
                    if (totalItemCount > previousTotal) {
                        previousTotal = totalItemCount;
                    }
                    if ((totalItemCount - visibleItemCount) <= (pastVisibleItem + viewThreshold)) {
                        pageNo++;
                        // mainInterfaceAction.callNextPages(pageNo, globalText);
                    }
                }
            }
        });
    }

    public boolean checkSmsPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_SMS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        list = new ArrayList();
                        ListItem listItem;
                        Uri uriSms = Uri.parse("content://sms/inbox");
                        final Cursor cursor = getContentResolver().query(uriSms,
                                new String[]{"_id", "address", "date", "body"}, null, null, "date DESC");

                        while (cursor.moveToNext()) {
                            String address = cursor.getString(1);
                            String msg = cursor.getString(3);
                            String date = cursor.getString(2);
                            listItem = new ListItem();
                            listItem.setNumber(address);
                            listItem.setMessage(msg);
                            listItem.setDate(date);
                            list.add(listItem);
                        }
                        ListAdapter listAdapter = new ListAdapter(getConsolidatedList(list),
                                getBaseContext(),notif);
                        linearLayoutManager = new LinearLayoutManager(getBaseContext());
                        rvList.setLayoutManager(linearLayoutManager);
                        rvList.setAdapter(listAdapter);
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECEIVE_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    public SortedSet<Long> mapGroupData(List<ListItem> listItems) {
       groupedHashMap = new HashMap<>();

        for (ListItem item : listItems) {
            Long ts = System.currentTimeMillis();
            Long hashMapKey = (ts - Long.parseLong(item.getDate())) / (1000 * 60 * 60);
            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(item);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<ListItem> list = new ArrayList<>();
                list.add(item);
                groupedHashMap.put(hashMapKey, list);
            }
        }
         keys = new TreeSet<Long>(groupedHashMap.keySet());

        return keys;
    }

    public List<Object> getConsolidatedList(List<ListItem> listItems) {
        List<Object> consolidatedList = new ArrayList<>();

        for (Long date : mapGroupData(listItems)) {
            DateItem dateItem = new DateItem();
            Long temp = System.currentTimeMillis() - (date* 1000 * 60 * 60);
            if (date < 24) {
                if(date.equals("0")){
                    dateItem.setDate("just now");
                }else {
                    dateItem.setDate(date + " hours ago");
                }
            } else {
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(temp);
                String date1 = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
                dateItem.setDate(date1);
            }
            consolidatedList.add(dateItem);

            for (ListItem pojoOfJsonArray : groupedHashMap.get(date)) {
                GeneralItem generalItem = new GeneralItem();
                generalItem.setPojoOfJsonArray(pojoOfJsonArray);
                consolidatedList.add(generalItem);
            }
        }
        return consolidatedList;
    }
}