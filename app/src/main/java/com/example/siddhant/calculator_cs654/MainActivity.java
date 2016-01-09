package com.example.siddhant.calculator_cs654;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView currentTextView, previousTextView, infoTextView;
    ProgressBar progressBar;
    boolean isProcessingRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentTextView = (TextView) findViewById(R.id.currentTextView);
        currentTextView.setSelected(true);
        previousTextView = (TextView) findViewById(R.id.previousTextView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        LinearLayout parent = (LinearLayout) findViewById(R.id.dockLinerLayout);
        setGenericListeners(parent);
        setListners();
    }

    private void setListners() {
        RelativeLayout backSpaceRelativeLayout = (RelativeLayout) findViewById(R.id.backSpaceRelativeLayout);
        backSpaceRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = currentTextView.getText().toString();
                int len = str.length();
                if (len > 0)
                    currentTextView.setText(str.substring(0, len - 1).trim());
            }
        });
        currentTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                infoTextView.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setGenericListeners(ViewGroup parent) {
//        TextView specialTextView1 = (TextView) findViewById(R.id.specialTextView1);
//        TextView specialTextView2 = (TextView) findViewById(R.id.specialTextView2);
//        TextView specialTextView3 = (TextView) findViewById(R.id.specialTextView3);
//        TextView specialTextView4 = (TextView) findViewById(R.id.specialTextView4);
//        TextView specialTextView5 = (TextView) findViewById(R.id.specialTextView5);
//        TextView CETextView = (TextView) findViewById(R.id.CETextView);
//        TextView CTextView = (TextView) findViewById(R.id.CETextView);
//        TextView DivideTextView = (TextView) findViewById(R.id.CETextView);
//        TextView PiTextView = (TextView) findViewById(R.id.CETextView);
//        TextView SevenTextView = (TextView) findViewById(R.id.CETextView);
//        TextView EightTextView = (TextView) findViewById(R.id.CETextView);
//        TextView NineTextView = (TextView) findViewById(R.id.CETextView);
//        TextView MultiplyTextView = (TextView) findViewById(R.id.CETextView);
//        TextView NegateTextView = (TextView) findViewById(R.id.CETextView);
//        TextView FourTextView = (TextView) findViewById(R.id.CETextView);
//        TextView FiveTextView = (TextView) findViewById(R.id.CETextView);
//        TextView SixTextView = (TextView) findViewById(R.id.CETextView);
//        TextView MinusTextView = (TextView) findViewById(R.id.CETextView);
//        TextView AbsTextView = (TextView) findViewById(R.id.CETextView);
//        TextView OneTextView = (TextView) findViewById(R.id.CETextView);
//        TextView TwoTextView = (TextView) findViewById(R.id.CETextView);
//        TextView ThreeTextView = (TextView) findViewById(R.id.CETextView);
//        TextView AddTextView = (TextView) findViewById(R.id.CETextView);
//        TextView LeftBracketTextView = (TextView) findViewById(R.id.CETextView);
//        TextView RightBracketTextView = (TextView) findViewById(R.id.CETextView);
//        TextView ZeroBracketTextView = (TextView) findViewById(R.id.CETextView);

        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                setGenericListeners((ViewGroup) child);
            } else {
                if (child != null && child instanceof TextView) {
                    child.setOnClickListener(listener);
                }
            }
        }

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isProcessingRequest)
                return;
            TextView tv = (TextView) v;
            String str = tv.getText().toString();
            String str1 = currentTextView.getText().toString();
            switch (str) {
                case "CE":
                    currentTextView.setText("");
                    previousTextView.setText("");
                    break;
                case "C":
                    if (previousTextView.getText().length() != 0) {
                        currentTextView.setText(previousTextView.getText());
                        previousTextView.setText("");
                    } else {
                        currentTextView.setText("");
                    }
                    break;
                case "\u00b1":
                    currentTextView.setText(str1.trim() + " ( -");
                    break;
                case "abs":
                    currentTextView.setText(str1.trim() + " abs (");
                    break;
                case "=":
                    computeResult(str1);
                    break;
                case "\u03c0":
                    currentTextView.setText(str1.trim() + " pi");
                    break;
                case "tan":
                case "cos":
                case "sin":
                case "atan":
                case "acos":
                case "asin":
                case "log":
                case "sqrt":
                    currentTextView.setText(str1.trim() + " " + str + " (");
                    break;
                case "+":
                case "-":
                case "/":
                case "*":
                    currentTextView.setText(str1.trim() + " " + str);
                    break;
                case "(":
                    currentTextView.setText(str1.trim() + " (");
                    break;
                case ")":
                    currentTextView.setText(str1.trim() + " )");
                    break;
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                case "0":
                case ".":
                    String str2 = str1.trim();
                    boolean flag = false;
                    char lastChar = '1';
                    if (str2.length() == 0) {
                        flag = true;
                    } else {
                        lastChar = str2.charAt(str2.length() - 1);
                    }
                    if (flag || Character.isDigit(lastChar) || lastChar == '.') {
                        currentTextView.append(str);
                    } else {
                        currentTextView.setText(str2 + " " + str);
                    }
                    break;
                default:
                    currentTextView.setText(str1.trim() + " " + str);
                    break;

            }
        }
    };

    private void computeResult(final String expression) {
        isProcessingRequest = true;
        progressBar.setVisibility(View.VISIBLE);
        infoTextView.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Response response = makeHttpGetRequest(expression);
                if (response != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.success) {
                                previousTextView.setText(expression);
                                currentTextView.setText(response.result);
                                progressBar.setVisibility(View.GONE);
                                if (response.cacheHit) {
                                    infoTextView.setText("Cache hit!");
                                    infoTextView.setTextColor(Color.parseColor("#009688"));
                                } else {
                                    infoTextView.setText("Cache miss!");
                                    infoTextView.setTextColor(Color.parseColor("#FF5722"));
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                infoTextView.setText(response.error);
                                infoTextView.setTextColor(Color.parseColor("#F44336"));
                            }
                            isProcessingRequest = false;
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            infoTextView.setText("Can't connect!");
                            infoTextView.setTextColor(Color.parseColor("#F44336"));
                            isProcessingRequest = false;
                        }
                    });
                }
            }
        }).start();
    }

    private Response makeHttpGetRequest(String expression) {
        HttpURLConnection conn = null;
        try {
            String parameters = URLEncoder.encode(expression.replaceAll("\\s+", ""), "UTF-8");
            URL url = new URL("http://107.167.178.155:8000/eval/" + parameters);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("platform", "android");
            conn.setRequestProperty("version", BuildConfig.VERSION_NAME);
            String line;
            InputStreamReader isr = new InputStreamReader(
                    conn.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String response = sb.toString();
            Response myResponse = new Gson().fromJson(response, Response.class);
            isr.close();
            reader.close();
            return myResponse;
        } catch (Exception ex) {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
