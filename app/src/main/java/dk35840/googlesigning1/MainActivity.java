package dk35840.googlesigning1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
        SignInButton googleButton;
   private int RC_SIGN_IN=1;
    DatabaseReference userRef;

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;





    private static String TAG="Main_Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("dk","main on create called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleButton=(SignInButton) findViewById(R.id.googleSignInbuttonId);
        mAuth=FirebaseAuth.getInstance();
       final ProgressDialog progressDialog = new ProgressDialog(this);  //progress dialog

      //  loginProgress= (ProgressBar) findViewById(R.id.login_progress);
        authStateListener =new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    progressDialog.setMessage("Registering Please Wait...");
                    progressDialog.show();

                    Toast.makeText(MainActivity.this, "Successfully logged in as " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();

                   // loginProgress.setVisibility(View.INVISIBLE);



                  //  mcurrentUser=mAuth.getCurrentUser();

                    userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                    Log.d("dk","userref mains-"+userRef);
                  //  loginProgress.setVisibility(View.VISIBLE);

                    userRef.addValueEventListener(new ValueEventListener() {




                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Log.d("dk","main   User info retrieve called");


                            UserInformation user = dataSnapshot.getValue(UserInformation.class);
                            Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                            intent.putExtra("user",user);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                          //  Log.d("dk",user.getName());




                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                }

            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this,"You Got Error",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

            googleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);



    }




    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);  //progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           // updateUI(user);
                            Log.d("dk","main-call to activity acc"+userRef);

                            Intent intent = new Intent(MainActivity.this,AccountActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
