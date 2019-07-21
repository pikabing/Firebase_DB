package android.example.zersey;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Events> mEvents;
    Context context;

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
        holder.likes.setText(events.getLikes());
        holder.comments.setText(events.getComments());

    }

    @Override
    public int getItemCount() {
        if (mEvents == null)
            return 0;
        return mEvents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, category, likes, comments;
        ImageView image;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            category = itemView.findViewById(R.id.event_category);
            likes = itemView.findViewById(R.id.like_number);
            comments = itemView.findViewById(R.id.comment_number);
            image = itemView.findViewById(R.id.event_image);
        }
    }

    public void setData(List<Events> events) {
        this.mEvents = events;
    }
}
