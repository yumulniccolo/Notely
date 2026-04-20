package com.example.finalexer2grp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> files;
    private List<File> filesFiltered;
    private OnFileClickListener listener;

    public interface OnFileClickListener {
        void onFileClick(File file);
    }

    public FileAdapter(List<File> files, OnFileClickListener listener) {
        this.files = files;
        this.filesFiltered = new ArrayList<>(files);
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_note, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = filesFiltered.get(position);
        holder.tvFileName.setText(file.getName().replace(".txt", ""));
        String content = readFileContent(file);
        holder.tvContentCard.setText(content);
        holder.itemView.setOnClickListener(v -> listener.onFileClick(file));
    }

    private String readFileContent(File file) {
        try {
            java.util.Scanner scanner = new java.util.Scanner(file).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (Exception e) {
            return "Error reading content";
        }
    }

    @Override
    public int getItemCount() {
        return filesFiltered.size();
    }

    public void filter(String query) {
        filesFiltered.clear();
        if (query.isEmpty()) {
            filesFiltered.addAll(files);
        } else {
            String lower = query.toLowerCase();
            for (File file : files) {
                String fileName = file.getName().replace(".txt", "").toLowerCase();
                String fileContent = readFileContent(file).toLowerCase();

                if (fileName.contains(lower) || fileContent.contains(lower)) {
                    filesFiltered.add(file);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        TextView tvContentCard;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvTitleCard);
            tvContentCard = itemView.findViewById(R.id.tvContentCard);
        }
    }
}