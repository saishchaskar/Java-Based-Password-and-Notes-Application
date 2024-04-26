package com.example.passnote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notesList;
    private Context context;
    private NotesDatabaseHelper dbHelper;

    public NotesAdapter(List<Note> notesList, Context context, NotesDatabaseHelper dbHelper) {
        this.notesList = notesList;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        final Note note = notesList.get(holder.getAdapterPosition());

        holder.noteTitle.setText(note.getTitle());
        holder.noteText.setText(note.getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Note note = notesList.get(adapterPosition);
                    Intent intent = new Intent(context, EditNoteActivity.class);
                    intent.putExtra("note", note);
                    context.startActivity(intent);
                }
            }
        });

        holder.deleteNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteNoteDialog(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;
        TextView noteText;
        ImageButton deleteNoteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.note_title);
            noteText = itemView.findViewById(R.id.note_text);
            deleteNoteButton = itemView.findViewById(R.id.delete_note_button);
        }
    }

    private void showDeleteNoteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you want to delete this note?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.deleteNote(note.getId());
                notesList.remove(note);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }
}
