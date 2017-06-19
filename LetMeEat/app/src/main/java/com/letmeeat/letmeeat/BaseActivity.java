package com.letmeeat.letmeeat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

/**
 * Created by santhosh on 15/11/2016.
 * Base Activity which includes all the methods required in each activity and extends AppCompactActivity
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    void showProgressDialog(String title, String message) {
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.processing);
        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.please_wait);
        }


        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message);
            progressDialog.setCancelable(false);
        } else if (!progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}
