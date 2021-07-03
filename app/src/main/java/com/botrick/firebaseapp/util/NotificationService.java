package com.botrick.firebaseapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.botrick.firebaseapp.R;
import com.botrick.firebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.botrick.firebaseapp.util.App.CHANNEL_1;

public class NotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        //é executado quando o serviço é criado -> apenas uma vez
        DatabaseReference receiveRef = FirebaseDatabase.getInstance().getReference("requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("receive");

        receiveRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                showNotify(user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public void showNotify(User user){
        //Criando a notificação
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1).setSmallIcon(R.drawable.ic_account_circle_black_24dp).setContentTitle("Nova Solicitação!").setContentText(user.getNome()).setPriority(Notification.PRIORITY_HIGH).build();

        //enviando para o canal Channel
        NotificationManagerCompat nm;
        nm = NotificationManagerCompat.from(getApplicationContext());
        nm.notify(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //é executado quando o serviço é chamado -> várias vezes
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
