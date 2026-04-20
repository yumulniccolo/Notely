package com.example.finalexer2grp2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentFiles extends Fragment {

    private RecyclerView recyclerView;
    private FileAdapter adapter;

    public FragmentFiles() {
        super(R.layout.fragment_files);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddNote);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAdd.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_fragmentFiles_to_fragmentEdit);
        });

        loadFiles();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFiles();
    }

    private void loadFiles() {
        File directory = getContext().getFilesDir();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        List<File> fileList = new ArrayList<>();

        if (files != null) {
            fileList.addAll(Arrays.asList(files));
        }

        adapter = new FileAdapter(fileList, file -> {

            Bundle bundle = new Bundle();
            bundle.putString("fileName", file.getName());

            Navigation.findNavController(requireView()).navigate(R.id.action_fragmentFiles_to_fragmentEdit, bundle);
        });

        recyclerView.setAdapter(adapter);
    }
}