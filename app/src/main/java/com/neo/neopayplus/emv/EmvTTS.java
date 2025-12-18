package com.neo.neopayplus.emv;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.LogUtil;

import java.util.Locale;

/**
 * Text-to-Speech wrapper for EMV payment prompts.
 * Provides audio feedback during payment transactions.
 */
public final class EmvTTS extends UtteranceProgressListener {
    private static final String TAG = "EmvTTS";
    private TextToSpeech textToSpeech;
    private boolean supportTTS;
    private ITTSProgressListener listener;

    private EmvTTS() {
    }

    public static EmvTTS getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setTTSListener(ITTSProgressListener l) {
        listener = l;
    }

    public void removeTTSListener() {
        listener = null;
    }

    private static final class SingletonHolder {
        private static final EmvTTS INSTANCE = new EmvTTS();
    }

    public void init() {
        // Initialize TTS object
        destroy();
        textToSpeech = new TextToSpeech(MyApplication.app, this::onTTSInit);
        textToSpeech.setOnUtteranceProgressListener(this);
    }

    public void play(String text) {
        play(text, "0");
    }

    public void play(String text, String utteranceId) {
        if (!supportTTS) {
            Log.e(TAG, "EmvTTS: play TTS failed, TTS not support...");
            return;
        }
        if (textToSpeech == null) {
            Log.e(TAG, "EmvTTS: play TTS skipped, textToSpeech not init..");
            return;
        }
        Log.e(TAG, "play() text: [" + text + "]");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onStart(String utteranceId) {
        Log.e(TAG, "TTS started, utteranceId:" + utteranceId);
        if (listener != null) {
            listener.onStart(utteranceId);
        }
    }

    @Override
    public void onDone(String utteranceId) {
        Log.e(TAG, "TTS done, utteranceId:" + utteranceId);
        if (listener != null) {
            listener.onDone(utteranceId);
        }
    }

    @Override
    public void onError(String utteranceId) {
        Log.e(TAG, "TTS error, utteranceId:" + utteranceId);
        if (listener != null) {
            listener.onError(utteranceId);
        }
    }

    @Override
    public void onStop(String utteranceId, boolean interrupted) {
        Log.e(TAG, "TTS stopped, utteranceId:" + utteranceId + ",interrupted:" + interrupted);
        if (listener != null) {
            listener.onStop(utteranceId, interrupted);
        }
    }

    public void stop() {
        if (textToSpeech != null) {
            int code = textToSpeech.stop();
            Log.e(TAG, "tts stop() code:" + code);
        }
    }

    public boolean isSpeaking() {
        if (textToSpeech != null) {
            return textToSpeech.isSpeaking();
        }
        return false;
    }

    public void destroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    /** TTS initialization callback */
    private void onTTSInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            LogUtil.e(TAG, "EmvTTS: init TTS failed, status:" + status);
            supportTTS = false;
            return;
        }
        updateTtsLanguage();
        if (supportTTS) {
            textToSpeech.setPitch(1.0f);
            textToSpeech.setSpeechRate(1.0f);
            LogUtil.e(TAG, "onTTSInit() success,locale:" + textToSpeech.getVoice().getLocale());
        }
    }

    /** Update TTS language */
    private void updateTtsLanguage() {
        Locale locale = Locale.ENGLISH;
        int result = textToSpeech.setLanguage(locale);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            supportTTS = false;
            LogUtil.e(TAG, "updateTtsLanguage() failed, TTS not support in locale:" + locale);
        } else {
            supportTTS = true;
            LogUtil.e(TAG, "updateTtsLanguage() success, TTS locale:" + locale);
        }
    }
}
