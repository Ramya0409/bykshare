package com.example.bykshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    protected static final String TAG = "MarkerInfoWindowAdapter";

    private final View infowindow;
    private Context context;
    private TextView _title, _snippet;

    public MarkerInfoWindowAdapter(Context context) {
        this.context = context;
        infowindow = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null);
    }

    private void renderInfoWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        String snippet = marker.getSnippet();

        _title = (TextView) view.findViewById(R.id.title);
        _snippet = (TextView) view.findViewById(R.id.snippet);

        if(! title.equals("")){
            _title.setText(title);
        }
        if (! snippet.equals("")){
            _snippet.setText(snippet);
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        renderInfoWindowText(marker, infowindow);
        return infowindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        renderInfoWindowText(marker, infowindow);
        return infowindow;
    }
}
