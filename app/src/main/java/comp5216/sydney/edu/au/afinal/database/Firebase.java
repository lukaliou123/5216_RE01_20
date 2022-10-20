package comp5216.sydney.edu.au.afinal.database;

import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import comp5216.sydney.edu.au.afinal.R;
import comp5216.sydney.edu.au.afinal.entity.Account;
import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.util.Adapter;

public class Firebase {
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private Account localUser;
    private static Firebase mFirebase;

    private Firebase(){
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth=FirebaseAuth.getInstance();
        localUser = null;
    }

    public static void init() {
        if(mFirebase == null){
            mFirebase = new Firebase();
        }
    }

    public static Firebase getInstance(){
        if (mFirebase == null){
            init();
        }return mFirebase;
    }


    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public Account getLocalUser(){
        return localUser;
    }

    public StorageReference getPhotoStorageRef(String doc){
        return storageRef.child(doc);
    }

    public Task setLocalUser(String uid){
        return mFirestore.collection("Accounts")
                .document(uid)
                .get()
                .addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        DocumentSnapshot document = task1.getResult();
                        String uid1 = (String) document.getData().get("AccountID");
                        String username = (String) document.getData().get("Username");
                        String icon = (String) document.getData().get("Icon");
                        String email = (String) document.getData().get("Email");
                        String gender = (String) document.getData().get("Gender");
                        String birth = (String) document.getData().get("Birth");
                        localUser = new Account(uid1, username, icon, gender, email, birth);
                        Log.d("Firebase", "Finish!!!!!!!!!");
                    } else {
                        Log.d("Firebase", "Error getting documents: ", task1.getException());
                    }
                });
    }

    public Task<Uri> uploadPhotoToStorage(String uid, Uri uri){
        if (uri == null){
            return null;
        }
        StorageReference photoRef = storageRef.child("images/" + uid + "/" +uri.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(uri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Photo uploaded");
                }
            }
        });
        return uriTask;
    }

    public Task updateAccountSnapshotInFireStore(String id, String name, String icon, String gender, String birth){
        DocumentReference accountRef = mFirestore.collection("Accounts").document(id);
        Map<String, Object> account = new HashMap<>();
        account.put("Username", name);
        account.put("Icon", icon);
        account.put("Birth", birth);
        account.put("Gender", gender);
        return accountRef.update(account);
    }

    public Task createAccountSnapshotInFireStore(String id, String username, String icon, String email){
        CollectionReference accounts = mFirestore.collection("Accounts");
        Map<String, String> account = new HashMap<>();
        account.put("AccountID", id);
        account.put("Username", username);
        account.put("Email", email);
        account.put("Icon", icon);
        account.put("Birth", "1/1/2000");
        account.put("Gender", "Secret");
        return accounts.document(id).set(account).addOnSuccessListener(documentReference -> Log.d("Firestore", "DocumentSnapshot added with ID: " + id)).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error adding document", e);
            }
        });
    }


    public void logout(){
        mAuth.signOut();
    }

    public void getAllEvents( ArrayList<EventEntity> events, Adapter eventsAdapter){
        mFirestore.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                events.add(EventEntity.FromQueryDocument(document));
                            }
                            eventsAdapter.notifyDataSetChanged();
                            Log.d("Firebase", "Finish");
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void getUserEvent(ArrayList<EventEntity> events, Adapter eventsAdapter, String uid) {
        mFirestore.collection("Events").whereEqualTo("blog_ref", uid).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            events.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                EventEntity event = EventEntity.FromQueryDocument(doc);
                                events.add(event);

                            }eventsAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }
}
