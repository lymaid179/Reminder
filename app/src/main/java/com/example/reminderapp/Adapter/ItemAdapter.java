package com.example.reminderapp.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.Interface.ItemCheckClicked;
import com.example.reminderapp.Interface.ItemClicked;
import com.example.reminderapp.LoginActivity;
import com.example.reminderapp.Model.Reminder;
import com.example.reminderapp.R;
import com.example.reminderapp.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>  {

    Context context;
    List<Reminder> Nhacnhos = new ArrayList<>();

    ItemClicked itemClicked;
    ItemCheckClicked itemCheckClicked;


    public ItemAdapter(Context context,  List<Reminder> Nhacnhos) {
        this.context = context;
        this.Nhacnhos = Nhacnhos;
    }

    public void setItemClicked(ItemClicked itemClicked) {
        this.itemClicked = itemClicked;
    }

    public void setItemCheckClicked(ItemCheckClicked itemCheckClicked) {
        this.itemCheckClicked = itemCheckClicked;
    }

    @NonNull
    //@Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ItemAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Reminder reminder = Nhacnhos.get(position);

        String imageName = reminder.getImage().getImagename();
        int resID = context.getResources().getIdentifier(imageName , "drawable" , context.getPackageName());

        holder.imageView.setImageResource(resID);
        holder.title.setText(reminder.getTitle());
        String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(reminder.getTime().toDate());
        holder.time.setText(""+ time);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked.ItemClicked(v,holder.getAdapterPosition());
            }
        });
        holder.isDone.setChecked(reminder.isCheckDone());
        holder.isDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isDone.setChecked(!reminder.isCheckDone());
                reminder.setCheckDone(!reminder.isCheckDone());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("notes")
                        .document(""+reminder.getId())
                        .set(reminder)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                itemCheckClicked.ItemCheckClicked(v,holder.getAdapterPosition(), reminder.isCheckDone());
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return Nhacnhos.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView time;
        CheckBox isDone;



        public ViewHolder(@NonNull View v) {
            super(v);

            imageView = v.findViewById(R.id.imageView);
            title = v.findViewById(R.id.textTitle);
            time = v.findViewById(R.id.textTime);
            isDone = v.findViewById(R.id.checkBox);
        }
    }



    public interface ReminderItemListener {
        void onItemClicked(View v, int position);
    }
}
