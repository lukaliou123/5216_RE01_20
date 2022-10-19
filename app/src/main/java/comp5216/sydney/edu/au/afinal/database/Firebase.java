package comp5216.sydney.edu.au.afinal.database;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public Task setLocalUser(String uid){
        return mFirestore.collection("Accounts")
                .whereEqualTo("AccountID", uid)
                .get()
                .addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()) {
                        Log.d("Firebase", String.valueOf(task1.getResult().size())+"!!!!!!!!!");
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            String uid1 = (String) document.getData().get("AccountID");
                            String username = (String) document.getData().get("Username");
                            String icon = (String) document.getData().get("Icon");
                            String email = (String) document.getData().get("Email");
                            String gender = (String) document.getData().get("Gender");
                            String birth = (String) document.getData().get("Birth");
                            localUser = new Account(uid1, username, icon, gender, email, birth);
                        }
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

    public Task createAccountSnapshotInFireStore(String id, String username, String icon, String email){
        CollectionReference accounts = mFirestore.collection("Accounts");
        Map<String, String> account = new HashMap<>();
        account.put("AccountID", id);
        account.put("Username", username);
        account.put("Email", email);
        account.put("Icon", icon);
        account.put("Birth", "");
        account.put("Gender", "Secret");
        return accounts.add(account).addOnSuccessListener(documentReference -> Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId())).addOnFailureListener(new OnFailureListener() {
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
                                GeoPoint geo = (GeoPoint) document.getData().get("geo");
                                String title = (String) document.getData().get("title");
                                String blogRef = (String) document.getData().get("blog_ref");
                                String blogger = (String) document.getData().get("Blogger");
                                Timestamp timestamp = (Timestamp) document.getData().get("timeStamp");
                                String description = (String) document.getData().get("description");
                                List<String> imageUrl = (List<String>) document.getData().get("imageUrl");
                                long likesInLong = (Long) document.getData().get("likes");
                                Integer likes = (int) likesInLong;
                                String address = (String) document.getData().get("location");
                                events.add(new EventEntity(blogger,blogRef,description,timestamp,geo,imageUrl,likes,address,title));
                                eventsAdapter.notifyDataSetChanged();
                            }
                            Log.d("Firebase", "Finish");
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}
