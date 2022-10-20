package comp5216.sydney.edu.au.afinal.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import comp5216.sydney.edu.au.afinal.EventActivity;
import comp5216.sydney.edu.au.afinal.MapsActivity;
import comp5216.sydney.edu.au.afinal.R;
import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.databinding.FragmentProfileBinding;
import comp5216.sydney.edu.au.afinal.entity.Account;
import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.entity.Events;
import comp5216.sydney.edu.au.afinal.util.Adapter;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView name, email;
    private ImageView image;
    private Account user;
    private boolean isFirst;
    private ArrayList<EventEntity> events;
    private Adapter eventsAdapter;
    private ListView listView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button editProfileBtn = (Button) root.findViewById(R.id.editProfileBtn);
        editProfileBtn.setOnClickListener(new editProfileBtn());

        name = binding.username;
        email = binding.userEmail;
        image = binding.profileImage;
        user = Firebase.getInstance().getLocalUser();
        isFirst = true;

        name.setText(user.getUsername());
        email.setText(user.getEmail());
        if(!user.getIcon().equals(""))
            Glide.with(this)
                    .load(user.getIcon())
                    .into(image);

        listView = binding.listView;
        events = new ArrayList<>();
        eventsAdapter = new Adapter(this.getContext(), events);
        listView.setAdapter(eventsAdapter);
        Firebase.getInstance().getUserEvent(events, eventsAdapter, user.getAccountID());
        listViewListener();

        return root;
    }

    private void listViewListener(){
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            EventEntity event = events.get(i);
            Events tempEvents = Events.getSingleton();
            tempEvents.add(event);
            Intent intent = new Intent(this.getContext(), EventActivity.class);
            intent.putExtra("event",event);
            startActivity(intent);
        });
    }

    public class editProfileBtn implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), EditProfileActivity.class);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isFirst){
            user = Firebase.getInstance().getLocalUser();
            name.setText(user.getUsername());
            email.setText(user.getEmail());
            if(!user.getIcon().equals(""))
                Glide.with(this)
                        .load(user.getIcon())
                        .into(image);

        }isFirst = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

