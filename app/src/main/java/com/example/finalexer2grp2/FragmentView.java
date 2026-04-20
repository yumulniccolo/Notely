package com.example.finalexer2grp2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class FragmentView extends Fragment {

    private String fileName;

    public FragmentView() {
        super(R.layout.fragment_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvContent = view.findViewById(R.id.tv_noteContent);
        FloatingActionButton fabEdit = view.findViewById(R.id.fab_save);

        // ✅ Get fileName from bundle
        if (getArguments() != null) {
            fileName = getArguments().getString("fileName");
        }

        // ✅ Load file content
        if (fileName != null && !fileName.isEmpty()) {
            try {
                FileInputStream fis = requireContext().openFileInput(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr);

                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                reader.close();
                tvContent.setText(content.toString());

            } catch (Exception e) {
                tvContent.setText("Error loading file");
            }
        }

        // ✅ Navigate to Edit
        fabEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("fileName", fileName);

            Navigation.findNavController(v).navigate(
                    R.id.action_fragmentView_to_fragmentEdit,
                    bundle
            );
        });
    }
}