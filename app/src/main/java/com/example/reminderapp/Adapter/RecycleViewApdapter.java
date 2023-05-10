package com.example.reminderapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.Interface.CategoryOnItemSelected;
import com.example.reminderapp.Model.Category;
import com.example.reminderapp.R;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewApdapter extends  RecyclerView.Adapter< RecycleViewApdapter.ViewHolder> {
    Context context;
    List<Category> categoryList = new ArrayList<>();
    CategoryOnItemSelected categoryOnItemSelected;

    public   RecycleViewApdapter(Context context, List<Category> categoryList){
        this.context = context;
        this.categoryList = categoryList;
    }

    public void setCategoryOnItemSelected( CategoryOnItemSelected categoryOnItemSelected) {
        this.categoryOnItemSelected = categoryOnItemSelected;
    }


    @NonNull
    @Override
    public RecycleViewApdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cate, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewApdapter.ViewHolder holder, int position) {
        Category cate = categoryList.get(position);

        String[] imageName = cate.getImagename().split("[.]");
        String imageNameFormat = imageName[0].toLowerCase();

        int resID = context.getResources().getIdentifier(imageNameFormat , "drawable" , context.getPackageName());

        holder.imageView.setImageResource(resID);
        holder.title.setText(cate.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryOnItemSelected.CategoryOnItemSelected(v,holder.getAdapterPosition());
            }
        });
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        View itemView;

        public ViewHolder(@NonNull View v) {
            super(v);

            imageView = v.findViewById(R.id.imageView2);
            title = v.findViewById(R.id.textView2);
            itemView = v.findViewById(R.id.item);
        }
    }
}
