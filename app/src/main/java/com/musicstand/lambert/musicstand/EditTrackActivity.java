package com.musicstand.lambert.musicstand;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class EditTrackActivity extends AppCompatActivity {

    //Button saveBtn, cancelBtn;
    FloatingActionButton saveBtn;
    EditText trackTitle, trackArtist, trackAlbum;

    Track track = null;
    boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_track);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hasChanges = false;

        Intent i = getIntent();

        this.track = (Track) i.getSerializableExtra("track");

        setupFormFields();
        setupButtons();

        setTitle(this.track.toString());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    protected void setupFormFields() {
        trackTitle = (EditText) findViewById(R.id.track_title);
        trackArtist = (EditText) findViewById(R.id.track_artist);
        trackAlbum = (EditText) findViewById(R.id.track_album);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hasChanges = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        trackTitle.setText(track.getTitle());
        trackArtist.setText(track.getArtist());
        trackAlbum.setText(track.getAlbum());

        trackTitle.addTextChangedListener(watcher);
        trackArtist.addTextChangedListener(watcher);
        trackAlbum.addTextChangedListener(watcher);
    }

    protected void setupButtons() {
        saveBtn = (FloatingActionButton) findViewById(R.id.save_button);
        //cancelBtn = (Button) findViewById(R.id.cancel_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();

                String album = trackAlbum.getText().toString();
                String artist = trackArtist.getText().toString();
                String title = trackTitle.getText().toString();

                if ("".equals(title)) {
                    CoordinatorLayout   coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please enter a track title", Snackbar.LENGTH_SHORT);
                    View v = snackbar.getView();
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    v.setLayoutParams(params);
                    snackbar.show();
                } else {
                    resultIntent.putExtra("saved", new Track(album, artist, title));

                    // TODO Add extras or a data URI to this intent as appropriate.
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
