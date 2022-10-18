package comp5216.sydney.edu.au.afinal.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


import comp5216.sydney.edu.au.afinal.entity.EventEntity;
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
            List<String> resUrlList = new ArrayList<>(filePaths.size());
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference();
            for(int i = 0; i < filePaths.size(); i ++){
                StorageReference ref = storageReference.child("images/" + new File(filePaths.get(i)).getName());
                File file = new File(filePaths.get(i));

                int finalI = i;
                ref.putFile(Uri.fromFile(new File(filePaths.get(i)))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setMessage("uploaded: " + finalI + "/" + filePaths.size());
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //get the url of the file we have uploaded
                                Log.e("uri",uri.getPath());
                                resUrlList.add(uri.getPath());

                            }
                        });
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
            return resUrlList;
        }
        return null;
    }

    public static void uploadEvent(EventEntity event){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference ref = firebaseFirestore.collection("Events");
        ref.add(event);
    }

    public static void setEvent(EventEntity event, String documentID){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference ref = firebaseFirestore.collection("Events").document(documentID);
        ref.set(event);
    }









}
