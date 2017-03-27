package com.mygt.handshank.sample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chengkai on 2017/1/15.
 */
public class GameModel implements IGameDispatch , BluetoothLeService.Callback{

    private final String TAG = this.getClass().getSimpleName();
    private final int INIT_MODEL = 55;
    private final int CHANGE_MODEL_SUCCED = 56;
    private final int CHANGE_MODEL = 57;

    private int modelType = Constants.NORMAL_MODEL;
    private int beforeModelType = 0;

    private List<String> addressList = new ArrayList<String>();
    private Set<String> rigAddressList = new HashSet<String>();

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;

    private BluetoothAdapter mBluetoothAdapter;

    private Context mContext;

    private boolean shortbytes = false;
    private String infodata = "";
    private String shortdata = "";

    private List<Integer> keycodes = new ArrayList<>();

    private static final int KEY_GAME_MODEL = Constants.GAME_MODEL;
    private static final int KEY_NORMAL_MODEL = Constants.NORMAL_MODEL;
    private static final int KEY_GET_INFO = Constants.GET_INFO;
    private static final int KEY_VIBRATE = Constants.MAKE_VIBRATE;
    private static final int KEY_GET_STATE = Constants.GET_STATE;
    private static final int KEY_GET_NAME = Constants.GET_NAME;
    private static final int KEY_GET_ID = Constants.GET_ID;

    private SparseArray<byte[]> events = new SparseArray<>();

    boolean initModel = false;

    IUITestView mUITestView;

    Handler mHandler;

    OnChangeModelListener mOnChangeModelListener;

    public GameModel(Context context, IUITestView uiTestView) {
        this.mContext = context;
        this.mUITestView = uiTestView;
        this.mHandler = new ModelHandler();
        init(mContext);
    }

    class ModelHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String address = bundle.getString(Constants.EXTRA_ADDRESS);
            switch (msg.what) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    mConnected = true;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    addRigAddressList(address);
                    if (!initModel) {
                        mHandler.sendEmptyMessageDelayed(INIT_MODEL, 700);
                    }
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    mConnected = false;
                    rigAddressList.remove(address);
                    break;
                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    // 将数据显示在mDataField上
                    byte[] data = bundle.getByteArray(Constants.EXTRA_DATA);
                    dispatchData(data);
                    displayData(data, address);
                    break;
                case INIT_MODEL:
                    dispatchEvent(Constants.NORMAL_MODEL);
                    break;
                case CHANGE_MODEL_SUCCED:
                    if (mOnChangeModelListener != null) {
                        mOnChangeModelListener.onChangeSucced(modelType);
                    }
                    break;
                case CHANGE_MODEL:
                    break;

            }
        }
    }

    // 管理服务的生命周期
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                return;
            }
            connectService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.处理服务所激发的各种事件
    // ACTION_GATT_CONNECTED: connected to a GATT server.连接一个GATT服务
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.从GATT服务中断开连接
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.查找GATT服务
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.从服务中接受数据
    @Override
    public void onDispatchData(int type, byte[] data, String address) {
        Message message = mHandler.obtainMessage(type);
        Bundle bundle = new Bundle();
        bundle.putByteArray(Constants.EXTRA_DATA, data);
        bundle.putString(Constants.EXTRA_ADDRESS, address);
        message.setData(bundle);
        mHandler.sendMessage(message);
        Log.d("测试次数GameModel", "onDispatchData");

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "action:" + action);
            String address ;
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                address = device.getAddress();
                addRigAddressList(address);
                Log.d(TAG, "devices:" + device.getName() + "address:" + address);
            }
        }
    };

    private void addRigAddressList(String address) {
        if (address != null) {
            rigAddressList.add(address);
        }
    }

    public void setOnChangeModelListener(OnChangeModelListener onChangeModelListener) {
        mOnChangeModelListener = onChangeModelListener;
    }

    public void init(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //获取已经保存过的设备信息
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : devices) {
            Log.d(TAG, "bond:" + bluetoothDevice.getBondState());
            addressList.add(bluetoothDevice.getAddress());
            Log.d(TAG, "devices：" + bluetoothDevice.getName() + ", " + bluetoothDevice.getAddress());
        }
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onResume() {
        register();
        if (mBluetoothLeService != null && !mBluetoothLeService.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            return;
        }
        connectService();
    }

    private void connectService() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.registerCallback(this);
            for (String address : addressList) {
                final boolean result = mBluetoothLeService.connect(address);
                Log.d(TAG, "Connect request result=" + result + ",address:" + address);
                if (result) {
                    addRigAddressList(address);
                }
            }
        }
    }

    public void onPause() {
        unRegister();
        if (mBluetoothLeService != null) {
            mBluetoothLeService.unRegisterCallback(this);
        }
    }

    public void onDestroy() {
        unRegister();
        if (mBluetoothLeService != null) {
            mBluetoothLeService.unRegisterCallback(this);
        }
        mContext.unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    boolean isRegister = false;
    private void register() {
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        isRegister = true;
    }

    private void unRegister() {
        if (isRegister) {
            mContext.unregisterReceiver(mGattUpdateReceiver);
            isRegister = false;
        }
    }

    private void dispatchData(byte[] data) {
        if (data == null || data.length < 3) {
            return;
        }
        byte type = data[2];
        switch (type) {
            case Constants.KEYCODE_TYPE:
                decodeKey(data);
                break;
            case Constants.VIBRATE_TYPE:
                decodeVibrate(data[3]);
                break;
            case Constants.STATE_TYPE:
                break;
            case Constants.ID_TYPE:
                break;
            case Constants.NAME_TYPE:
                break;
            case Constants.INFO_TYPE:
                break;
            case Constants.CHANGE_TYPE:
                changeModel(data[3]);
                break;
        }
    }

    private void changeModel(byte model) {
        mUITestView.setChangeModel(model);
        if (model == 0) {
            changeSuccess();
            modelType = beforeModelType;
            mHandler.sendEmptyMessage(CHANGE_MODEL_SUCCED);
        }
    }

    boolean stopVibrate = true;
    int indexVibrate = 0;
    private void decodeVibrate(byte data) {
        if (data == 0) {
            stopVibrate = true;
        }
    }

    private void decodeKey(byte[] data) {
        int length = data.length;
        for(int i=3; i<length; i++) {
            mUITestView.setDataKey(i, data[i]);
        }
        byte left = 0;
        if (length > 6) {
            left = data[5];
        }
        byte right = 0;
        if (length > 7) {
            right = data[6];
        }
        if (left != 0 || right != 0) {
            stopVibrate = false;
            indexVibrate = 0;
        }
        if (!stopVibrate && indexVibrate < 5) {
            byte[] replyData = makeVibrate(left, right);
            writeCharacteristic(replyData);
            indexVibrate ++;
        }
    }

    /**
     * private void updateConnectionState(final int resourceId) {
     * runOnUiThread(new Runnable() {
     *
     * @Override public void run() { mConnectionState.setText(resourceId); } });
     * }
     */
    private void displayData(byte[] data, String address) {
        byte type = data[2];
        byte state = data[3];
        if (type == Constants.CHANGE_TYPE && state == 0) {
            return;
        }
        String dataStr = Byte2six.Bytes2HexString(data);
        String info = dataStr + "\n address---" + address;
        Log.d(TAG, info);
        if(dataStr.length()<10){
            shortbytes = true;
            shortdata = dataStr;
        }else{
            shortbytes = false;
            infodata = info;
            shortdata = "";
        }
        mUITestView.setDataInfo(shortbytes,shortdata,infodata);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        //system
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        return intentFilter;
    }

    @Override
    public void dispatchEvent(int type) {
        if (mBluetoothLeService == null) {
            return;
        }
        byte[] data = null;
        switch (type) {
            case Constants.GAME_MODEL:
                data = gameModel();
                break;
            case Constants.NORMAL_MODEL:
                data = nomalModel();
                break;
            /*case Constants.MAKE_VIBRATE:
                makeVibrate(data);
                break;*/
            case Constants.GET_INFO:
                data = getInfo();
                break;
            case Constants.GET_STATE:
                data = getState();
                break;
            case Constants.GET_NAME:
                data = getName();
                break;
            case Constants.GET_ID:
                data = getID();
                break;
        }
        Log.d(TAG, "dispatchEvent");
        if (data == null) {
            return;
        }
        if (type == Constants.GAME_MODEL || type == Constants.NORMAL_MODEL) {
            beforeModelType = type;
            writeCharacteristic(data);
        }else {
            writeCharacteristic(data);
        }
    }

    public int getModel() {
        return modelType;
    }

    boolean changeStart = false;
    boolean changeSuccess = false;
    int index = 0;

    public void changeSuccess() {
        changeSuccess = true;
//        changeStart = false;
    }

    private void writeChangeCharacteristic(final byte[] data) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!changeSuccess) {
                    writeChangeCharacteristic(data);
                }
                index ++;
                if (index >= 5) {
                    index = 1;
                    changeStart = false;
                }
            }
        };
        writeCharacteristic(data);
        mHandler.postDelayed(runnable, 100);

        /*index ++;
        if (index >= 5 || changeSuccess) {
            index = 1;
        }*/

//        mBluetoothLeService.writeChangeCharacteristic(data);
    }

    private void dispatchWriteChange(byte[] data) {
        Log.d(TAG,"writeChangeCharacteristic,"+changeStart);
        if (changeStart) {
            return;
        }
        Log.d(TAG,"thread:"+Thread.currentThread().getName()+",id:"+Thread.currentThread().getId());

//        changeSuccess = false;
        changeStart = true;

        writeChangeCharacteristic(data);
    }

    private void writeCharacteristic(byte[] data) {
        mBluetoothLeService.writeCharacteristic(data);
    }

    private byte[] gameModel() {
        byte[] data = events.get(KEY_GAME_MODEL);
        if (data == null || data.length < 4) {
            data = new byte[20];
            events.put(KEY_GAME_MODEL, data);
        }
        data[0] = 0x20;
        data[1] = 0x04;
        data[2] = 0x08;
        data[3] = 0x01;
        return data;
    }

    private byte[] nomalModel() {
        byte[] data = events.get(KEY_NORMAL_MODEL);
        if (data == null || data.length < 4) {
            data = new byte[20];
            events.put(KEY_NORMAL_MODEL, data);
        }
        data[0] = 0x20;
        data[1] = 0x04;
        data[2] = 0x08;
        data[3] = 0x00;
        return data;
    }

    /**
     *
     * @param left 左边强度
     * @param right 右边强度
     */
    private byte[] makeVibrate(byte left, byte right) {
        byte[] data = events.get(KEY_VIBRATE);
        if (data == null || data.length < 5) {
            data = new byte[20];
            events.put(KEY_VIBRATE, data);
        }
        data[0] = (byte) 0x20;
        data[1] = 0x05;
        data[2] = 0x02;
        data[3] = left;
        data[4] = right;
        return data;
    }

    private byte[] getInfo() {
        byte[] data = events.get(KEY_GET_INFO);
        if (data == null || data.length < 3) {
            data = new byte[20];
            events.put(KEY_GET_INFO, data);
        }
        data[0] = 0x20;
        data[1] = 0x08;
        data[2] = (byte) 0xf1;
        return data;
    }

    private byte[] getState() {
        byte[] data = events.get(KEY_GET_STATE);
        if (data == null || data.length < 3) {
            data = new byte[20];
            events.put(KEY_GET_STATE, data);
        }
        data[0] = 0x20;
        data[1] = 0x03;
        data[2] = 0x03;
        return data;
    }

    private byte[] getName() {
        byte[] data = events.get(KEY_GET_NAME);
        if (data == null || data.length < 3) {
            data = new byte[20];
            events.put(KEY_GET_NAME, data);
        }
        data[0] = 0x20;
        data[1] = 0x03;
        data[2] = (byte) 0xf0;
        return data;
    }

    private byte[] getID() {
        byte[] data = events.get(KEY_GET_ID);
        if (data == null || data.length < 3) {
            data = new byte[20];
            events.put(KEY_GET_ID, data);
        }
        data[0] = 0x20;
        data[1] = 0x03;
        data[2] = 0x06;
        return data;
    }

    interface OnChangeModelListener {
        void onChangeSucced(int modelType);
    }

}
