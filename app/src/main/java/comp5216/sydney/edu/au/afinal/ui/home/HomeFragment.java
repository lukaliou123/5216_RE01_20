package comp5216.sydney.edu.au.afinal.ui.home;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import comp5216.sydney.edu.au.afinal.AddActivity;
import comp5216.sydney.edu.au.afinal.MapsActivity;
import comp5216.sydney.edu.au.afinal.NavigatorBase;
import comp5216.sydney.edu.au.afinal.databinding.FragmentHomeBinding;
import comp5216.sydney.edu.au.afinal.ui.profile.EditProfileActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void mapActivityLaunch(View view) {
    }


    public void add_button_home_launch(View view) {
    }
}