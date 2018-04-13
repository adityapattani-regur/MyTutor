package com.storytellers.tutor.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.storytellers.tutor.app.Utils.CircleTransform;

import java.util.List;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.MyViewHolder>{

    private Context context;
    private List<Tutor> tutorList;
    private TutorAdapterListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tutorName, tutorKnows;
        ImageView tutorImage;

        MyViewHolder(final View itemView) {
            super(itemView);
            tutorImage = itemView.findViewById(R.id.tutor_image);
            tutorName = itemView.findViewById(R.id.tutor_name);
            tutorKnows = itemView.findViewById(R.id.tutor_knows);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTutorSelected(itemView, tutorList.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setTutors(List<Tutor> tutors) {
        tutorList = tutors;
        notifyDataSetChanged();
    }

    TutorAdapter(Context context, List<Tutor> employeeList, TutorAdapterListener listener){
        this.context = context;
        this.listener = listener;
        this.tutorList = employeeList;
    }

    @NonNull
    @Override
    public TutorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.tutor_item_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorAdapter.MyViewHolder holder, int position) {
        final Tutor tutor = tutorList.get(position);
        holder.tutorName.setText(tutor.getName());

        if (tutor.getKnows() == null || tutor.getKnows().length() == 0) {
            holder.tutorKnows.setText("New user");
        } else {
            holder.tutorKnows.setText(tutor.getKnows());
        }

        if (tutor.getImage() != null) {
            Picasso.get().load(tutor.getImage())
                    .transform(new CircleTransform())
                    .error(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(holder.tutorImage);
        }
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    public interface TutorAdapterListener {
        void onTutorSelected(View view, Tutor tutor);
    }
}
