package com.techspark.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleet,contentet;
    ImageButton saveNotebtn;
    TextView pageTitletv,deletenotetvbtn;
    String title,content,docId;
    boolean isEditMode = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleet = findViewById(R.id.notes_title_text);
        contentet = findViewById(R.id.notes_content_text);
        saveNotebtn = findViewById(R.id.save_note_button);
        pageTitletv = findViewById(R.id.page_title);
        deletenotetvbtn = findViewById(R.id.delete_note_btn);

        //recieve data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleet.setText(title);
        contentet.setText(content);
        if(isEditMode){
            pageTitletv.setText("Edit your note");
            deletenotetvbtn.setVisibility(View.VISIBLE);
        }

        deletenotetvbtn.setOnClickListener((v)->deleteNoteFromFirebase());

        saveNotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
            void saveNote(){
                String noteTitle = titleet.getText().toString();
                String noteContent = contentet.getText().toString();

                if(noteTitle==null || noteTitle.isEmpty()){
                    titleet.setError("Title is required");
                    return;
                }

                Note note = new Note();
                note.setTitle(noteTitle);
                note.setContent(noteContent);
                note.setTimestamp(Timestamp.now());

                saveNoteToFirebase(note);
        }

        void saveNoteToFirebase(Note note){
            DocumentReference documentReference;
            if(isEditMode){
                //update the note
                documentReference = Utility.getCollectionReferenceForNotes().document(docId);
            }
            else{
                //create new note
                documentReference = Utility.getCollectionReferenceForNotes().document();
            }


            documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        // note is added
                        Utility.showToast(NoteDetailsActivity.this,"Note Added successfully");
                        finish();
                    }
                    else{
                        // failure
                        Utility.showToast(NoteDetailsActivity.this,"Failed while adding notes");
                    }
                }
            });
        }


        });


    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;

            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();
                }
                else{
                    // failure
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting notes");
                }
            }
        });
    }

}