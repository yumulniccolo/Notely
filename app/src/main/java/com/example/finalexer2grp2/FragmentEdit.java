package com.example.finalexer2grp2;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;

public class FragmentEdit extends Fragment {

    private boolean isDirty = false;
    private String originalContent = "";

    public FragmentEdit() {
        super(R.layout.fragment_edit);
    }

    public boolean hasUnsavedChanges() {
        return isDirty;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etContent = view.findViewById(R.id.eT_noteContent);
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save);
        EditText etTitle = getActivity().findViewById(R.id.toolbar_title_edittext);

        // get passed filename
        String fileName = getArguments() != null ? getArguments().getString("fileName") : null;

        // loading filename if editing
        if (fileName != null && !fileName.isEmpty()) {
            loadFile(fileName, etContent);
            etTitle.setText(fileName.replace(".txt", ""));
        } else {
            etTitle.setText(""); // new note
        }

        originalContent = etContent.getText().toString();

        etContent.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                isDirty = !s.toString().equals(originalContent);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // save button
        fabSave.setOnClickListener(v -> {

            String newFileName = etTitle.getText().toString().trim();
            String content = etContent.getText().toString();

            if (newFileName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            saveToFile(newFileName + ".txt", content);
        });

        //calling the exitdialog
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        //no changes made
                        if (!isDirty) {
                            setEnabled(false);
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                            return;
                        }

                        //may changes
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Exit?")
                                .setMessage("You have unsaved changes. Exit anyway?")
                                .setPositiveButton("Yes", (d, w) -> {
                                    Navigation.findNavController(requireView()).navigateUp();
                                })
                                .setNegativeButton("No", null).show();
                    }
                }
        );
    }
    private void saveToFile(String fileName, String content) {
        try {
            File file = new File(getContext().getFilesDir(), fileName);
            FileOutputStream writer = new FileOutputStream(file);

            writer.write(content.getBytes());
            writer.close();

            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();

            Navigation.findNavController(getView()).navigateUp();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving file", Toast.LENGTH_SHORT).show();
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
