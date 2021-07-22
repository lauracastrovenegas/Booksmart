package com.example.booksmart.ui.resources;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.booksmart.R;
import com.example.booksmart.databinding.FragmentResourcesBinding;
import com.parse.ParseUser;

public class ResourcesFragment extends Fragment {

    public static final String KEY_SCHOOL = "school";

    private ResourcesViewModel resourcesViewModel;
    TextView toolbarTitleSchool;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        toolbarTitleSchool = view.findViewById(R.id.tvToolbarTitleSchool);
        toolbarTitleSchool.setText(ParseUser.getCurrentUser().getString(KEY_SCHOOL));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}