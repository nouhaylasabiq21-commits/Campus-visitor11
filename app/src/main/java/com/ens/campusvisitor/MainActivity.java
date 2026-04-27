package com.ens.campusvisitor.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.ens.campusvisitor.R;
import com.ens.campusvisitor.ui.auth.LoginActivity;
import com.ens.campusvisitor.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        NavHostFragment navHost = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHost.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        String role = session.getUserRole();
        if (!"admin".equals(role) && !"agent".equals(role)) {
            bottomNav.getMenu().removeItem(R.id.checkInFragment);
        }
        if ("visitor".equals(role)) {
            bottomNav.getMenu().removeItem(R.id.visitorsFragment);
            bottomNav.getMenu().removeItem(R.id.hostsFragment);
            bottomNav.getMenu().removeItem(R.id.logsFragment);
        }
    }

    public void logout() {
        new SessionManager(this).clearSession();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}