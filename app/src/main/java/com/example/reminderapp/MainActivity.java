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
import com.example.reminderapp.Interface.ItemCheckClicked;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity implements ItemClicked, ItemCheckClicked {

    FloatingActionButton floatingActionButton;
    TabLayout tabLayout;
    ImageButton menuBtn;
    RecyclerView recyclerView;
    List<Reminder> reminderList = new ArrayList<>();
    List<Reminder> adapterReminderList = new ArrayList<>();
    ItemAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        menuBtn = findViewById(R.id.menubtn);
        recyclerView = findViewById(R.id.listItem);
        tabLayout = findViewById(R.id.tabLayout);

        menuBtn.setOnClickListener((v)->showMenu() );

       //nut troi
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryChooseActivity.class);
                startActivity(intent);
            }

        });

        //RecycleView
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new ItemAdapter(MainActivity.this, adapterReminderList);
        adapter.setItemClicked(this);
        adapter.setItemCheckClicked(this);
        recyclerView.setAdapter(adapter);

        //TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Hoạt Động"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hoàn thành"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getDataIsDone(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData(){
        reminderList.clear();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .whereEqualTo("userEmail", user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Reminder reminder = document.toObject(Reminder.class);
                        reminderList.add(reminder);
                    }

                    getDataIsDone(tabLayout.getSelectedTabPosition());
                }
            }
        });
    }

    private void getDataIsDone(int tabPosition) {
        adapterReminderList.clear();
        boolean isDone = false;
        if (tabPosition == 0) {
            isDone = false;
        } else {
            isDone = true;
        }
        boolean finalIsDone = isDone;
        List<Reminder> filterReminder = reminderList.stream().filter(new Predicate<Reminder>() {
            @Override
            public boolean test(Reminder reminder) {
                return reminder.isCheckDone() == finalIsDone;
            }
        }).collect(Collectors.toList());
        adapterReminderList.addAll(filterReminder);
        adapter.notifyDataSetChanged();
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
        Reminder reminder = adapterReminderList.get(position);
        Intent intent = new Intent(MainActivity.this, UpdateReminderActivity.class);
        intent.putExtra("id", reminder.getId());
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("content", reminder.getContent());
        intent.putExtra("time", reminder.getTime().toDate());
        intent.putExtra("category", reminder.getImage());

        startActivity(intent);
    }

    @Override
    public void ItemCheckClicked(View v, int position, boolean isDone) {
        getData();
    }
}