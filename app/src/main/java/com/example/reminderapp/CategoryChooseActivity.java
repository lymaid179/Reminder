package com.example.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.Adapter.RecycleViewApdapter;
import com.example.reminderapp.Interface.CategoryOnItemSelected;
import com.example.reminderapp.Model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryChooseActivity extends AppCompatActivity implements CategoryOnItemSelected {

    ImageButton back;

    RecyclerView categorychoose;

    List<Category> categoryList = new ArrayList<>();

    RecycleViewApdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_choose);

        back = findViewById(R.id.imageButton);
        categorychoose = findViewById(R.id.recyclerView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter = new RecycleViewApdapter(this,categoryList);
        GridLayoutManager itemManager = new GridLayoutManager(this, 3);

        adapter.setCategoryOnItemSelected(this);
        categorychoose.setAdapter(adapter);

        categorychoose.setLayoutManager(itemManager);
        getData();


    }

    @Override
    public void CategoryOnItemSelected(View v, int position) {
            Category category = categoryList.get(position);
            Intent intent = new Intent(CategoryChooseActivity.this, NoteDetailsActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
    }

    private void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Category category = document.toObject(Category.class);
                        categoryList.add(category);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
