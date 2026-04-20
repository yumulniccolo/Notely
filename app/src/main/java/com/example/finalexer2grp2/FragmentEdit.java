package com.example.finalexer2grp2;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;

public class FragmentEdit extends Fragment {

    public FragmentEdit() {
        super(R.layout.fragment_edit);
    }

    // Save created / edited text to file
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etContent = view.findViewById(R.id.eT_noteContent);
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save);

        fabSave.setOnClickListener(v -> {
            EditText etTitle = getActivity().findViewById(R.id.toolbar_title_edittext);

            String fileName = etTitle.getText().toString().trim();
            String content = etContent.getText().toString();

            if (fileName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title in the toolbar", Toast.LENGTH_SHORT).show();
                return;
            }

            saveToFile(fileName + ".txt", content);
        });
    }

    private void saveToFile(String fileName, String content) {
        try {
            File path = getContext().getFilesDir();
            File file = new File(path, fileName);

            FileOutputStream writer = new FileOutputStream(file);
            writer.write(content.getBytes());
            writer.close();

            Toast.makeText(getContext(), "Saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            Navigation.findNavController(getView()).navigateUp();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }
}