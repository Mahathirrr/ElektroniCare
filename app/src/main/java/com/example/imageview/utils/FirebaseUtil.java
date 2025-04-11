package com.example.imageview.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class FirebaseUtil {

    // Firebase Authentication
    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    // Firebase Firestore
    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    // Users Collection
    public static CollectionReference getUsersCollection() {
        return getFirestore().collection("users");
    }

    public static DocumentReference getCurrentUserDocument() {
        String userId = getCurrentUserId();
        return userId != null ? getUsersCollection().document(userId) : null;
    }

    public static DocumentReference getUserDocument(String userId) {
        return getUsersCollection().document(userId);
    }

    // Repair Requests Collection
    public static CollectionReference getRequestsCollection() {
        return getFirestore().collection("repair_requests");
    }

    public static DocumentReference getRequestDocument(String requestId) {
        return getRequestsCollection().document(requestId);
    }

    // Offers Collection
    public static CollectionReference getOffersCollection() {
        return getFirestore().collection("offers");
    }

    public static DocumentReference getOfferDocument(String offerId) {
        return getOffersCollection().document(offerId);
    }

    // Repairs Collection
    public static CollectionReference getRepairsCollection() {
        return getFirestore().collection("repairs");
    }

    public static DocumentReference getRepairDocument(String repairId) {
        return getRepairsCollection().document(repairId);
    }

    // Firebase Storage
    public static FirebaseStorage getStorage() {
        return FirebaseStorage.getInstance();
    }

    public static StorageReference getStorageReference() {
        return getStorage().getReference();
    }

    public static StorageReference getRequestPhotosReference(String requestId) {
        return getStorageReference().child("request_photos").child(requestId);
    }

    public static StorageReference getProfileImagesReference() {
        return getStorageReference().child("profile_images");
    }
}
