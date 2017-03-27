package com.mygt.handshank.sample;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

public class UITestActivity extends AppCompatActivity implements IUITestView, View.OnClickListener, GameModel.OnChangeModelListener {

    private static final int UPDATA_COUNT = 1;
    private final String TAG = getClass().getSimpleName();

    private double starttime = 0.0;      // 起始时间
    private double endtime = 0.0;         //结束时间
    private double recordtime = 0.0;        // 记录时间
    private double intervaltime = 0.0;      //  间隔时间
    private double finaltime = 1000;      //  最后的时间
    private int count = 1;
    private int show = 0;
    public boolean flag = true;

    private int mModelType;

    View l1, l2, r1, r2, up, left, right, down, x, y, a, b, back, start, i, home;

    View[] byte3Views, byte4Views;

    View normalInfoX, normalInfoY;

    RockerView leftR, rightR;

    NumberProgressBar l2P, r2P;

    GameModel mGameModel;

    TextView infoKey1, infoKey2, infoX1, infoY1, infoX2, infoY2, infokeyinterval, infokeycount;

    int l_radius;
    int r_radius;
    Point l_centerP, r_centerP;

    int l_offsetX, l_offsetY, r_offsetX, r_offsetY;

    boolean initRocker = false;
    private int stoptime = 18;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_test);

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        up = findViewById(R.id.up);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        down = findViewById(R.id.down);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        back = findViewById(R.id.back);
        start = findViewById(R.id.start);
        i = findViewById(R.id.i);
        home = findViewById(R.id.home);

        leftR = (RockerView) findViewById(R.id.l_r);
        rightR = (RockerView) findViewById(R.id.r_r);

        l2P = (NumberProgressBar) findViewById(R.id.l2_p);
        r2P = (NumberProgressBar) findViewById(R.id.r2_p);

        infoKey2 = (TextView) findViewById(R.id.info_key2);
        infoKey1 = (TextView) findViewById(R.id.info_key1);
        infokeyinterval = (TextView) findViewById(R.id.info_key_interval);
        infokeycount = (TextView) findViewById(R.id.info_key_count);
        infoX1 = (TextView) findViewById(R.id.info_x1);
        infoY1 = (TextView) findViewById(R.id.info_y1);
        infoX2 = (TextView) findViewById(R.id.info_x2);
        infoY2 = (TextView) findViewById(R.id.info_y2);

        initViews();

        findViewById(R.id.test).setOnClickListener(this);
        findViewById(R.id.change_model).setOnClickListener(this);

        normalInfoX = findViewById(R.id.normal_info_x);
        normalInfoY = findViewById(R.id.normal_info_y);

        mGameModel = new GameModel(this, this);

        mGameModel.setOnChangeModelListener(this);

        getWindow().findViewById(Window.ID_ANDROID_CONTENT).setBackgroundResource(R.mipmap.background);

        Log.d(TAG, "thread:" + Thread.currentThread().getName() + ",id:" + Thread.currentThread().getId());

    }

    private void initViews() {
        byte3Views = new View[8];
        byte4Views = new View[8];

        byte3Views[0] = up;
        byte3Views[1] = down;
        byte3Views[2] = left;
        byte3Views[3] = right;
        byte3Views[4] = start;
        byte3Views[5] = back;
        byte3Views[6] = leftR;
        byte3Views[7] = rightR;

        byte4Views[0] = i;
        byte4Views[2] = a;
        byte4Views[3] = b;
        byte4Views[4] = x;
        byte4Views[5] = y;
        byte4Views[6] = l1;
        byte4Views[7] = r1;
    }

    long genericTime;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent");
        int code = event.getKeyCode();


//        KeyEvent.KEYCODE_BUTTON_X;
        Log.d(TAG, "code:" + code);

        int action = event.getAction();
        if (action == KeyEvent.ACTION_DOWN) {
            keyDown(code, event);
        } else if (action == KeyEvent.ACTION_UP) {
            keyUp(code, event);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameModel.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameModel.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameModel.onPause();
    }

    public void keyDown(int keyCode, KeyEvent event) {
        infoKey2.setText("keyCode:" + keyCode);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_L1:
                l1.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_L2:
                l2.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                r1.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                r2.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                y.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                x.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                a.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                b.setSelected(true);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                up.setSelected(true);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                left.setSelected(true);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                right.setSelected(true);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                down.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BACK:
//                back.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_START:
                start.setSelected(true);
                break;
            case KeyEvent.KEYCODE_I:
                i.setSelected(true);
                break;
            case KeyEvent.KEYCODE_HOME:
                home.setSelected(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                leftR.setClick(true);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                rightR.setClick(true);
                break;
        }
    }

    public void keyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_L1:
                l1.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_L2:
                l2.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                r1.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                r2.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                y.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                x.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                a.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                b.setSelected(false);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                up.setSelected(false);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                left.setSelected(false);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                right.setSelected(false);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                down.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BACK:
//                back.setSelected(false);
                finish();
                break;
            case KeyEvent.KEYCODE_BUTTON_START:
                start.setSelected(false);
                break;
            case KeyEvent.KEYCODE_I:
                i.setSelected(false);
                break;
            case KeyEvent.KEYCODE_HOME:
                home.setSelected(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                leftR.setClick(false);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                rightR.setClick(false);
                break;
        }
    }

    long max = 0;

    private void initRocker() {
        if (!initRocker) {
            l_radius = leftR.getAreaRadius();
            r_radius = rightR.getAreaRadius();
            l_centerP = leftR.getCenterPoint();
            r_centerP = rightR.getCenterPoint();

            l_offsetX = l_centerP.x - l_radius;
            l_offsetY = l_centerP.y - l_radius;
            r_offsetX = r_centerP.x - r_radius;
            r_offsetY = r_centerP.y - r_radius;
            initRocker = true;
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "onGenericMotionEvent");
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                genericTime = System.nanoTime();
//                genericTime = SystemClock.currentThreadTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
//                long time2 = SystemClock.currentThreadTimeMillis();
                long time2 = System.nanoTime();
                long interval = time2 - genericTime;
                genericTime = time2;
                if (interval < 1000000000 && max < interval) {
                    max = interval;
                    Log.d(TAG, "max1:" + max);
                }
                Log.d(TAG, "interval:" + interval);
                break;
        }
        initRocker();
        float l2 = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        float r2 = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        Log.d(TAG, "RIGGER l2:" + l2 + ",r2:" + r2);
        float a_x = event.getAxisValue(MotionEvent.AXIS_X);
        float a_y = event.getAxisValue(MotionEvent.AXIS_Y);
        Log.d(TAG, "lift a_x:" + a_x + ",a_y:" + a_y);
        infoX1.setText("x1:" + a_x);
        infoY1.setText("y1:" + a_y);
//        Log.d(TAG, "l_radius:"+)
        float l_x = (a_x) * l_radius + l_centerP.x;
        float l_y = (a_y) * l_radius + l_centerP.y;
        leftR.moveRockerWithKey(l_x, l_y);

//        右摇杆用
        float a_z = event.getAxisValue(MotionEvent.AXIS_Z);
        float a_rz = event.getAxisValue(MotionEvent.AXIS_RZ);
        infoX2.setText("x2:" + a_z);
        infoY2.setText("y2:" + a_rz);
        Log.d(TAG, "right a_z:" + a_z + ",a_rz:" + a_rz);
        float r_x = (a_z) * r_radius + r_centerP.x;
        float r_y = (a_rz) * r_radius + r_centerP.y;
        rightR.moveRockerWithKey(r_x, r_y);

        float x = event.getX();
        float y = event.getY();
        Log.d(TAG, "getx:" + x + ",y:" + y);
        int index = event.getActionIndex();
        Log.d(TAG, "index:" + index);

        return super.onGenericMotionEvent(event);
    }

    @Override
    public void setDataKey(int position, byte keycode) {
        initRocker();
        switch (position) {
            case Constants.KEYCODE_TYPE_BYTE_3:
                decodeKey3Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_4:
                decodeKey4Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_5:
                decodeKey5Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_6:
                decodeKey6Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_7:
                decodeKey7Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_9:
                decodeKey9Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_11:
                decodeKey11Byte(keycode);
                break;
            case Constants.KEYCODE_TYPE_BYTE_13:
                decodeKey13Byte(keycode);
                break;
        }
    }

    @Override
    public void setDataInfo(boolean shortdata, String shortdatas, final String data) {
        flag = true;
        if (shortdata) {
            infoKey1.setText(shortdatas);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    infoKey1.setText("");
                }
            });
        }
        if (System.currentTimeMillis() <= recordtime) {
            starttime = System.currentTimeMillis();  // 接收数据前开始计时   ms
            ++count;
        } else {
            show = count;
            count = 0;
            recordtime = System.currentTimeMillis() + 1000;
//            Log.d("bibi", "接收数据结尾时间: "+recordtime);
            Log.d("haha", "show==: " + show);
        }
        endtime = System.currentTimeMillis();
        infoKey2.setText(data);
        infokeycount.setText("个数：" + show + "个");
        endtime = System.currentTimeMillis();
        intervaltime = endtime - starttime + stoptime;
        Log.d("haha", "时间间隔为==: " + intervaltime);
        if (intervaltime < finaltime) {
            infokeyinterval.setText("间隔时间:" + intervaltime + "ms");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    if (System.currentTimeMillis() - endtime > 1000) {
                        flag = false;
                        handle.obtainMessage(UPDATA_COUNT).sendToTarget();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //            @Override
//    public void setDataInfo(boolean shortdata ,String shortdatas, String data) {
//                        flag = true;
//                        if(shortdata){
//                            infoKey1.setText(shortdatas);
//                        }else{
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        Thread.sleep(500);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Log.e("睡眠了3000ms，，","发送消息。。。。。。。");
//                                    Message message = new Message();
//                                    message.what = 1;
//                                    handler.sendMessage(message);
//                                }
//                            }).start();
//                        }
//                        Log.e("测试c",""+System.currentTimeMillis());
//                        if(System.currentTimeMillis()<=recordtime){
//                            count++;
//                        }else{
//                            show = count;
//                            count=0;
//                            recordtime = System.currentTimeMillis()+1000;
//                        }
//                        infoKey2.setText(data);
//                        infokeycount.setText("1s总个数"+show+"个");
//                        intervaltime =  System.currentTimeMillis()-starttime;
//                        starttime = System.currentTimeMillis();
//                        if(intervaltime<finaltime){
//                            finaltime = intervaltime;
//                        }
//                        infokeyinterval.setText("最小间隔时间:"+finaltime+"ms");
//                        Log.e("测试间隔时间。",finaltime+"ms");
//
//                        endtime = System.currentTimeMillis();
//                        Log.i("测试d",""+endtime);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.i("测试f",""+endtime);
//                                while(flag){
//                                    try {
//                                        Thread.sleep(800);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Log.i("测试。。",System.currentTimeMillis()+"..."+endtime);
//                                    if(System.currentTimeMillis()-endtime>=1000){
//                                        flag = false;
//                                        Message message = new Message();
//                                        message.what = 2;
//                        handler.sendMessage(message);
//                    }
//                }
//
//            }
//        }).start();
//        Log.i("测试e",""+endtime);
//    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATA_COUNT) {
                Log.d(TAG, "收到消息===: ");
                infokeycount.setText("个数：" + 0 + "个");
            }
        }
    };

    boolean isGameModel = false;

    @Override
    public void setChangeModel(byte data) {

    }

    float r_x;

    private void decodeKey11Byte(byte keycode) {
        r_x = getRockerLocation(keycode, r_radius) + r_offsetX;
    }

    private void decodeKey13Byte(byte keycode) {
        float y = getRockerLocation(keycode, r_radius) + r_offsetY;
        rightR.moveRockerWithKey(r_x, y);
    }

    float l_x;

    private void decodeKey7Byte(byte keycode) {
        int progress = Constants.BYTE_MAX & keycode;
        Log.d(TAG, "progress_x:" + progress);
        l_x = getRockerLocation(keycode, l_radius) + l_offsetX;
        Log.d(TAG, "l_x:" + l_x + ",radius:" + l_radius);
    }

    private void decodeKey9Byte(byte keycode) {
        int progress = Constants.BYTE_MAX & keycode;
        Log.d(TAG, "progress_y:" + progress);
        float y = getRockerLocation(keycode, l_radius) + l_offsetY;
        Log.d(TAG, "y:" + y + ",radius:" + l_radius);
        leftR.moveRockerWithKey(l_x, y);
    }

    private float getRockerLocation(byte keycode, int radius) {
        int progress = Constants.BYTE_MAX & keycode;
        float _p = 1.0f * progress / Constants.BYTE_MAX;
        float location = _p * radius * 2;
        return location;
    }

    private void decodeKey5Byte(byte keycode) {
        int progress = Constants.BYTE_MAX & keycode;
        l2.setSelected(!(progress == 0));
        l2P.setProgress(progress);
        if (!(progress == 0) && isTest) {
            l2.setVisibility(View.INVISIBLE);
        }
    }

    private void decodeKey6Byte(byte keycode) {
        int progress = Constants.BYTE_MAX & keycode;
        r2.setSelected(!(progress == 0));
        r2P.setProgress(progress);
        if (!(progress == 0) && isTest) {
            r2.setVisibility(View.INVISIBLE);
        }
    }

    private void decodeKey4Byte(byte keycode) {
        setSelectView(byte4Views, keycode);
    }

    private void decodeKey3Byte(byte keycode) {
        setSelectView(byte3Views, keycode);
    }

    private void setSelectView(View[] views, byte keycode) {
        int mark;
        for (int i = 0; i < 8; i++) {
            mark = keycode & 1 << i;
            if (views[i] == null) {
                continue;
            }
            if (mark == 0) {
                views[i].setSelected(false);
                continue;
            }

            views[i].setSelected(true);
            if (isTest) {
                views[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test:
//                changeTest();
                startTest = true;
                if (mModelType != Constants.GAME_MODEL) {
                    mGameModel.dispatchEvent(Constants.GAME_MODEL);
                } else {
                    changeTest();
                }
                break;
            case R.id.change_model:
                if (isTest) {
                    changeTest();
                    break;
                }
                if (mModelType != Constants.NORMAL_MODEL) {
                    mGameModel.dispatchEvent(Constants.NORMAL_MODEL);
                } else {
                    mGameModel.dispatchEvent(Constants.GAME_MODEL);
                }
                break;
        }
    }

    boolean isTest = false;
    boolean startTest = false;

    private void changeTest() {
        isTest = !isTest;

        if (isTest) {
            infoKey2.setText("正在测试");
        } else {
            infoKey2.setText("结束测试");
        }

        for (int i = 0; i < 8; i++) {
            if (byte3Views[i] != null) {
                byte3Views[i].setVisibility(View.VISIBLE);
            }
            if (byte4Views[i] != null) {
                byte4Views[i].setVisibility(View.VISIBLE);
            }
        }
        l2.setVisibility(View.VISIBLE);
        r2.setVisibility(View.VISIBLE);

        /*i.setVisibility(View.VISIBLE);
        a.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);
        x.setVisibility(View.VISIBLE);
        y.setVisibility(View.VISIBLE);
        l1.setVisibility(View.VISIBLE);
        r1.setVisibility(View.VISIBLE);
        up.setVisibility(View.VISIBLE);
        down.setVisibility(View.VISIBLE);
        left.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        leftR.setVisibility(View.VISIBLE);
        rightR.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onChangeSucced(int modelType) {
        this.mModelType = modelType;
        if (modelType == Constants.GAME_MODEL) {
            normalInfoX.setVisibility(View.GONE);
            normalInfoY.setVisibility(View.GONE);
            if (startTest) {
                startTest = false;
                changeTest();
            } else {
                infoKey2.setText("游戏模式");
            }
        } else if (modelType == Constants.NORMAL_MODEL) {
            normalInfoX.setVisibility(View.VISIBLE);
            normalInfoY.setVisibility(View.VISIBLE);
            infoKey2.setText("普通模式");
        }
    }
}
