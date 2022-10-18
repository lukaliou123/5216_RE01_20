package comp5216.sydney.edu.au.afinal.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.afinal.entity.EventEntity;

public class Firebase {
    private FirebaseFirestore mFirestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static Firebase mFirebase;

    private Firebase(){
        mFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public static void init() {
        if(mFirebase == null){
            mFirebase = new Firebase();
        }
    }

    private static Firebase getInstance(){
        if (mFirebase == null){
            throw new NullPointerException();
        }return mFirebase;
    }


    private ArrayList<EventEntity> getSearchItem(String tag) {
        ArrayList<EventEntity> events = new ArrayList<>();
        mFirestore.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint geo = (GeoPoint) document.getData().get("location");
                                String title = (String) document.getData().get("title");
                                String blogRef = (String) document.getData().get("blog_ref");
                                String blogger = (String) document.getData().get("Blogger");
                                Timestamp timestamp = (Timestamp) document.getData().get("time");
                                String description = (String) document.getData().get("description");
                                List<String> imageUrl = (List<String>) document.getData().get("imageUrl");
                                Integer likes = (Integer) document.getData().get("likes");
                                String address = (String) document.getData().get("address");
                                events.add(new EventEntity(blogger,blogRef,description,timestamp,geo,imageUrl,likes,address,title));
                            }
                        } else {
                            Log.d("Firebase", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return events;
    }


}
