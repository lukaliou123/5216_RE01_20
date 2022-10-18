package comp5216.sydney.edu.au.afinal.database;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.util.Adapter;

public class Firebase {
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private static Firebase mFirebase;

    private Firebase(){
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth=FirebaseAuth.getInstance();
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
