package android.example.zersey;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddEvent extends AppCompatActivity {

    private MaterialButton image, create;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference rootOfEvents = databaseReference.child("events");
    private String title, desc, cat, imagePath;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private StorageTask storageTask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        TextInputEditText event_title, event_desc, event_cat;

        event_title = findViewById(R.id.event_title);
        event_desc = findViewById(R.id.event_desc);
        event_cat = findViewById(R.id.event_category);
        create = findViewById(R.id.create);
        image = findViewById(R.id.event_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = new UploadImage().requestPermission(AddEvent.this);
                if (result == 2) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = event_title.getText().toString();
                desc = event_desc.getText().toString();
                cat = event_cat.getText().toString();

                if (title.equals("") || desc.equals("")) {
                    Toast.makeText(AddEvent.this, "Please give a name and a description.", Toast.LENGTH_SHORT).show();
                } else if (storageTask != null && storageTask.isInProgress()) {
                    Toast.makeText(AddEvent.this, "Upload in progress. Please wait.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference childNode = rootOfEvents.child("title");
                    childNode.setValue(title);
                    childNode.child("desc").setValue(desc);
                    childNode.child("category").setValue(cat);
                    childNode.child("image").setValue(imagePath);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK &&
                data != null) {
            Uri uri = data.getData();
            new UploadImage();
            String path = UploadImage.getPath(getApplicationContext(), uri);
            if (path.equals("")) {
                Toast.makeText(this, "Invalid. Please select from local storage only", Toast.LENGTH_SHORT).show();
                return;
            }
            imagePath = (uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1));
            image.setEnabled(false);
            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(this, "Uplaod in progress...", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage(uri);
            }
        }
    }

    private void uploadImage(Uri uri) {

        storageTask = storageReference.child(String.valueOf(System.currentTimeMillis())).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddEvent.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                image.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEvent.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                image.setEnabled(true);
            }
        });
    }
}
