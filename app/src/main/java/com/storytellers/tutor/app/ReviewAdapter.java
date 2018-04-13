package com.storytellers.tutor.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context context;
    private List<Review> reviewList;
    private ReviewAdapter.ReviewAdapterListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;

        MyViewHolder(final View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.review_user_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReviewSelected(itemView, reviewList.get(getAdapterPosition()));
                }
            });
        }
    }

    ReviewAdapter(Context context, List<Review> reviewList, ReviewAdapter.ReviewAdapterListener listener){
        this.context = context;
        this.listener = listener;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.review_item_list, parent, false);
        return new ReviewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {
        final Review review = reviewList.get(position);
        Picasso.get().load(review.getUserImage()).error(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public interface ReviewAdapterListener {
        void onReviewSelected(View view, Review review);
    }
}