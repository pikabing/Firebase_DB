package android.example.zersey;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Events> mEvents;
    Context context;
    private boolean liked = false;

    public EventAdapter(Context context, List<Events> events){
        this.mEvents = events;
        this.context = context;
    }

    @NonNull
    @Override
    public EventAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout, parent, false);
        return new EventAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.MyViewHolder holder, int position) {

        final Events events = mEvents.get(position);
        RequestOptions options = new RequestOptions().placeholder(R.drawable.error_image_placeholder).error(R.drawable.error_image_placeholder);
        String image = events.getImage();
        if (!image.equals("none")) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(image);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .into(holder.image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } else {
            Glide.with(context).load("").apply(options).into(holder.image);
        }

        holder.title.setText(events.getTitle());
        holder.category.setText(events.getCategory());
        holder.likes.setText("" + events.getLikes());
        Query query = FirebaseDatabase.getInstance().getReference().child("comments").orderByChild("post").equalTo(events.getTitle());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.setComments(dataSnapshot.getChildrenCount());
                holder.comments.setText("" + events.getComments());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = FirebaseDatabase.getInstance().getReference().child("events").orderByChild("title").equalTo(events.getTitle());
                long likes = events.getLikes();
                if (!liked) {
                    likes++;
                } else {
                    likes--;
                }
                query.getRef().child(events.getTitle()).child("likes").setValue(likes);
                liked = !liked;
            }
        });

        holder.commentsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventIndividual.class);
                intent.putExtra("event", events);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mEvents == null)
            return 0;
        return mEvents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, category, likes, comments;
        ImageView image, likeImage, commentsImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            category = itemView.findViewById(R.id.event_category);
            likes = itemView.findViewById(R.id.like_number);
            comments = itemView.findViewById(R.id.comment_number);
            image = itemView.findViewById(R.id.event_image);
            likeImage = itemView.findViewById(R.id.likes);
            commentsImage = itemView.findViewById(R.id.comments);
        }
    }

    public void setData(List<Events> events) {
        this.mEvents = events;
    }
}
