package comp5216.sydney.edu.au.afinal.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.entity.FollowRelationItem;
import io.grpc.Compressor;

public class NetUtil {


    public static String uploadMediaFile(String filePath, Context context){
        if(filePath != null)
        {
            //Firebase
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference();
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading to firebase...");
            progressDialog.show();
            final String[] resUrl = {null};
            StorageReference ref = storageReference.child("images/" + new File(filePath).getName());
            ref.putFile(Uri.fromFile(new File(filePath)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //get the url of the file we have uploaded
                                    Log.e("uri",uri.getPath());
                                    resUrl[0] = uri.getPath();
                                }
                            });
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            return resUrl[0];
        }
        return  null;
    }

    public static List<String> uploadMediaFiles(List<String> filePaths, Context context){
        if(filePaths != null && filePaths.size() != 0){

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading to firebase...");
            progressDialog.show();
            List<String> imageName = new ArrayList<>(filePaths.size());
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference();
            for(int i = 0; i < filePaths.size(); i ++){
                String uuid = UUID.randomUUID().toString();
                StorageReference ref = storageReference.child("images/" + uuid + ".jpg");
                imageName.add("images/" + uuid + ".jpg");
                int finalI = i;
                ref.putFile(Uri.fromFile(new File(filePaths.get(i)))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("uploaded: " + finalI + "/" + filePaths.size());

                    }
                }) .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
            progressDialog.dismiss();
            return imageName;
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
