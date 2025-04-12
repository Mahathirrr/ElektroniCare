package com.example.imageview.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class FirebaseUtil {

    private static final String TAG = "FirebaseUtil";

    // Firebase Authentication
    public static FirebaseAuth getAuth() {
        try {
            return FirebaseAuth.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error getting FirebaseAuth instance", e);
            throw e;
        }
    }

    public static FirebaseUser getCurrentUser() {
        try {
            return getAuth().getCurrentUser();
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user", e);
            return null;
        }
    }

    public static String getCurrentUserId() {
        try {
            FirebaseUser user = getCurrentUser();
            return user != null ? user.getUid() : null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user ID", e);
            return null;
        }
    }

    public static boolean isLoggedIn() {
        try {
            return getCurrentUser() != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status", e);
            return false;
        }
    }

    // Firebase Firestore
    public static FirebaseFirestore getFirestore() {
        try {
            return FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error getting FirebaseFirestore instance", e);
            throw e;
        }
    }

    // Users Collection
    public static CollectionReference getUsersCollection() {
        try {
            return getFirestore().collection("users");
        } catch (Exception e) {
            Log.e(TAG, "Error getting users collection", e);
            throw e;
        }
    }

    public static DocumentReference getCurrentUserDocument() {
        try {
            String userId = getCurrentUserId();
            return userId != null ? getUsersCollection().document(userId) : null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user document", e);
            return null;
        }
    }

    public static DocumentReference getUserDocument(String userId) {
        try {
            return getUsersCollection().document(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user document", e);
            return null;
        }
    }

    // Repair Requests Collection
    public static CollectionReference getRequestsCollection() {
        try {
            return getFirestore().collection("repair_requests");
        } catch (Exception e) {
            Log.e(TAG, "Error getting requests collection", e);
            throw e;
        }
    }

    public static DocumentReference getRequestDocument(String requestId) {
        try {
            return getRequestsCollection().document(requestId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting request document", e);
            return null;
        }
    }

    // Offers Collection
    public static CollectionReference getOffersCollection() {
        try {
            return getFirestore().collection("offers");
        } catch (Exception e) {
            Log.e(TAG, "Error getting offers collection", e);
            throw e;
        }
    }

    public static DocumentReference getOfferDocument(String offerId) {
        try {
            return getOffersCollection().document(offerId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting offer document", e);
            return null;
        }
    }

    // Repairs Collection
    public static CollectionReference getRepairsCollection() {
        try {
            return getFirestore().collection("repairs");
        } catch (Exception e) {
            Log.e(TAG, "Error getting repairs collection", e);
            throw e;
        }
    }

    public static DocumentReference getRepairDocument(String repairId) {
        try {
            return getRepairsCollection().document(repairId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting repair document", e);
            return null;
        }
    }

    // Firebase Storage
    public static FirebaseStorage getStorage() {
        try {
            return FirebaseStorage.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Error getting FirebaseStorage instance", e);
            throw e;
        }
    }

    public static StorageReference getStorageReference() {
        try {
            return getStorage().getReference();
        } catch (Exception e) {
            Log.e(TAG, "Error getting storage reference", e);
            return null;
        }
    }

    public static StorageReference getRequestPhotosReference(String requestId) {
        try {
            return getStorageReference().child("request_photos").child(requestId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting request photos reference", e);
            return null;
        }
    }

    public static StorageReference getProfileImagesReference() {
        try {
            return getStorageReference().child("profile_images");
        } catch (Exception e) {
            Log.e(TAG, "Error getting profile images reference", e);
            return null;
        }
    }
}
