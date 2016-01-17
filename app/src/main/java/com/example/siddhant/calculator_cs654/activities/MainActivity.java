package com.example.siddhant.calculator_cs654.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siddhant.calculator_cs654.R;
import com.example.siddhant.calculator_cs654.webRequest.HttpGetRequest;
import com.example.siddhant.calculator_cs654.webRequest.Response;
import com.example.siddhant.calculator_cs654.helperClasses.TypefaceSpan;
import com.example.siddhant.calculator_cs654.databaseClasses.DatabaseClass;
import com.example.siddhant.calculator_cs654.databaseClasses.DatabaseHelper;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    TextView currentTextView, previousTextView, infoTextView;
    HorizontalScrollView ctvScrollBar, ptvScrollBar;
    ProgressBar progressBar;
    boolean isProcessingRequest = false;
    private Handler backSpacingHandler = new Handler();
    Typeface typeface;
    Vibrator vibrator;
    int selectedMode = 0;
    boolean flag = true, requestInvalidated = false;
    RelativeLayout moreOptionsRelativeLayout;
    String copiedText = "";
    DatabaseHelper myDbHelper;
    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        typeface = Typeface.createFromAsset(getAssets(), "proxima.otf");
        if (actionBar != null) {
            SpannableString s = new SpannableString("Calculator");
            s.setSpan(new TypefaceSpan(typeface), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(s);
        }
        currentTextView = (TextView) findViewById(R.id.currentTextView);
        currentTextView.setSelected(true);
        previousTextView = (TextView) findViewById(R.id.previousTextView);
        infoTextView = (TextView) findViewById(R.id.infoTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ctvScrollBar = (HorizontalScrollView) findViewById(R.id.ctvScrollBar);
        ptvScrollBar = (HorizontalScrollView) findViewById(R.id.ptvScrollBar);
        LinearLayout parent = (LinearLayout) findViewById(R.id.dockLinerLayout);
        currentTextView.setTypeface(typeface);
        previousTextView.setTypeface(typeface);
        infoTextView.setTypeface(typeface);
        moreOptionsRelativeLayout = (RelativeLayout) findViewById(R.id.moreOptionsRelativeLayout);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        myDbHelper = DatabaseHelper.getInstance(MainActivity.this);
        setGenericListeners(parent);
        setListeners();
    }


    TextView specialTextView1, specialTextView2, specialTextView3, specialTextView4, specialTextView5,
            specialTextView6, specialTextView7, specialTextView8, specialTextView9, specialTextView10;

    private void setListeners() {
        specialTextView1 = (TextView) findViewById(R.id.specialTextView1);
        specialTextView2 = (TextView) findViewById(R.id.specialTextView2);
        specialTextView3 = (TextView) findViewById(R.id.specialTextView3);
        specialTextView4 = (TextView) findViewById(R.id.specialTextView4);
        specialTextView5 = (TextView) findViewById(R.id.specialTextView5);
        specialTextView6 = (TextView) findViewById(R.id.specialTextView6);
        specialTextView7 = (TextView) findViewById(R.id.specialTextView7);
        specialTextView8 = (TextView) findViewById(R.id.specialTextView8);
        specialTextView9 = (TextView) findViewById(R.id.specialTextView9);
        specialTextView10 = (TextView) findViewById(R.id.specialTextView10);
        final RelativeLayout backSpaceRelativeLayout = (RelativeLayout) findViewById(R.id.backSpaceRelativeLayout);
        backSpaceRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = currentTextView.getText().toString();
                int len = str.length();
                if (len > 0)
                    currentTextView.setText(str.substring(0, len - 1).trim());
                vibrator.vibrate(50);
            }
        });
        backSpaceRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                flag = true;
                backSpacingHandler.post(new BackSpaceUpdater());
                return false;
            }
        });
        backSpaceRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && flag) {
                    flag = false;
                }
                return false;
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
                ctvScrollBar.postDelayed(new Runnable() {
                    public void run() {
                        ctvScrollBar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                }, 100L);
            }
        });
        previousTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ptvScrollBar.postDelayed(new Runnable() {
                    public void run() {
                        ptvScrollBar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                    }
                }, 100L);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        moreOptionsRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(50);
                switchMoreOptionTextViews();
                if (selectedMode == 0) {
                    selectedMode = 1;
                    moreOptionsRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    selectedMode = 0;
                    moreOptionsRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        RelativeLayout copyRelativeLayout = (RelativeLayout) findViewById(R.id.copyRelativeLayout);
        RelativeLayout pasteRelativeLayout = (RelativeLayout) findViewById(R.id.pasteRelativeLayout);
        RelativeLayout historyRelativeLayout = (RelativeLayout) findViewById(R.id.historyRelativeLayout);
        copyRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTextView.getText().toString().equals("")) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(MainActivity.this, "Nothing to copy", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    copiedText = currentTextView.getText().toString();
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT);
                    toast.show();
                    currentTextView.setBackgroundColor(getResources().getColor(R.color.colorVeryLight));
                    currentTextView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentTextView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        }
                    }, 100);
                }
                vibrator.vibrate(50);
            }
        });
        pasteRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!copiedText.isEmpty()) {
                    currentTextView.append(copiedText);
                } else {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(MainActivity.this, "Noting to paste", Toast.LENGTH_SHORT);
                    toast.show();
                }
                vibrator.vibrate(50);
            }
        });
        historyRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(i, 2);
                vibrator.vibrate(50);
            }
        });
    }


    private void switchMoreOptionTextViews() {

        if (specialTextView1.getText().toString().equals("sqrt")) {
            specialTextView1.setText("\u00b1");
        } else {
            specialTextView1.setText("sqrt");
        }
        if (specialTextView2.getText().toString().equals("log")) {
            specialTextView2.setText("1/x");
        } else {
            specialTextView2.setText("log");
        }
        if (specialTextView3.getText().toString().equals("sin")) {
            specialTextView3.setText("sinh");
        } else {
            specialTextView3.setText("sin");
        }
        if (specialTextView4.getText().toString().equals("cos")) {
            specialTextView4.setText("cosh");
        } else {
            specialTextView4.setText("cos");
        }
        if (specialTextView5.getText().toString().equals("tan")) {
            specialTextView5.setText("tanh");
        } else {
            specialTextView5.setText("tan");
        }
        if (specialTextView6.getText().toString().equals("x^2")) {
            specialTextView6.setText("x^3");
        } else {
            specialTextView6.setText("x^2");
        }
        if (specialTextView7.getText().toString().equals("10^x")) {
            specialTextView7.setText("exp");
        } else {
            specialTextView7.setText("10^x");
        }
        if (specialTextView8.getText().toString().equals("asin")) {
            specialTextView8.setText("asinh");
        } else {
            specialTextView8.setText("asin");
        }
        if (specialTextView9.getText().toString().equals("acos")) {
            specialTextView9.setText("acosh");
        } else {
            specialTextView9.setText("acos");
        }
        if (specialTextView10.getText().toString().equals("atan")) {
            specialTextView10.setText("atanh");
        } else {
            specialTextView10.setText("atan");
        }
        Animation an1 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an1.setStartOffset(125);
        Animation an2 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an2.setStartOffset(150);
        Animation an3 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an3.setStartOffset(175);
        Animation an4 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an4.setStartOffset(200);
        Animation an5 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an5.setStartOffset(225);
        Animation an6 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an6.setStartOffset(0);
        Animation an7 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an7.setStartOffset(25);
        Animation an8 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an8.setStartOffset(50);
        Animation an9 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an9.setStartOffset(75);
        Animation an10 = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        an10.setStartOffset(100);
        specialTextView1.startAnimation(an1);
        specialTextView2.startAnimation(an2);
        specialTextView3.startAnimation(an3);
        specialTextView4.startAnimation(an4);
        specialTextView5.startAnimation(an5);
        specialTextView6.startAnimation(an6);
        specialTextView7.startAnimation(an7);
        specialTextView8.startAnimation(an8);
        specialTextView9.startAnimation(an9);
        specialTextView10.startAnimation(an10);
    }

    class BackSpaceUpdater implements Runnable {
        public void run() {
            if (flag) {
                processBackSpace();
                backSpacingHandler.postDelayed(new BackSpaceUpdater(), 100);
            }
        }
    }

    private void processBackSpace() {
        String str = currentTextView.getText().toString();
        int len = str.length();
        if (len > 0)
            currentTextView.setText(str.substring(0, len - 1).trim());
        vibrator.vibrate(50);
    }

    private void setGenericListeners(ViewGroup parent) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                setGenericListeners((ViewGroup) child);
            } else {
                if (child != null && child instanceof TextView) {
                    child.setOnClickListener(listener);
                    ((TextView) child).setTypeface(typeface);
                }
            }
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isProcessingRequest)
                return;
            vibrator.vibrate(50);
            TextView tv = (TextView) v;
            String str = tv.getText().toString();
            String str1 = currentTextView.getText().toString();
            switch (str) {
                case "C":
                    currentTextView.setText("");
                    previousTextView.setText("");
                    break;
                case "CE":
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
                    if (str1.length() != 0) {
                        computeResult(str1);
                    } else {
                        infoTextView.setText("Please enter some input!");
                        infoTextView.setTextColor(Color.parseColor("#F44336"));
                    }
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
                case "tanh":
                case "cosh":
                case "sinh":
                case "atanh":
                case "acosh":
                case "asinh":
                case "log":
                case "sqrt":
                case "exp":
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
                case "x^2":
                    currentTextView.setText(str1.trim() + " ^ 2");
                    break;
                case "x^3":
                    currentTextView.setText(str1.trim() + " ^ 3");
                    break;
                case "10^x":
                    currentTextView.setText(str1.trim() + " ( 10 ^");
                    break;
                case "1/x":
                    currentTextView.setText(str1.trim() + " ( 1 /");
                    break;
                case "x^y":
                    currentTextView.setText(str1.trim() + " ^");
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
                final Response response = HttpGetRequest.makeHttpGetRequest(expression);
                if (response != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!requestInvalidated) {
                                if (response.success) {
                                    previousTextView.setText(expression);
                                    if (!response.result.equals("NaN") && !response.result.equals("Infinity")) {
                                        Double result = Double.parseDouble(response.result);
                                        DecimalFormat format = new DecimalFormat("0.########");
                                        response.result = format.format(result);
                                    }
                                    currentTextView.setText(response.result);
                                    myDbHelper.addPair(new DatabaseClass(expression, response.result));
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
                            } else {
                                requestInvalidated = false;
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!requestInvalidated) {
                                progressBar.setVisibility(View.GONE);
                                infoTextView.setText("Can't connect!");
                                infoTextView.setTextColor(Color.parseColor("#F44336"));
                                isProcessingRequest = false;
                            } else {
                                requestInvalidated = false;
                            }
                        }
                    });
                }
            }
        }).start();
    }

    Handler preventingGettingStuckHandler = new Handler();

    class preventingGettingStuck implements Runnable {

        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
            infoTextView.setText("Can't connect");
            infoTextView.setTextColor(Color.parseColor("#F44336"));
            isProcessingRequest = false;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentText", currentTextView.getText().toString());
        outState.putString("previousText", previousTextView.getText().toString());
        outState.putString("infoText", infoTextView.getText().toString());
        outState.putString("copiedText", copiedText);
        outState.putBoolean("isProcessingRequest", isProcessingRequest);
        outState.putBoolean("flag", flag);
        outState.putInt("selectedMode", selectedMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentTextView.setText(savedInstanceState.getString("currentText"));
        previousTextView.setText(savedInstanceState.getString("previousText"));
        infoTextView.setText(savedInstanceState.getString("infoText"));
        isProcessingRequest = savedInstanceState.getBoolean("isProcessingRequest");
        copiedText = savedInstanceState.getString("copiedText");
        if (isProcessingRequest) {
            progressBar.setVisibility(View.VISIBLE);
            preventingGettingStuckHandler.postDelayed(new preventingGettingStuck(), 2000);
        }
        flag = savedInstanceState.getBoolean("flag");
        selectedMode = savedInstanceState.getInt("selectedMode");
        if (selectedMode == 1) {
            switchMoreOptionTextViews();
            moreOptionsRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String query = data.getStringExtra("previous");
            String response = data.getStringExtra("current");
            previousTextView.setText(query);
            currentTextView.setText(response);
            infoTextView.setText("");
            progressBar.setVisibility(View.GONE);
            if (isProcessingRequest) {
                requestInvalidated = true;
            }
            isProcessingRequest = false;
        }
    }
}
