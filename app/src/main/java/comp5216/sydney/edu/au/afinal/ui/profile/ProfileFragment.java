package comp5216.sydney.edu.au.afinal.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import comp5216.sydney.edu.au.afinal.MapsActivity;
import comp5216.sydney.edu.au.afinal.R;
import comp5216.sydney.edu.au.afinal.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    Button btn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button editProfileBtn = (Button) root.findViewById(R.id.editProfileBtn);  //此处使得Button和xml中的按钮联系
        editProfileBtn.setOnClickListener(new editProfileBtn());  //这一行是在将but

        return root;
    }

    public class editProfileBtn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), EditProfileActivity.class);  //从前者跳到后者，特别注意的是，在fragment中，用getActivity()来获取当前的activity
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    public void editProfileBtn(View view) {
//        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
//        if (intent != null){
//         //   mLauncher.launch(intent);
//        }
//    }

}

