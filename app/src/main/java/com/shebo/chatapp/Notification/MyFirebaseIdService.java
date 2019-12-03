package com.shebo.chatapp.Notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String referenceToken = FirebaseInstanceId.getInstance().getToken();
        if (currentUser != null){
            updateToken(referenceToken);
        }
    }

    private void updateToken(String referenceToken) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token = new Token(referenceToken);

        reference.child(currentUser.getUid()).setValue(token);
    }
}