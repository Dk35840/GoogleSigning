package dk35840.googlesigning1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private Button signoutButton;
    private Spinner spinner2;
    EditText mName;

    private Button goHome;

    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
private UserInformation userinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Log.d("dk","Account Activity called");
        addItemsOnSpinner2();
        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mAuth=FirebaseAuth.getInstance();
        signoutButton= (Button) findViewById(R.id.GoogleSignout);
        goHome= (Button) findViewById(R.id.ButtonSend);
        mName=(EditText)findViewById(R.id.FirstName_id);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(AccountActivity.this,MainActivity.class));
                }
            }
        };

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("dk","goHOme called");


                String name = mName.getText().toString();
                String address = spinner2.getSelectedItem().toString();
                    userinfo=new UserInformation(name,address);

                Log.d("dk", "onClick: Attempting to submit to database: \n" +
                        "name: " + name + "\n" +
                        "address: " + address + "\n"
                );

                //handle the exception if the EditText fields are null
                if(!name.equals("") ){
                    UserInformation userInformation = new UserInformation(name,address);
                    myRef.child("users").child(userID).setValue(userInformation);

                    mName.setText(name);

                }else{
                    Toast.makeText(AccountActivity.this,"Fill out all the fields",Toast.LENGTH_LONG).show();
                }



                Intent intent = new Intent(AccountActivity.this,HomeScreen.class);
                intent.putExtra("user",userinfo);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void addItemsOnSpinner2() {

        spinner2 = (Spinner) findViewById(R.id.spinnerId);
        List<String> list = new ArrayList<String>();
        list.add("449 Palo Verde Road, Gaineville, Fl");
        list.add("6731 Thompson Street, Gaineville, Gl");
        list.add("8771 Thomas Boulevard, Orlando,Fl");
        list.add("1234 Verano Place, Orlando, Fl");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

}
