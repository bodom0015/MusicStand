package com.musicstand.lambert.musicstand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mike on 7/4/2016.
 */
public class TracksAdapter extends ArrayAdapter<Track> {
    public TracksAdapter(Context context, ArrayList<Track> tracks) {
        super(context, 0, tracks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Track track = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_track, parent, false);
        }
        // Lookup view for data population
        TextView tvTrackTitle = (TextView) convertView.findViewById(R.id.tvTrackTitle);
        TextView tvTrackArtist = (TextView) convertView.findViewById(R.id.tvTrackArtist);
        // Populate the data into the template view using the data object
        tvTrackTitle.setText(track.getTitle());
        tvTrackArtist.setText(track.getArtist());
        // Return the completed view to render on screen
        return convertView;
    }


}
