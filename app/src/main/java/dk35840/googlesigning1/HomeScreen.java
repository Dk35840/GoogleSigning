package dk35840.googlesigning1;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class HomeScreen extends AppCompatActivity {

    //DECLARE ALL THE FIELDS
    EditText roomName;
    String address;
    private UserInformation user;
    Button createRoom;
    ListView roomList;
    ArrayList<String> roomArrayListt;
    ArrayAdapter<String> roomAdapter;

    //DATABASE REFERENCE FIELD FOR FIREBASE
   DatabaseReference databaseReference3;
    DatabaseReference databaseReference2;
    private String userName;
    private FirebaseAuth mAuth1;
    private FirebaseUser mcurrentUser;

        private TextView homeTitle;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("dk","HOme Screen on create called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //DEFINE REFERENCES FOR ALL THE VIEWS
        roomName = (EditText) findViewById(R.id.editText);
        createRoom = (Button) findViewById(R.id.button3);
        roomList = (ListView) findViewById(R.id.roomListView);


        homeTitle= (TextView) findViewById(R.id.homeTitle);
        mAuth1 = FirebaseAuth.getInstance();
        mcurrentUser=mAuth1.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(HomeScreen.this,MainActivity.class));
                }
            }
        };


        //Get intent extras


        user = getIntent().getExtras().getParcelable("user");


        try {
            userName=user.getName();



            address=user.getAddress();


        } catch (Exception e) {
            e.printStackTrace();


            startActivity(new Intent(HomeScreen.this,AccountActivity.class));

        }




        homeTitle.setText(address);
        homeTitle.setTextSize(18);



        roomArrayListt = new ArrayList<String>();
        roomAdapter = new ArrayAdapter<String>(HomeScreen.this, android.R.layout.simple_list_item_1, roomArrayListt);

        roomList.setAdapter(roomAdapter);







        databaseReference2 = FirebaseDatabase.getInstance().getReference();






        //CREATE AN ENTRY FOR NEW ROOM IN FIREBASE DATABASE ON BUTTON CLICK
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(roomName.getText().toString(), "");
                //   databaseReference.updateChildren(map);


                databaseReference2.child("Database").child(address).updateChildren(map);

               ;
            }
        });



            databaseReference3 = databaseReference2.child("Database").child(address);  //must change to addee
            Log.d("dk","data ref 3 callled"+databaseReference3);
            databaseReference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Iterator iterator = dataSnapshot.getChildren().iterator();

                    Set<String> set = new HashSet<String>();

                    while (iterator.hasNext()) {

                        //GET NAMES OF ALL THE ROOMS ONE BY ONE FROM YOUR DATABASE
                        set.add((String) ((DataSnapshot) iterator.next()).getKey());

                    }

                    roomArrayListt.clear();
                    roomArrayListt.addAll(set);

                    roomAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(HomeScreen.this, Chat_room.class);
                intent.putExtra("Room_name", ((TextView) view).getText().toString());
                Log.d("dk", "Roomo_name put" + ((TextView) view).getText().toString());
                intent.putExtra("User_name",userName);
                intent.putExtra("ADDRESS",address);

                startActivity(intent);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("dk","HOme on start ");
        mAuth1.addAuthStateListener(mAuthListener);



    }}
