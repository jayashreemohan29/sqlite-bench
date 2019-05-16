package com.example.sony.myapplication;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.Random;



public class MainActivity extends AppCompatActivity {


    long update_elapsed_time=0;
    long insert_elapsed_time=0;
    long delete_elapsed_time=0;
    long all_elapsed_time=0;




    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText tnum=(EditText)findViewById(R.id.tnum); //num of tx
        final EditText jsize=(EditText)findViewById(R.id.jsize); //journal size
        final EditText checknum=(EditText)findViewById(R.id.checknum);
        final Button insert=(Button) findViewById(R.id.insert);
        final Button update=(Button) findViewById(R.id.update);
        final Button delete=(Button) findViewById(R.id.delete);
        final Button all=(Button) findViewById(R.id.all);
        final RadioButton wal=(RadioButton) findViewById(R.id.wal); //wal mode
        final RadioButton trunc=(RadioButton) findViewById(R.id.trunc); //delete mode
        final RadioButton joff=(RadioButton) findViewById(R.id.journaloff); //off
        final RadioButton full=(RadioButton) findViewById(R.id.full);
        final RadioButton normal=(RadioButton) findViewById(R.id.normal);
        final RadioButton soff=(RadioButton) findViewById(R.id.sqloff);

        final RadioGroup jmode = (RadioGroup)findViewById(R.id.maptype); //journal mode
        final RadioGroup smode = (RadioGroup)findViewById(R.id.maptype1); //sync mode

        final DBAdapter db = new DBAdapter(MainActivity.this);



        class UpdateThread implements Runnable {

            @Override
            public void run() {


                final String jstring = ((RadioButton) findViewById(jmode.getCheckedRadioButtonId())).getText().toString();
                final String sstring = ((RadioButton) findViewById(smode.getCheckedRadioButtonId())).getText().toString();
                // final int jid= ((RadioButton)findViewById(jmode.getCheckedRadioButtonId())).getId();
                // final int sid=((RadioButton)findViewById(smode.getCheckedRadioButtonId())).getId();
                final String tnumtext = tnum.getText().toString();
                final String jsizetext = jsize.getText().toString();
                final String checktext = checknum.getText().toString();
                //     Snackbar.make(view, tnumtext + "  " + jsizetext + " " + jstring + " " + sstring, Snackbar.LENGTH_LONG)
                //            .setAction("Action", null).show();

                String jmodestr;
                if (jstring.equals(wal.getText().toString()))
                    jmodestr = "WAL";

                else if (jstring.equals(trunc.getText().toString()))
                    jmodestr = "TRUNCATE";

                else
                    jmodestr = "DELETE";


                String smodestr;
                if (sstring.equals(full.getText().toString()))
                    smodestr = "FULL";

                else if (sstring.equals(normal.getText().toString()))
                    smodestr = "NORMAL";

                else
                    smodestr = "OFF";


                db.open();
                //.setpage();
                db.setJournalMode(jmodestr, smodestr); //set jmode and smode

                final int tnumint = Integer.parseInt(tnumtext);
                final int jsizeint = Integer.parseInt(jsizetext);
                final int chp_after_tx = Integer.parseInt(checktext);

                if(jmodestr.equals("WAL")){
                    db.setjournalsize(jsizetext);
                }

                Random randomGenerator = new Random();
                int num_tx = 0;

                Cursor c = db.getAllRows();
                if (c.moveToFirst()) {
                    c.moveToLast();
                    num_tx = Integer.parseInt(c.getString(0));
                }
                c.close();


                long startTime = System.currentTimeMillis();
                int count = 0;
                for (int i = 1; i < tnumint + 1; i++) {
                    int m = randomGenerator.nextInt(num_tx); //update
                    db.updateRow(m);
                    count++;
                    if (count == chp_after_tx) {
                         db.checkpoint();
                        count = 0;
                    }

                }

                if (count != 0)
                      db.checkpoint();

                    if (chp_after_tx == -1)
                         db.checkpoint();

                        db.close();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = (stopTime - startTime);
                update_elapsed_time=elapsedTime;



            }
        }



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateThread t=new UpdateThread();
                Thread update_thread= new Thread(t);
                update_thread.start();
                try {
                    update_thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getBaseContext(),  Long.toString(update_elapsed_time) + "ms \n", Toast.LENGTH_LONG).show();
               // deleteCache(getBaseContext());

            }

        });


        class InsertThread implements Runnable {

            @Override
            public void run() {
                final String jstring = ((RadioButton) findViewById(jmode.getCheckedRadioButtonId())).getText().toString();
                final String sstring = ((RadioButton) findViewById(smode.getCheckedRadioButtonId())).getText().toString();
                // final int jid= ((RadioButton)findViewById(jmode.getCheckedRadioButtonId())).getId();
                // final int sid=((RadioButton)findViewById(smode.getCheckedRadioButtonId())).getId();
                final String tnumtext = tnum.getText().toString();
                final String jsizetext = jsize.getText().toString();
                final String checktext = checknum.getText().toString();
                //jsize=no. of tx after which checkpoint is done
               // Snackbar.make(view, tnumtext + "  " + jsizetext + " " + jstring + " " + sstring, Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                String jmodestr;
                if (jstring.equals(wal.getText().toString()))
                    jmodestr = "WAL";

                else if (jstring.equals(trunc.getText().toString()))
                    jmodestr = "TRUNCATE";

                else
                    jmodestr = "DELETE";


                String smodestr;
                if (sstring.equals(full.getText().toString()))
                    smodestr = "FULL";

                else if (sstring.equals(normal.getText().toString()))
                    smodestr = "NORMAL";

                else
                    smodestr = "OFF";


                db.open();
               // db.setpage();

                db.setJournalMode(jmodestr, smodestr); //set jmode and smode

                final int tnumint = Integer.parseInt(tnumtext);
                final int jsizeint = Integer.parseInt(jsizetext);
                final int chp_after_tx = Integer.parseInt(checktext);

                if(jmodestr.equals("WAL")){
                    db.setjournalsize(jsizetext);
                }

                long startTime = System.currentTimeMillis();

                int count = 0;
                for (int i = 0; i < tnumint; i++) {
                    db.insertRow();
                    count++;
                    if (count == chp_after_tx) {
                        db.checkpoint();
                        count = 0;
                    }
                }
                 if (count!=0)
                     db.checkpoint();

                if (chp_after_tx == -1)
                    db.checkpoint();

                    db.close();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = (stopTime - startTime);
                insert_elapsed_time=elapsedTime;


            }


        }





        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertThread t=new InsertThread();
                Thread insert_thread= new Thread(t);
                insert_thread.start();
                try {
                    insert_thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getBaseContext(),  Long.toString(insert_elapsed_time) + "ms \n", Toast.LENGTH_LONG).show();
               // deleteCache(getBaseContext());
            }
        });








        class DeleteThread implements Runnable {

            @Override
            public void run() {

                final String jstring = ((RadioButton) findViewById(jmode.getCheckedRadioButtonId())).getText().toString();
                final String sstring = ((RadioButton) findViewById(smode.getCheckedRadioButtonId())).getText().toString();
                // final int jid= ((RadioButton)findViewById(jmode.getCheckedRadioButtonId())).getId();
                // final int sid=((RadioButton)findViewById(smode.getCheckedRadioButtonId())).getId();
                final String tnumtext = tnum.getText().toString();
                final String jsizetext = jsize.getText().toString();
                final String checktext = checknum.getText().toString();
                //jsize=no. of tx after which checkpoint is done
                //  Snackbar.make(view, tnumtext+ "  "+ jsizetext+ " "+ jstring + " " + sstring , Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                String jmodestr;
                if (jstring.equals(wal.getText().toString()))
                    jmodestr = "WAL";

                else if (jstring.equals(trunc.getText().toString()))
                    jmodestr = "TRUNCATE";

                else
                    jmodestr = "DELETE";


                String smodestr;
                if (sstring.equals(full.getText().toString()))
                    smodestr = "FULL";

                else if (sstring.equals(normal.getText().toString()))
                    smodestr = "NORMAL";

                else
                    smodestr = "OFF";


                db.open();
                //db.setpage();


                db.setJournalMode(jmodestr, smodestr); //set jmode and smode

                final int tnumint = Integer.parseInt(tnumtext);
                final int jsizeint = Integer.parseInt(jsizetext);
                final int chp_after_tx = Integer.parseInt(checktext);

                if(jmodestr.equals("WAL")){
                    db.setjournalsize(jsizetext);
                }




                Random randomGenerator = new Random();
                int num_tx = 0;

                Cursor c = db.getAllRows();
                if (c.moveToFirst()) {
                    c.moveToLast();
                    num_tx = Integer.parseInt(c.getString(0));
                }

                c.close();

                long startTime = System.currentTimeMillis();
                String k = "";
                int count = 0;

                for (int i = 1; i < tnumint + 1; i++) {
                    int m = randomGenerator.nextInt(num_tx);
                    db.deleteRow(m);
                    count++;

                    if (count == chp_after_tx) {
                        db.checkpoint();
                        count = 0;
                    }

                }

                if (count != 0)
                     db.checkpoint();

                    if (chp_after_tx == -1)
                            db.checkpoint();

                        db.close();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = (stopTime - startTime);
                delete_elapsed_time = elapsedTime;


            }

        }


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteThread t=new DeleteThread();
                Thread delete_thread= new Thread(t);
                delete_thread.start();
                try {
                    delete_thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getBaseContext(),  Long.toString(delete_elapsed_time) + "ms \n", Toast.LENGTH_LONG).show();
               // deleteCache(getBaseContext());
            }
        });






        class AllThread implements Runnable {

            @Override
            public void run() {

                final String jstring = ((RadioButton) findViewById(jmode.getCheckedRadioButtonId())).getText().toString();
                final String sstring = ((RadioButton) findViewById(smode.getCheckedRadioButtonId())).getText().toString();
                // final int jid= ((RadioButton)findViewById(jmode.getCheckedRadioButtonId())).getId();
                // final int sid=((RadioButton)findViewById(smode.getCheckedRadioButtonId())).getId();
                final String tnumtext = tnum.getText().toString();
                final String jsizetext = jsize.getText().toString();
                //jsize=no. of tx after which checkpoint is done
                //  Snackbar.make(view, tnumtext+ "  "+ jsizetext+ " "+ jstring + " " + sstring , Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                String jmodestr;
                if (jstring.equals(wal.getText().toString()))
                    jmodestr = "WAL";

                else if (jstring.equals(trunc.getText().toString()))
                    jmodestr = "TRUNCATE";

                else
                    jmodestr = "DELETE";


                String smodestr;
                if (sstring.equals(full.getText().toString()))
                    smodestr = "FULL";

                else if (sstring.equals(normal.getText().toString()))
                    smodestr = "NORMAL";

                else
                    smodestr = "OFF";


                db.open();
                //db.setpage();

                final String journalmode = db.setJournalMode(jmodestr, smodestr);

                final int tnumint = Integer.parseInt(tnumtext);
                final int chp_after_tx = Integer.parseInt(jsizetext);

                Random randomGenerator = new Random();
                int num_tx = 0;

                Cursor c = db.getAllRows();
                if (c.moveToFirst()) {
                    c.moveToLast();
                    num_tx = Integer.parseInt(c.getString(0));
                }

                c.close();

                int count = 0;
                long startTime = System.currentTimeMillis();
                String k = "";

                //insert tnumint tx
                for (int i = 0; i < tnumint; i++) {
                    db.insertRow();
                    count++;
                    if (count == chp_after_tx) {
                        db.checkpoint();
                        count = 0;
                    }
                }
                if (count!=0)
                    db.checkpoint();

                if (chp_after_tx == -1)
                    db.checkpoint();



                //update tnumint/10+1 tx changed to tnumint
                count = 0;
                for (int i = 1; i < tnumint/10+1; i++) {
                    int m = randomGenerator.nextInt(num_tx); //update
                    db.updateRow(m);
                    count++;
                    if (count == chp_after_tx) {
                        db.checkpoint();
                        count = 0;
                    }
                }

                if (count != 0)
                    db.checkpoint();

                if (chp_after_tx == -1)
                    db.checkpoint();


                //delete tnumint/10 +1 to tnumint tx
                count=0;
                for (int i = 1; i < tnumint/10+1; i++) {
                    int m = randomGenerator.nextInt(num_tx);
                    db.deleteRow(m);
                    count++;

                    if (count == chp_after_tx) {
                        db.checkpoint();
                        count = 0;
                    }
                }

                if (count != 0)
                    db.checkpoint();

                if (chp_after_tx == -1)
                    db.checkpoint();


                db.close();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = (stopTime - startTime);
                all_elapsed_time = elapsedTime;
            }
        }

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllThread t=new AllThread();
                Thread all_thread= new Thread(t);
                all_thread.start();
                try {
                    all_thread.join();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getBaseContext(),  Long.toString(all_elapsed_time) + "ms \n", Toast.LENGTH_LONG).show();
               // deleteCache(getBaseContext());
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



