package com.example.seerfqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<Model> arrayList;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tasksRef = rootRef.child("Key");
   public Adapter(Context context, ArrayList<Model> arrayList){

        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.single_history_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

       Model model = arrayList.get(position);

        holder.history_item_type.setText(model.getHistory_item_type());
        holder.history_item_summary.setText(model.getHistory_item_summary());
        holder.history_item_date.setText(model.getHistory_item_date());

        holder.remove.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                Toast.makeText(context, "Remove", Toast.LENGTH_SHORT).show();
                arrayList.remove(position);
                notifyItemRemoved(position);

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView history_item_type,history_item_summary,history_item_date;
        ConstraintLayout remove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            history_item_type = itemView.findViewById(R.id.history_item_type);
            history_item_summary = itemView.findViewById(R.id.history_item_summary);
            history_item_date = itemView.findViewById(R.id.history_item_date);

            remove = itemView.findViewById(R.id.remove);

        }
    }
}
