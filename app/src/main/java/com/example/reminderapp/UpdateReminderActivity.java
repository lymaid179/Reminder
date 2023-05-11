package com.example.reminderapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.Model.Category;
import com.example.reminderapp.Model.Reminder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateReminderActivity extends AppCompatActivity {
    EditText titleEditText,contentEditText;
    ImageButton backbtn;
    String title,content,docId;
    TextView txtNgay, txtgio;
    Button dateBtn, timeBtn, updateBtn;
    Button deleteNoteTextViewBtn;
    List<Reminder> reminderList = new ArrayList<>();

    final int[] selectedYear = {0};
    final int[] selectedMonth = {0};
    final int[] selectedDay = {0};
    final int[] selectedHour = {0};
    final int[] selectedMinute = {0};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        backbtn = findViewById(R.id.backbtn);
        dateBtn = findViewById(R.id.button3);
        timeBtn = findViewById(R.id.button4);
        updateBtn = findViewById(R.id.button5);
        deleteNoteTextViewBtn = findViewById(R.id.button6);
        txtNgay = findViewById(R.id.textNgay);
        txtgio = findViewById(R.id.textGio);


        //backbtn
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //datebtn
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int cy = c.get(Calendar.YEAR);
                int cm = c.get(Calendar.MONTH);
                int cd = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(UpdateReminderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                txtNgay.setText(d + "/" + m + "/" + y);
                                selectedYear[0] = y;
                                selectedMonth[0] = m;
                                selectedDay[0] = d;
                            }
                        }, cy, cm, cd);
                dialog.show();
            }
        });

        //timeBtn
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hh = c.get(Calendar.HOUR_OF_DAY);
                int mm = c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(UpdateReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int h, int m) {
                        txtgio.setText(h + ":" + m);
                        selectedHour[0] = h;
                        selectedMinute[0] = m;

                    }
                }, hh, mm, false);
                dialog.show();
            }
        });

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        Date time = (Date) intent.getSerializableExtra("time");
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        selectedYear[0] = c.get(Calendar.YEAR);
        selectedMonth[0]=c.get(Calendar.MONTH);
                selectedDay[0]=c.get(Calendar.DAY_OF_MONTH);
                selectedHour[0]=c.get(Calendar.HOUR);
                selectedMinute[0]=c.get(Calendar.MINUTE);

        titleEditText.setText(title);
        contentEditText.setText(content);
        txtNgay.setText(new SimpleDateFormat("dd-MM-yyyy").format(time));
        txtgio.setText(new SimpleDateFormat("HH:mm:ss").format(time));

        //deletebtn
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase() );

        //updatebtn

        updateBtn.setOnClickListener((v)->UpdateNote() );


    }

    void UpdateNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        String noteDate = txtNgay.getText().toString();
        String noteTime = txtgio.getText().toString();
        if(noteTitle.isEmpty() || noteDate.isEmpty() || noteTime.isEmpty() ){
            Toast.makeText(UpdateReminderActivity.this, "Missing information", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar c = Calendar.getInstance();
        c.set(selectedYear[0],
                selectedMonth[0],
                selectedDay[0],
                selectedHour[0],
                selectedMinute[0],
                0);
        Timestamp timestamp = new Timestamp(c.getTime());
        int id = getIntent().getIntExtra("id",0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Category category = (Category) getIntent().getSerializableExtra("category");
        Reminder note = new Reminder(id, category,noteTitle, noteContent, timestamp,false,user.getEmail());
        UpdateNoteToFirebase(note);

    }


    void UpdateNoteToFirebase(Reminder note){
        DocumentReference documentReference;

        //create new note
        documentReference = Utility.getCollectionReferenceForNotes().document(""+note.getId());

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is updated
                    Utility.showToast(UpdateReminderActivity.this,"Note updated successfully");
                    finish();
                }else{
                    Utility.showToast(UpdateReminderActivity.this,"Failed while updating note");
                }
            }
        });

    }
    void deleteNoteFromFirebase(){

        int id = getIntent().getIntExtra("id",0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("notes")
                .document(""+id)
                .delete();
        finish();
    }

    }


