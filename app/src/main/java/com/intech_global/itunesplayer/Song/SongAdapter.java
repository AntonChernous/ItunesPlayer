package com.intech_global.itunesplayer.Song;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.intech_global.itunesplayer.MainActivity;
import com.intech_global.itunesplayer.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anton on 28.07.2016.
 */
public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private MainActivity.ListStyle ls;
    private Context ctx;

    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();


    public SongAdapter(ArrayList<Song> songs, MainActivity.ListStyle ls, Context ctx) {
        this.songs = songs;
        this.ls = ls;
        this.ctx = ctx;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.media3)
                .showImageForEmptyUri(R.drawable.cross100)
                .showImageOnFail(R.drawable.cross100)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    //кол-во элементов
    @Override
    public int getCount() {
        return songs.size();
    }

    //элемент по позиции
    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    //Id по позиции
    @Override
    public long getItemId(int position) {
        //return position;
        return songs.get(position).ID;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        TextView textView2;
        ImageView img;

        if (convertView == null) {
            LayoutInflater inflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (ls){
                case List:
                    convertView=inflater.inflate(R.layout.list_item, parent, false);
                    break;
                case TableCol2:
                    convertView=inflater.inflate(R.layout.tablecol2_item, parent, false);
                    break;
                case TableCol3:
                    convertView=inflater.inflate(R.layout.tablecol3_item, parent, false);
                    break;
            }
        }

        Song s = songs.get(position);

        switch (ls){
            case List:
                textView=(TextView)convertView.findViewById(R.id.textViewTrackName);
                textView.setText(s.TrackName);

                textView2=(TextView)convertView.findViewById(R.id.textViewArtistName);
                textView2.setText(s.ArtistName);

                //Image
                img = (ImageView)convertView.findViewById(R.id.img);
                ImageLoader.getInstance().displayImage(s.ImgUrl, img, options, animateFirstListener);

                break;
            case TableCol2:
                textView=(TextView)convertView.findViewById(R.id.textViewTrackName);
                textView.setText(s.TrackName);

                //Image
                img = (ImageView)convertView.findViewById(R.id.img);
                ImageLoader.getInstance().displayImage(s.ImgUrl, img, options, animateFirstListener);

                break;
            case TableCol3:
                textView=(TextView)convertView.findViewById(R.id.textViewTrackName);
                textView.setText(s.TrackName);

                textView2=(TextView)convertView.findViewById(R.id.textViewArtistName);
                textView2.setText(s.ArtistName);

                //Image
                img = (ImageView)convertView.findViewById(R.id.img);
                ImageLoader.getInstance().displayImage(s.ImgUrl, img, options, animateFirstListener);
                break;
        }

        return convertView;
    }


    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}




