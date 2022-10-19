package comp5216.sydney.edu.au.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.databinding.ActivityNavigatorBinding;
import comp5216.sydney.edu.au.afinal.ui.home.HomeFragment;

public class NavigatorBase extends AppCompatActivity {

    private ActivityNavigatorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigator);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void mapActivityLaunch(View v) {
        Intent intent = new Intent(NavigatorBase.this, MapsActivity.class);
        startActivity(intent);
    }

    public void add_button_home_launch(View v) {
        Intent intent = new Intent(NavigatorBase.this, AddActivity.class);
        startActivity(intent);
    }


    public void logout(View v){
        Firebase.getInstance().logout();
        startActivity(new Intent(NavigatorBase.this, LoginActivity.class));
    }
}