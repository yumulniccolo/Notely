package com.example.finalexer2grp2;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

        TextView tvTitle = view.findViewById(R.id.tv_noteTitle);
        TextView tvContent = view.findViewById(R.id.tv_noteContent);
        FloatingActionButton fabEdit = view.findViewById(R.id.fab_save);

        // hide toolbar edittext when viewing
        EditText etToolbarTitle = getActivity().findViewById(R.id.toolbar_title_edittext);
        etToolbarTitle.setVisibility(View.GONE);

        if (getArguments() != null) {
            fileName = getArguments().getString("fileName");
        }

        if (fileName != null && !fileName.isEmpty()) {
            tvTitle.setText(fileName.replace(".txt", ""));

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
                tvContent.setText(android.text.Html.fromHtml(content.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));
            } catch (Exception e) {
                tvContent.setText("Error loading file");
            }
        }

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