package com.neo.neopayplus.basic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.R;
import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.utils.Utility;

public class LowMemoryKillerActivity extends BaseAppCompatActivity {
    private EditText edtSetPkgName;
    private EditText edtRemovePkgName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmk_package);
        initView();
    }

    private void initView() {
        initToolbarBringBack(R.string.basic_lmk_package);
        edtSetPkgName = findViewById(R.id.edt_set_pkg_name);
        edtRemovePkgName = findViewById(R.id.edt_remove_pkg_name);
        findViewById(R.id.btn_set).setOnClickListener(this);
        findViewById(R.id.btn_remove).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set:
                setLMKPackage();
                break;
            case R.id.btn_remove:
                removeLMKPackage();
                break;
        }
    }

    /** Set a package to system low memory killer white list */
    private void setLMKPackage() {
        try {
            String pkgName = edtSetPkgName.getText().toString();
            if (TextUtils.isEmpty(pkgName)) {
                showToast("package name should not be empty");
                edtSetPkgName.requestFocus();
                return;
            }
            int code = MyApplication.app.basicOptV2.setLMKPackage(pkgName);
            String msg = null;
            if (code == 0) {
                msg = "success";
            } else {
                msg = Utility.formatStr("failed, code: %d", code);
            }
            showToast(msg);
            LogUtil.e(TAG, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Remove a package from system low memory killer white list */
    private void removeLMKPackage() {
        try {
            String pkgName = edtRemovePkgName.getText().toString();
            if (TextUtils.isEmpty(pkgName)) {
                showToast("package name should not be empty");
                edtRemovePkgName.requestFocus();
                return;
            }
            int code = MyApplication.app.basicOptV2.removeLMKPackage(pkgName);
            String msg = null;
            if (code == 0) {
                msg = "success";
            } else {
                msg = Utility.formatStr("failed, code: %d", code);
            }
            showToast(msg);
            LogUtil.e(TAG, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
