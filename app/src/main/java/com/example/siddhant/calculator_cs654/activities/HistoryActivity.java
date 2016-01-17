package com.example.siddhant.calculator_cs654.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.siddhant.calculator_cs654.adapters.HistoryAdapter;
import com.example.siddhant.calculator_cs654.R;
import com.example.siddhant.calculator_cs654.helperClasses.TypefaceSpan;
import com.example.siddhant.calculator_cs654.databaseClasses.DatabaseClass;
import com.example.siddhant.calculator_cs654.databaseClasses.DatabaseHelper;

public class HistoryActivity extends AppCompatActivity {

    DatabaseHelper myDbHelper;
    ListView historyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Typeface typeface = Typeface.createFromAsset(getAssets(), "proxima.otf");
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            SpannableString s = new SpannableString("History");
            s.setSpan(new TypefaceSpan(typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(s);
        }
        historyListView = (ListView) findViewById(R.id.historyListView);
        myDbHelper = DatabaseHelper.getInstance(this);
        HistoryAdapter myAdapter = new HistoryAdapter(this, myDbHelper.getAllHistory());
        historyListView.setAdapter(myAdapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(HistoryActivity.this);
                dlgAlert.setMessage("Feed to the calculator?");
                dlgAlert.setTitle("Confirm..");
                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseClass singleHistory = (DatabaseClass) parent.getAdapter().getItem(position);
                        Intent intent = new Intent();
                        intent.putExtra("current", singleHistory.response);
                        intent.putExtra("previous", singleHistory.query);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlgAlert.setCancelable(false);
                dlgAlert.create().show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_clear:
                clearAllHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAllHistory() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Clear the history?");
        dlgAlert.setTitle("Confirm..");
        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                myDbHelper.deleteHistory();
                historyListView.setAdapter(null);
                Toast.makeText(HistoryActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
            }
        });
        dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }
}
