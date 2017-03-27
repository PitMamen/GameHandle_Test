package com.mygt.handshank.sample;

/**
 * Created by chengkai on 2017/1/16.
 */
public interface IUITestView {

    void setDataKey(int position, byte keycode);

    void setDataInfo(boolean shortdata ,String shortdatas, String data);

    void setChangeModel(byte data);

}
