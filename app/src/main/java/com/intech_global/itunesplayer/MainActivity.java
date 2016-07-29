package com.intech_global.itunesplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.intech_global.itunesplayer.Song.Song;
import com.intech_global.itunesplayer.Song.SongAdapter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String prefixID = "com.intech_global.itunesplayer.";

    public enum ListStyle{
        List,
        TableCol2,
        TableCol3
    }

    private ListStyle mListStyle = ListStyle.List;

    private ArrayList<Song> songs = new ArrayList<Song>();

    private GetSongsAsyncTask mGetSongsAsyncTask;
    private SongAdapter mSongsAdapter=null;


    private EditText editTextSearch;
    private RadioGroup toggle;
    private RadioButton listStyle;
    private RadioButton tableStyle;
    private ImageButton imageButtonSearch;
    private ListView listViewSong;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-07-28 10:58:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        editTextSearch = (EditText)findViewById( R.id.editTextSearch );
        toggle = (RadioGroup)findViewById( R.id.toggle );
        listStyle = (RadioButton)findViewById( R.id.listStyle );
        tableStyle = (RadioButton)findViewById( R.id.tableStyle );
        imageButtonSearch = (ImageButton)findViewById( R.id.imageButtonSearch );
        listViewSong = (ListView)findViewById( R.id.listViewSong );

/*        listStyle.setOnClickListener((View.OnClickListener) this);
        tableStyle.setOnClickListener( this );
        imageButtonSearch.setOnClickListener( this );*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageLoader(getApplicationContext());//Инициализация загрузчика картинок

        findViews();

        //Поиск
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetSongs();
            }
        });

        //Список стиль
        listStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetListStyle();
                GetSongs();
            }
        });

        //Таблица стиль
        tableStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetListStyle();
                GetSongs();
            }
        });

        //Выбор песни
        listViewSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = songs.get(position);

                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

                intent.putExtra(prefixID+"TrackName", s.TrackName);
                intent.putExtra(prefixID+"ImgUrl", s.ImgUrl);
                intent.putExtra(prefixID+"PreviewUrl", s.PreviewUrl);
                startActivity(intent);
            }
        });

        //Прячем клавиатуру
        //This can be used to suppress the soft-keyboard until the user actually touches the editText View
        //http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //Поворот экана. Восстанавливаются данные
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //При смене ориентации экрана
        //Загружаем песни в соответствующем стиле
        SetListStyle();
        GetSongs();
    }

    //Установка флага стиля
    private void SetListStyle(){
        if (isLandscapeOrientation()){
            if (listStyle.isChecked()) mListStyle = ListStyle.List; else mListStyle = ListStyle.TableCol3;
        }else{
            if (listStyle.isChecked()) mListStyle = ListStyle.List; else mListStyle = ListStyle.TableCol2;
        }
    }

    //Инициализация загрузчика картинок
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(5 * 1024 * 1024); // 5 MB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    //Получить список через интернет
    private void GetSongs(){
        String searchText = editTextSearch.getText().toString();
        if (searchText.length() < 5){
            Toast.makeText(getApplicationContext(), "Введите минимум 5 символов", Toast.LENGTH_LONG).show();
        }else{
            //Загрузка песен через интернет
            mGetSongsAsyncTask = new GetSongsAsyncTask();
            mGetSongsAsyncTask.searchText = searchText;
            mGetSongsAsyncTask.execute();
        }
    }

    private class GetSongsAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        public String searchText;

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = "https://itunes.apple.com/search?term=" + searchText;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet hget = new HttpGet(url);

                //Response
                HttpResponse response = client.execute(hget);
                HttpEntity httpEntity = response.getEntity();

                //String result = EntityUtils.toString(httpEntity, "UTF-8");
                String result = EntityUtils.toString(httpEntity);

                if (response.getStatusLine().getStatusCode() == 200){
                    songs.clear();

                    JSONObject jObject = new JSONObject(result);

                    //песни
                    JSONArray jSongs = jObject.getJSONArray("results");
                    for (int i=0; i < jSongs.length(); i++){
                        JSONObject jSong = jSongs.getJSONObject(i);
                        try {
                            Song s = new Song(jSong.getInt("trackId"), jSong.getString("trackName"), jSong.getString("artworkUrl100"), jSong.getString("artistName"), jSong.getString("previewUrl"));

                            songs.add(s);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    return true;
                }

            } catch (UnsupportedEncodingException | ClientProtocolException e) {
                e.printStackTrace();
                Log.e("UPLOAD", e.getMessage());
                //this.exception = e;
            } catch (IOException e) {
                if (e!=null) e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean uploaded) {
            super.onPostExecute(uploaded);

            if(uploaded){
                //Закачали

                mSongsAdapter = new SongAdapter(songs, mListStyle, MainActivity.this);
                listViewSong.setAdapter(mSongsAdapter);

            }else{
                //Не закачали
                Toast.makeText(getApplicationContext(), "Ошибка загрузки списка песен через интернет", Toast.LENGTH_LONG).show();
            }
        }



    }

    private Boolean isLandscapeOrientation(){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }
}
