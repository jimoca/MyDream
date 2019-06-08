package com.example.thefinalexam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    ArrayList<Actress> ACTRESS_ITEMS = new ArrayList<>();

    int clickNum = 0;
    String sortStr = "";
    private final String TAG = "MainActivity";
    private Adapter mAdapter;
    private RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//----------------get database data------------------------------------------------------------
        getData(sortStr);

        mList = (RecyclerView) findViewById(R.id.recyclerViewTasks);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);


        mAdapter = new Adapter(this, ACTRESS_ITEMS);

        mList.setAdapter(mAdapter);

        //----------------------------------Click to Detail view----------------------------------------------


        mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener(){
            @Override
            public void onItemClick(View view , int position){

                Toast.makeText(MainActivity.this, ACTRESS_ITEMS.get(position).getName(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("Id",ACTRESS_ITEMS.get(position).getId());
                intent.putExtra("ActressName",ACTRESS_ITEMS.get(position).getName());
                intent.putExtra("ActressCup", ACTRESS_ITEMS.get(position).getCup());
                intent.putExtra("ActressHeight", ACTRESS_ITEMS.get(position).getHeight());
                intent.putExtra("ActressAge",ACTRESS_ITEMS.get(position).getAge());
                intent.putExtra("ImageUrl",ACTRESS_ITEMS.get(position).getPosterThumbnailUrl());
                startActivity(intent);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Uri ActressUri = Uri.parse("content://com.example.thefinalexam.ActressProvider/actress");
                getContentResolver().delete(ActressUri,"_id="+ACTRESS_ITEMS.get(position).getId(),null);
                ACTRESS_ITEMS.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mList);

        //---------------------------------Click to Question view----------------------------------------------
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ACTRESS_ITEMS.isEmpty()){
                    Intent QuestionIntent = new Intent(MainActivity.this, QuestionActivity.class);
                    startActivity(QuestionIntent);
                }else{
                    Toast.makeText(MainActivity.this, "請先新增女優!!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

           switch (id){
            case R.id.action_settings :
                Intent SettingIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(SettingIntent);
                break;
            case R.id.action_add :
                Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(addIntent);
                break;
            case R.id.action_sort :
                if(!ACTRESS_ITEMS.isEmpty()){
                    if(clickNum == 0){
                        clickNum++;
                        sortStr = "ActressName";
                    }else if(clickNum == 1){
                        clickNum++;
                        sortStr = "ActressCup";
                    }else if(clickNum == 2){
                        clickNum = 0;
                        sortStr = "ActressAge";
                    }
                    ACTRESS_ITEMS.clear();
                    mAdapter.notifyDataSetChanged();
                    getData(sortStr);
                }else{
                    Toast.makeText(MainActivity.this, "請先新增女優!!", Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData(String sortStr){
        Uri ActressUri = Uri.parse("content://com.example.thefinalexam.ActressProvider/actress");
        ContentValues contentValues = new ContentValues();
//        getContentResolver().delete(ActressUri,null,null);//  ----清除表格----
//        contentValues.put("ActressName", "三上優雅");
//        contentValues.put("ActressCup","z");
//        contentValues.put("ActressAge","20");
//        contentValues.put("PosterUrl","https://i.imgur.com/gn1XeZ2.jpg");
//        getContentResolver().insert(ActressUri, contentValues);
        Cursor ActressCursor = getContentResolver().query(ActressUri, new String[]{"_id", "ActressName","ActressCup","ActressAge","ActressHeight","PosterUrl"}, null, null, sortStr);
        if (ActressCursor != null) {
            while (ActressCursor.moveToNext()) {
                Log.e(TAG, "ID:" + ActressCursor.getInt(ActressCursor.getColumnIndex("_id"))
                        + "  ActressName:" + ActressCursor.getString(ActressCursor.getColumnIndex("ActressName"))
                        +"  ActressCup:"+ActressCursor.getString(ActressCursor.getColumnIndex("ActressCup"))
                        +"  ActressAge:"+ActressCursor.getString(ActressCursor.getColumnIndex("ActressAge"))
                        +"  ActressHeight:"+ActressCursor.getString(ActressCursor.getColumnIndex("ActressHeight")));
                ACTRESS_ITEMS.add(new Actress(ActressCursor.getString(ActressCursor.getColumnIndex("_id")),ActressCursor.getString(ActressCursor.getColumnIndex("ActressName")),ActressCursor.getString(ActressCursor.getColumnIndex("ActressCup")),ActressCursor.getString(ActressCursor.getColumnIndex("ActressHeight")),ActressCursor.getString(ActressCursor.getColumnIndex("ActressAge")),ActressCursor.getString(ActressCursor.getColumnIndex("PosterUrl"))));
            }

            ActressCursor.close();
        }
    }
}
