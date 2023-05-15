package com.example.reminderapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.Adapter.ItemAdapter;
import com.example.reminderapp.Model.Category;
import com.example.reminderapp.Model.Reminder;
import com.example.reminderapp.Notifications.AlarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText,contentEditText;
    ImageButton backbtn;
    TextView pageTitleTextView;
    TextView txtNgay, txtgio;
    Button dateBtn, timeBtn, addBtn;
    AlarmManager alarmManager;

    List<Reminder> reminderList = new ArrayList<>();

    final int[] selectedYear = {0};
    final int[] selectedMonth = {0};
    final int[] selectedDay = {0};
    final int[] selectedHour = {0};
    final int[] selectedMinute = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        backbtn = findViewById(R.id.backbtn);
        pageTitleTextView = findViewById(R.id.page_title);
        dateBtn = findViewById(R.id.button3);
        timeBtn= findViewById(R.id.button4);
        addBtn = findViewById(R.id.button5);
        txtNgay = findViewById(R.id.textNgay);
        txtgio = findViewById(R.id.textGio);

    //backbtn
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getData();

    //datebtn
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int cy = c.get(Calendar.YEAR);
                int cm = c.get(Calendar.MONTH);
                int cd = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(NoteDetailsActivity.this,
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

                TimePickerDialog dialog = new TimePickerDialog(NoteDetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int h, int m) {
                        txtgio.setText(h+":"+m);
                        selectedHour[0] = h;
                        selectedMinute[0] = m;

                    }
                }, hh, mm, false);
                dialog.show();
            }
        });

        addBtn.setOnClickListener( (v)-> saveNote());


    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContext = contentEditText.getText().toString();
        String noteDate = txtNgay.getText().toString();
        String noteTime = txtgio.getText().toString();
        if(noteTitle.isEmpty() || noteDate.isEmpty() || noteTime.isEmpty() ){
            Toast.makeText(NoteDetailsActivity.this, "Missing information", Toast.LENGTH_SHORT).show();
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
        int id = 0;
        if (!reminderList.isEmpty()) {
            id = reminderList.get(reminderList.size()-1).getId() +1;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Category category = (Category) getIntent().getSerializableExtra("category");

        Reminder note = new Reminder(id, category,noteTitle, noteContext, timestamp,false,user.getEmail());
        setNotification(note);
        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Reminder note){
        DocumentReference documentReference;

            //create new note
        documentReference = Utility.getCollectionReferenceForNotes().document(""+note.getId());

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });

    }

    private void getData(){
        reminderList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes").orderBy("id").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Reminder reminder = document.toObject(Reminder.class);
                        reminderList.add(reminder);
                    }
                }
            }
        });
    }

    private void setNotification(Reminder reminder){
        Intent notificationIntent = new Intent(this, AlarmReceiver.class);
        System.out.println(reminder.getTitle());
        notificationIntent.putExtra("title", reminder.getTitle());
        notificationIntent.putExtra("id", ""+reminder.getId());

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reminder.getTime().toDate());
        System.out.println(calendar.getTime());

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);  //set repeating every 24 hours
    }



}