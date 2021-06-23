package com.botrick.firebaseapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

//o certo é LoadingDialog: eu escrevi errado -_-
public class LoadinDialog {
    private Activity activity;
    private AlertDialog dialog;
    private int layoutId;

    public LoadinDialog(Activity activity, int layoutId){
        //Layout -> R.Layout.custom_dialog.xml
        this.activity = activity;
        this.layoutId = layoutId;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        //setando o Layout do AlertDialog
        builder.setView(inflater.inflate(layoutId, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }

}
