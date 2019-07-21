package android.example.zersey;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class EventIndividual extends AppCompatActivity {

    private long maxid = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_individual_layout);
        ImageView eventImage;
        TextView title, desc;
        RecyclerView commentsRV;
        FloatingActionButton addComments;
        DatabaseReference databaseReference;
        eventImage = findViewById(R.id.event_image);
        title = findViewById(R.id.event_title);
        desc = findViewById(R.id.event_desc);
        commentsRV = findViewById(R.id.comments_recycler_view);
        addComments = findViewById(R.id.addcomments);
        Events events = getIntent().getParcelableExtra("event");

        RequestOptions options = new RequestOptions().placeholder(R.drawable.error_image_placeholder).error(R.drawable.error_image_placeholder);
        String image = events.getImage();
        if (!image.equals("none")) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(image);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(EventIndividual.this)
                            .load(uri)
                            .into(eventImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } else {
            Glide.with(EventIndividual.this).load("").apply(options).into(eventImage);
        }

        title.setText(events.getTitle());
        desc.setText(events.getDesc());

        List<String> mComments = new ArrayList<>();
        CommentsAdapter commentsAdapter = new CommentsAdapter(this, mComments);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComments.clear();
                DataSnapshot commSnap = dataSnapshot.child("comments");
                Iterable<DataSnapshot> commentTree = commSnap.getChildren();
                for (DataSnapshot child: commentTree) {
                    Object post = child.child("post").getValue();
                    if (post != null) {
                        post = post.toString();
                        if (post.equals(events.getTitle())) {
                            String string = child.child("comment").getValue().toString();
                            mComments.add(string);
                        }
                    }
                }
                commentsAdapter.setData(mComments);
                commentsAdapter.notifyDataSetChanged();
                commentsRV.setLayoutManager(new LinearLayoutManager(EventIndividual.this, RecyclerView.VERTICAL, false));
                commentsRV.setAdapter(commentsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(EventIndividual.this,R.style.Theme_AppCompat_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.comment_popup);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                TextView closePopup = dialog.findViewById(R.id.closePopup);
                closePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final TextInputEditText comment = dialog.findViewById(R.id.comment_on_event);
                MaterialButton post = dialog.findViewById(R.id.postComment);
                post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comm_string = comment.getText().toString();
                        if (comm_string.equals("")){
                            Toast.makeText(EventIndividual.this, "Please insert a comment", Toast.LENGTH_SHORT).show();;
                        } else {
                            DatabaseReference comments = FirebaseDatabase.getInstance().getReference().child("comments").child(String.valueOf(System.currentTimeMillis()));
                            comments.child("comment").setValue(comm_string);
                            comments.child("post").setValue(events.getTitle());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }
}
