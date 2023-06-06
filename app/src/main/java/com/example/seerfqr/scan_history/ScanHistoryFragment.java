package com.example.seerfqr.scan_history;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.seerfqr.Adapter;
import com.example.seerfqr.Model;
import com.example.seerfqr.R;
import com.example.seerfqr.databinding.FragmentScanHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanHistoryFragment extends Fragment {
    FragmentScanHistoryBinding binding;

    public RecyclerView history_list;

    Adapter adapter;

    ArrayList<Model> arrayList;

    private ScanHistoryViewModel mViewModel;

    public static ScanHistoryFragment newInstance() {
        return new ScanHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanHistoryBinding.inflate(inflater,container,false);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tasksRef = rootRef.child("Key");
        arrayList = new ArrayList<>();
        history_list = (RecyclerView) binding.getRoot().findViewById(R.id.history_list);


        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Model model = new Model(singleSnapshot.child("history_item_type").getValue().toString(),
                            singleSnapshot.child("history_item_summary").getValue().toString(),
                            singleSnapshot.child("history_item_date").getValue().toString());

                    arrayList.add(model);

                }
                adapter = new Adapter(binding.getRoot().getContext() , arrayList);
                history_list.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
                history_list.setAdapter(adapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "onCancelled", databaseError.toException());
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.action_scanHistoryFragment_to_nav_scanner);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),onBackPressedCallback);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScanHistoryViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        history_list = view.findViewById(R.id.history_list);
        history_list.hasFixedSize();
        history_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new Adapter(getActivity(),arrayList);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}