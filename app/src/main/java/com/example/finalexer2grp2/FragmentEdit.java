package com.example.finalexer2grp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

public class FragmentEdit extends Fragment {

    private boolean isDirty = false;
    private String originalContent = "";
    private String originalFileName = null;
    private EditText etContent;

    public FragmentEdit() {
        super(R.layout.fragment_edit);
    }

    public boolean hasUnsavedChanges() {
        return isDirty;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etContent = view.findViewById(R.id.eT_noteContent);
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save);
        EditText etTitle = getActivity().findViewById(R.id.toolbar_title_edittext);

        originalFileName = getArguments() != null ? getArguments().getString("fileName") : null;

        if (originalFileName != null && !originalFileName.isEmpty()) {
            loadFile(originalFileName, etContent);
            etTitle.setText(originalFileName.replace(".txt", ""));
        } else {
            etTitle.setText("");
        }

        originalContent = etContent.getText().toString();

        etContent.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                isDirty = !s.toString().equals(originalContent);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        fabSave.setOnClickListener(v -> {
            String newFileName = etTitle.getText().toString().trim();

            if (newFileName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            saveToFile(newFileName + ".txt");
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isDirty) {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    return;
                }

                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Exit?")
                        .setMessage("You have unsaved changes. Exit anyway?")
                        .setPositiveButton("Yes", (d, w) -> {
                            Navigation.findNavController(requireView()).navigateUp();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void saveToFile(String newFileName) {
        try {
            File newFile = new File(getContext().getFilesDir(), newFileName);
            FileOutputStream writer = new FileOutputStream(newFile);

            String content = etContent.getText().toString();
            writer.write(content.getBytes());
            writer.close();

            if (originalFileName != null
                    && !originalFileName.isEmpty()
                    && !originalFileName.equals(newFileName)) {

                updatePinnedFileName(originalFileName, newFileName);

                File oldFile = new File(getContext().getFilesDir(), originalFileName);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            originalFileName = newFileName;
            originalContent = content;
            isDirty = false;

            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(getView()).navigateUp();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePinnedFileName(String oldFileName, String newFileName) {
        SharedPreferences prefs = requireContext().getSharedPreferences("pinned_notes", Context.MODE_PRIVATE);
        Set<String> pinnedSet = new HashSet<>(prefs.getStringSet("pinned_files", new HashSet<>()));

        if (pinnedSet.contains(oldFileName)) {
            pinnedSet.remove(oldFileName);
            pinnedSet.add(newFileName);
            prefs.edit().putStringSet("pinned_files", pinnedSet).apply();
        }
    }

    private void loadFile(String fileName, EditText etContent) {
        try {
            File file = new File(getContext().getFilesDir(), fileName);
            java.util.Scanner scanner = new java.util.Scanner(file).useDelimiter("\\A");
            String content = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            etContent.setText(content);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading file", Toast.LENGTH_SHORT).show();
        }
    }
}
