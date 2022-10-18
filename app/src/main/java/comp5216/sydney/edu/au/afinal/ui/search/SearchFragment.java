package comp5216.sydney.edu.au.afinal.ui.search;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import comp5216.sydney.edu.au.afinal.databinding.FragmentSearchBinding;
import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.util.Adapter;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private ListView listView;
    private ArrayList<EventEntity> events;
    private Adapter eventsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearch;
        final SearchView searchView = binding.searchView;
        listView = binding.listView;
        events = new ArrayList<>();
        searchView.onActionViewExpanded();
        searchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        eventsAdapter = new Adapter(this.getContext(), events);
        listView.setAdapter(eventsAdapter);
        listViewListener();
        return root;
    }

    private void listViewListener(){
        listView.setOnItemClickListener((adapterView, view, i, l) -> {

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}