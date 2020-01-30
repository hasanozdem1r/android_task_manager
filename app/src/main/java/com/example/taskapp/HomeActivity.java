package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton fabBtn;
    //Firebase Objects
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //Recycler View
    private RecyclerView recyclerView;

    //update input field
    private EditText titleUp,noteUp;
    private Button btnDeleteUp,btnUpdateUp,backBtn;

    //variable
    private String title,note,post_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");
        //Firebase Initialization
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String userId=mUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("TaskNote").child(userId);

        mDatabase.keepSynced(true);

        //Recycler ...
        recyclerView=findViewById(R.id.recyclerParent);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn=findViewById(R.id.fabBtn);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);
                View myView=inflater.inflate(R.layout.custominputfield,null);
                myDialog.setView(myView);
                final AlertDialog dialog=myDialog.create();

                final EditText title=myView.findViewById(R.id.editTitle);
                final EditText note=myView.findViewById(R.id.editNote);
                Button buttonSave=myView.findViewById(R.id.butonSave);

                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mTitle=title.getText().toString().trim();
                        String mNote=note.getText().toString().trim();

                        if(TextUtils.isEmpty(mTitle)){
                            title.setError("Required Field !!");
                            return;
                        }
                        if(TextUtils.isEmpty(mNote)){
                            title.setError("Required Field !!");
                            return;
                        }

                        String id=mDatabase.push().getKey();
                        String date= DateFormat.getDateInstance().format(new Date());
                        Data data=new Data(mTitle,mNote,date,id);
                        mDatabase.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder,final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());

                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=getRef(position).getKey();
                        title=model.getTitle();
                        note=model.getNote();
                        updateData();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView=itemView;

        }
        public void setTitle(String title1){
            TextView mTitle=myView.findViewById(R.id.titleViewItem);
            mTitle.setText(title1);
        }
        public  void setNote(String note1){
            TextView mNote=myView.findViewById(R.id.noteViewItem);
            mNote.setText(note1);
        }
        public void setDate(String date1){
            TextView mDate=myView.findViewById(R.id.dateViewItem);
            mDate.setText(date1);

        }

    }

    public void updateData(){
        AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);

        View myView=inflater.inflate(R.layout.updateinputfield,null);
        myDialog.setView(myView);

        final AlertDialog dialog=myDialog.create();
        titleUp=myView.findViewById(R.id.editTitleUpdate);
        noteUp=myView.findViewById(R.id.editNoteUpdate);

        titleUp.setText(title);
        titleUp.setSelection(title.length());

        noteUp.setText(note);
        noteUp.setSelection(note.length());

        btnDeleteUp=myView.findViewById(R.id.butonDeleteUp);
        btnUpdateUp=myView.findViewById(R.id.butonUpdateUp);

        btnUpdateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=titleUp.getText().toString().trim();
                note=noteUp.getText().toString().trim();

                String mDate=DateFormat.getDateInstance().format(new Date());
                Data data=new Data(title,note,mDate,post_key);
                mDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDeleteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutItem:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
