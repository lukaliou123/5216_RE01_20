package comp5216.sydney.edu.au.afinal.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.entity.FollowRelationItem;

public class NetUtil {


    public static Task<Uri> uploadMediaFile(String filePath){
        Task<Uri> uriTask = null;
        if(filePath != null)
        {
            //Firebase
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageReference.child("images/" + new File(filePath).getName());

            UploadTask uploadTask = ref.putFile(Uri.fromFile(new File(filePath)));
            uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Photo uploaded");
                    }
                }
            });
        }
        return uriTask;
    }

    public static List<Task<Uri>> uploadMediaFiles(List<String> filePaths, Context context){
        if(filePaths != null && filePaths.size() != 0){
            final List<Task<Uri>> res = new ArrayList<>();
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading to firebase...");
            progressDialog.show();
            List<String> imageName = new ArrayList<>(filePaths.size());
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference();
            for(int i = 0; i < filePaths.size(); i ++) {
                String uuid = UUID.randomUUID().toString();
                StorageReference ref = storageReference.child("images/" + uuid + ".jpg");
                UploadTask uploadTask = ref.putFile(Uri.fromFile(new File(filePaths.get(i))));
                int finalI = i;
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        res.add(task.getResult().getStorage().getDownloadUrl());
                        progressDialog.setMessage("uploaded" + finalI +"/"+ filePaths.size());
                        if(finalI == filePaths.size() - 1){
                            progressDialog.dismiss();
                        }
                    }
                });
            }
            return res;
        }
        return null;
    }


    public static void uploadEvent(EventEntity event, Context context){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference ref = firebaseFirestore.collection("Events");
        ref.add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Tips")
                        .setMessage("Uploaded!")
                        .setPositiveButton("ok", (dialogInterface, i) -> {});
                builder.create().show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Tips")
                        .setMessage("Upload failed!")
                        .setPositiveButton("ok", (dialogInterface, i) -> {});
                builder.create().show();
            }
        });
    }

    public static void setEvent(EventEntity event, String documentID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firebaseFirestore.collection("Events").document(documentID);
        ref.set(event);
    }

    public static void follow(String loginAccountId, String viewAccountId, OnCompleteListener<DocumentReference> updateUIListener) {
        if(loginAccountId.equals(viewAccountId))
            return;

            FirebaseFirestore.getInstance().collection("FollowRelation").whereEqualTo("followerID", loginAccountId).whereEqualTo("followeeID", viewAccountId).get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if (task.getResult().size() == 0)
                                {
                                    FollowRelationItem item = new FollowRelationItem();
                                    item.setFollowerID(loginAccountId);
                                    item.setFolloweeID(viewAccountId);
                                    Task addFollowTask = FirebaseFirestore.getInstance().collection("FollowRelation").add(item);
                                    if(updateUIListener != null)
                                        addFollowTask.addOnCompleteListener(updateUIListener);
                                }
                            }
                        }
                    }
            );
    }

    public static void unfollow(String loginAccountId, String viewAccountId, OnCompleteListener<Void> updateUIListener) {
        if (loginAccountId == viewAccountId)
            return;

        FirebaseFirestore.getInstance().collection("FollowRelation").whereEqualTo("followerID", loginAccountId).whereEqualTo("followeeID", viewAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                doc.getReference().delete().addOnCompleteListener(
                                        updateUIListener
                                );
                            }
                        }
                    }
                }
        );
    }

}
