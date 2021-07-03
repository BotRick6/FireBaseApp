package com.botrick.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.botrick.firebaseapp.util.NotificationService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationActivity extends AppCompatActivity {
    private ImageView btnMenu;
    private DrawerLayout drawerLayout;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        btnMenu = findViewById(R.id.navigation_icon);
        drawerLayout = findViewById(R.id.nav_drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_nome)).setText(auth.getCurrentUser().getDisplayName());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email)).setText(auth.getCurrentUser().getEmail());

        //TextView textNome = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_nome);
        //TextView textEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);

        //textNome.setText(auth.getCurrentUser().getDisplayName());
        //textEmail.setText(auth.getCurrentUser().getEmail());

        //evento de logout
        navigationView.getMenu().findItem(R.id.nav_menu_logout).setOnMenuItemClickListener(item -> {
            auth.signOut();
            finish();
           return false;
        });

        //Recuperar o navController -> realiza a troca de fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //Juntar navController com navView(menu)
        NavigationUI.setupWithNavController(navigationView, navController);

        //Criando um serviço
        Intent service = new Intent(getApplicationContext(), NotificationService.class);

        getApplicationContext().startService(service);
    }

}
