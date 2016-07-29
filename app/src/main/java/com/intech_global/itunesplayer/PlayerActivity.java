package com.intech_global.itunesplayer;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    private ImageView imageViewImg;
    private TextView textViewTrackName;
    private ToggleButton playPauseButton;
    private SeekBar musicSeekBar;

    private DisplayImageOptions options;

    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private String sPreviewUrl;

    private boolean isLoaded=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findViews();

        String sTrackName = getIntent().getStringExtra(MainActivity.prefixID+"TrackName");
        String sImgUrl = getIntent().getStringExtra(MainActivity.prefixID+"ImgUrl");
        sPreviewUrl = getIntent().getStringExtra(MainActivity.prefixID+"PreviewUrl");

        textViewTrackName.setText(sTrackName);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.media3)
                .showImageForEmptyUri(R.drawable.cross100)
                .showImageOnFail(R.drawable.cross100)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();

        ImageLoader.getInstance().displayImage(sImgUrl, imageViewImg, options);

        mediaPlayer = new MediaPlayer();


    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-07-29 16:44:50 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        imageViewImg = (ImageView)findViewById( R.id.imageViewImg );
        textViewTrackName = (TextView)findViewById( R.id.textViewTrackName );
        playPauseButton = (ToggleButton)findViewById( R.id.playPauseButton );
        musicSeekBar = (SeekBar)findViewById( R.id.musicSeekBar );
    }


    public void PlayPauseClick(View view) {
        if (playPauseButton.isChecked()) { // Checked - Pause icon
            start();
        } else { // Unchecked - Play icon
            pause();
        }
    }

    private void start() {
        if (isLoaded){
            //Стоит на паузе. Продолжаем
            mediaPlayer.start();
            startPlayProgressUpdater();
        }else {
            //1ый раз играем

            try {
                mediaPlayer.setDataSource(sPreviewUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        }
    }

    private void pause() {
        mediaPlayer.pause();
    }
    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        isLoaded=true;

        musicSeekBar.setMax(mediaPlayer.getDuration());
        musicSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mediaPlayer.seekTo(musicSeekBar.getProgress());

                return false;
            }
        });

        mediaPlayer.start();
        startPlayProgressUpdater();
    }

    private void startPlayProgressUpdater() {
        musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification,1000);
        }else{
            //musicSeekBar.setProgress(0);
            //playPauseButton.setChecked(false);
        }
    }
}
