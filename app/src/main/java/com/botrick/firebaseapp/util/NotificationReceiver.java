package com.botrick.firebaseapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String messagem = intent.getStringExtra("toast");

        Toast.makeText(context, messagem, Toast.LENGTH_SHORT).show();

        //abrindo uma activity atravez da notificação
        Intent intent1 = new Intent();
        intent1.setClassName("com.botrick.firebaseapp", "com.botrick.firebaseapp.NavigationActivity");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

      //PendingIntent intentFragment = new NavDeepLinkBuilder(context).setComponentName(NavigationActivity.class).setGraph(R.id.nav_menu_cadastro_imagem).createPendingIntent();
      //context.startActivity(intent1);
    }

}
