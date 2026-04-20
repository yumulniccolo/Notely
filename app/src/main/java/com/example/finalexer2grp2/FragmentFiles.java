package com.example.finalexer2grp2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

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
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.clearFocus();
        etSearch.setFocusableInTouchMode(true);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        fabAdd.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_fragmentFiles_to_fragmentEdit);
        });

        loadFiles();

        // search watcher
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

        adapter = new FileAdapter(fileList, new FileAdapter.OnFileClickListener() {
            @Override
            public void onFileClick(File file) {
                Bundle bundle = new Bundle();
                bundle.putString("fileName", file.getName());
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_fragmentFiles_to_fragmentView, bundle
                );
            }
            @Override
            public void onEditClick(File file) {
                Bundle bundle = new Bundle();
                bundle.putString("fileName", file.getName());
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_fragmentFiles_to_fragmentEdit, bundle
                );
            }

            @Override
            public void onDeleteClick(File file) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (file.delete()) {
                                loadFiles();
                            }
                        })
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void sortFiles(String mode) {
        if (adapter == null) return;

        File[] files = requireContext().getFilesDir()
                .listFiles((dir, name) -> name.endsWith(".txt"));

        List<File> fileList = new ArrayList<>();
        if (files != null) fileList.addAll(Arrays.asList(files));

        if (mode.equals("alpha")) {
            fileList.sort((f1, f2) ->
                    f1.getName().compareToIgnoreCase(f2.getName()));
        }

        if (mode.equals("latest")) {
            fileList.sort((f1, f2) ->
                    Long.compare(f2.lastModified(), f1.lastModified()));
        }

        adapter = new FileAdapter(fileList, new FileAdapter.OnFileClickListener() {
            @Override
            public void onFileClick(File file) {
                Bundle bundle = new Bundle();
                bundle.putString("fileName", file.getName());

                Navigation.findNavController(requireView())
                        .navigate(R.id.action_fragmentFiles_to_fragmentView, bundle);
            }

            @Override
            public void onEditClick(File file) {
                Bundle bundle = new Bundle();
                bundle.putString("fileName", file.getName());

                Navigation.findNavController(requireView())
                        .navigate(R.id.action_fragmentFiles_to_fragmentEdit, bundle);
            }

            @Override
            public void onDeleteClick(File file) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (file.delete()) {
                                loadFiles();
                            }
                        })
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}