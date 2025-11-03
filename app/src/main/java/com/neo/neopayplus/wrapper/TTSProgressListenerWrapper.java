package com.neo.neopayplus.wrapper;

import com.neo.neopayplus.emv.ITTSProgressListener;
import com.neo.neopayplus.utils.LogUtil;

public class TTSProgressListenerWrapper implements ITTSProgressListener {
    private static final String TAG = "TTSProgressListenerWrap";

    @Override
    public void onStart(String utteranceId) {
        LogUtil.e(TAG, "onStart()");
    }

    @Override
    public void onDone(String utteranceId) {
        LogUtil.e(TAG, "onDone()");
    }

    @Override
    public void onError(String utteranceId) {
        LogUtil.e(TAG, "onError()");
    }

    @Override
    public void onStop(String utteranceId, boolean interrupted) {
        LogUtil.e(TAG, "onStop()");
    }
}
