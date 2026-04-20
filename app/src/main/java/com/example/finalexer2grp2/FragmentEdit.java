package com.example.finalexer2grp2;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.LinearLayout;

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
    private EditText etContent; // class field

    public FragmentEdit() {
        super(R.layout.fragment_edit);
    }

    public boolean hasUnsavedChanges() {
        return isDirty;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etContent = view.findViewById(R.id.eT_noteContent); // assign to class field
        FloatingActionButton fabSave = view.findViewById(R.id.fab_save);
        EditText etTitle = getActivity().findViewById(R.id.toolbar_title_edittext);
        Button btnBold = view.findViewById(R.id.btnBold);
        Button btnItalic = view.findViewById(R.id.btnItalic);
        Button btnUnderline = view.findViewById(R.id.btnUnderline);
        LinearLayout formattingToolbar = view.findViewById(R.id.formattingToolbar);

        // get passed filename
        String fileName = getArguments() != null ? getArguments().getString("fileName") : null;

        if (fileName != null && !fileName.isEmpty()) {
            loadFile(fileName, etContent);
            etTitle.setText(fileName.replace(".txt", ""));
        } else {
            etTitle.setText("");
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

        // keyboard listener - toolbar follows keyboard
        View rootView = requireActivity().getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            int screenHeight = rootView.getHeight();
            int keypadHeight = screenHeight - rect.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                formattingToolbar.setTranslationY(-keypadHeight);
            } else {
                formattingToolbar.setTranslationY(0);
            }
        });

        // Bold button toggle
        btnBold.setOnClickListener(v -> {
            int start = etContent.getSelectionStart();
            int end = etContent.getSelectionEnd();
            if (start < end) {
                Spannable spannable = etContent.getText();
                StyleSpan[] spans = spannable.getSpans(start, end, StyleSpan.class);
                boolean alreadyBold = false;
                for (StyleSpan span : spans) {
                    if (span.getStyle() == Typeface.BOLD) {
                        alreadyBold = true;
                        spannable.removeSpan(span);
                    }
                }
                if (!alreadyBold) {
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                Toast.makeText(getContext(), "Select text first", Toast.LENGTH_SHORT).show();
            }
        });

        // Italic button toggle
        btnItalic.setOnClickListener(v -> {
            int start = etContent.getSelectionStart();
            int end = etContent.getSelectionEnd();
            if (start < end) {
                Spannable spannable = etContent.getText();
                StyleSpan[] spans = spannable.getSpans(start, end, StyleSpan.class);
                boolean alreadyItalic = false;
                for (StyleSpan span : spans) {
                    if (span.getStyle() == Typeface.ITALIC) {
                        alreadyItalic = true;
                        spannable.removeSpan(span);
                    }
                }
                if (!alreadyItalic) {
                    spannable.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                Toast.makeText(getContext(), "Select text first", Toast.LENGTH_SHORT).show();
            }
        });

        // Underline button toggle
        btnUnderline.setOnClickListener(v -> {
            int start = etContent.getSelectionStart();
            int end = etContent.getSelectionEnd();
            if (start < end) {
                Spannable spannable = etContent.getText();
                UnderlineSpan[] spans = spannable.getSpans(start, end, UnderlineSpan.class);
                if (spans.length > 0) {
                    for (UnderlineSpan span : spans) {
                        spannable.removeSpan(span);
                    }
                } else {
                    spannable.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                Toast.makeText(getContext(), "Select text first", Toast.LENGTH_SHORT).show();
            }
        });

        // save button
        fabSave.setOnClickListener(v -> {
            String newFileName = etTitle.getText().toString().trim();
            if (newFileName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            saveToFile(newFileName + ".txt");
        });

        // back pressed
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
                        .setNegativeButton("No", null).show();
            }
        });
    }

    private void saveToFile(String fileName) {
        try {
            File file = new File(getContext().getFilesDir(), fileName);
            FileOutputStream writer = new FileOutputStream(file);

            // save as HTML to preserve formatting
            String htmlContent = android.text.Html.toHtml(etContent.getText());
            writer.write(htmlContent.getBytes());
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

            // load as HTML to restore formatting
            etContent.setText(android.text.Html.fromHtml(content, android.text.Html.FROM_HTML_MODE_LEGACY));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading file", Toast.LENGTH_SHORT).show();
        }
    }
}