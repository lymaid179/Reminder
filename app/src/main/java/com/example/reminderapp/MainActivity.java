package com.example.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.Adapter.ItemAdapter;
import com.example.reminderapp.Interface.ItemClicked;
import com.example.reminderapp.Model.Category;
import com.example.reminderapp.Model.Reminder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ItemClicked, Serializable {

    FloatingActionButton floatingActionButton;
    TabLayout tabLayout;
    ImageButton menuBtn;
    RecyclerView recyclerView;
    List<Reminder> reminderList = new ArrayList<>();
    ItemAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        menuBtn = findViewById(R.id.menubtn);
        recyclerView = findViewById(R.id.listItem);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Hoạt Động"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hoàn thành"));
        menuBtn.setOnClickListener((v)->showMenu() );

       //nut troi
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryChooseActivity.class);
                startActivity(intent);
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new ItemAdapter(MainActivity.this, reminderList);
        adapter.setItemClicked(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData(){
        reminderList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Reminder reminder = document.toObject(Reminder.class);
                        reminderList.add(reminder);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    void showMenu(){
        PopupMenu popupMenu  = new PopupMenu(MainActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //If user haven't login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void ItemClicked(View v, int position) {
        Reminder reminder = reminderList.get(position);
        Intent intent = new Intent(MainActivity.this, UpdateReminderActivity.class);
        intent.putExtra("id", reminder.getId());
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("content", reminder.getContent());
        intent.putExtra("time", reminder.getTime().toDate());
        intent.putExtra("category", reminder.getImage());

        startActivity(intent);
    }
}