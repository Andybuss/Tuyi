/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dong.lan.tuyi.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import applib.controller.HXSDKHelper;
import dong.lan.tuyi.utils.Lock;


public class BaseActivity extends Activity {


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showProgress(String string, boolean cancel)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(string);
        progressDialog.setCanceledOnTouchOutside(cancel);
        progressDialog.show();
    }
    public void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        HXSDKHelper.getInstance().getNotifier().reset();
    }

    @Override
    protected void onPause() {
        Lock.canPop = false;
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // umeng
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }


    public void Show(String s)
    {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    public void print(String s)
    {
        System.out.println(s);
    }
}
