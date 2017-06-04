package com.musicstand.lambert.musicstand;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    private ArrayList<Track> items;
    private ArrayAdapter<Track> itemsAdapter;
    private Track selectedTrack = null;
    private ListView lvItems;

    private final static String SAVE_FILE = "musicstand.txt";
    private final static int EDIT_TRACK_ACTIVITY = 1;
    private File libraryFile;

    private void readItems(final File file) {
        if (file.exists()) {
            String library = "";
            try {
                // items = new ArrayList<String>(FileUtils.readLines(file));
                library = FileUtils.readFileToString(file);

            } catch (IOException e) {
                System.err.println("Failed to read " + file.getAbsolutePath());
                e.printStackTrace();
            }

            String[] lines = library.split("\n");
            for (String line : lines) {
                //items.add(line);
                String[] fields = line.split(",");
                switch (fields.length) {
                    case 3:
                        items.add(new Track(fields[2], fields[0], fields[1]));
                        break;
                    case 1:
                        items.add(new Track(fields[0]));
                        break;
                    case 2:
                    default:
                        items.add(new Track(fields[0], fields[1]));
                        break;
                }
            }
        }
    }

    private void writeItems(final File file) {
        //FileUtils.writeLines(file, items);
        final ArrayList<String> lines = new ArrayList<>();
        for (Track item : items) {
            lines.add(item.getArtist() + "," +  item.getTitle() + "," + item.getAlbum());
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println("Failed to write " + file.getAbsolutePath());
            e.printStackTrace(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();

        libraryFile = new File(getFilesDir(), SAVE_FILE);

        readItems(libraryFile); // <---- Add this line

        itemsAdapter = new TracksAdapter(this, items);

        lvItems.setAdapter(itemsAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupListViewListener();

        setupFab();
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Grab the name from our textbox
                EditText etSongName = (EditText) findViewById(R.id.etSongName);
                String songName = etSongName.getText().toString();

                // Ignore empty item
                if (songName.equals("")) {
                    Snackbar.make(view, "Please enter a track title", Snackbar.LENGTH_SHORT).show();
                } else {
                    EditText etSongArtist = (EditText) findViewById(R.id.etSongArtist);
                    String songArtist = etSongArtist.getText().toString();

                    // Create a new item from it
                    final Track newTrack = new Track(songArtist, songName);
                    itemsAdapter.add(newTrack);

                    etSongName.setText("");
                    etSongArtist.setText("");
                    writeItems(libraryFile); // <---- Add this line

                    final String message = "Added " + ("".equals(songArtist) ? songName : songArtist + ": " + songName);
                    final Snackbar notification = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
                    notification.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemsAdapter.remove(newTrack);
                            notification.dismiss();
                        }
                    });
                    notification.show();
                }
            }
        });
    }

    // Attaches a long click listener to the listview
    private void setupListViewListener() {

        //registerForContextMenu(lvItems);

        lvItems.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
                selectedTrack = (Track) lvItems.getItemAtPosition(acmi.position);

                menu.add("Details");
                menu.add("Remove");
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                selectedTrack = items.get(pos);
                editTrack(selectedTrack);
            }
        });
    }

    private void editTrack(Track track) {
        //Starting a new Intent
        Intent editTrackActivity = new Intent(getApplicationContext(), EditTrackActivity.class);

        editTrackActivity.putExtra("track", track);

        Log.e("n", track.toString());
        startActivityForResult(editTrackActivity, EDIT_TRACK_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (EDIT_TRACK_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    // Extract the data returned from the child Activity.
                    Serializable s = data.getSerializableExtra("saved");

                    if (s != null) {
                        Track savedTrack = (Track) s;
                        selectedTrack.merge(savedTrack);
                        itemsAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
        }
    }

    private void deleteTrack(final MenuItem item) {
        final int id = item.getItemId();

        itemsAdapter.remove(selectedTrack);
        writeItems(libraryFile);
    }

    private void exportAction() {
        FileChooser sfd = new FileChooser(this);
        sfd.setFileListener(new FileChooser.FileSelectedListener() {
            @Override public void fileSelected(final File file) {
                writeItems(file);
            }
        });
        sfd.showDialog();
    }

    private void importAction() {
        FileChooser ofd = new FileChooser(this);
        ofd.setFileListener(new FileChooser.FileSelectedListener() {
            @Override public void fileSelected(final File file) {
                readItems(file);
            }
        });

        ofd.showDialog();
    }

    private void settingsAction() {
        Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);

        startActivity(settingsActivity);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if(item.getTitle()=="Details"){
            editTrack(selectedTrack);
        } else if(item.getTitle()=="Remove") {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            deleteTrack(item);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_library, menu);

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

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_settings:
                settingsAction();
                break;
            // action with ID action_import was selected
            case R.id.action_import:
                importAction();
                break;
            // action with ID action_export was selected
            case R.id.action_export:
                exportAction();
                break;
            default:
                break;
        }

        return true;

        //return super.onOptionsItemSelected(item);
    }
}
