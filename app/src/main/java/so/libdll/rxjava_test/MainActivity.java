package so.libdll.rxjava_test;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressBar myPregressBar;
    private TextView myTextView;
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        myTextView = (TextView)findViewById(R.id.mytext);
        myPregressBar = (ProgressBar)findViewById(R.id.myProgressBar);
        myImageView = (ImageView)findViewById(R.id.myImageView);
        myPregressBar.setVisibility(View.INVISIBLE);
        String url = "http://camranger.com/wp-content/uploads/2014/10/Android-Icon.png";
        loadPhoto(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Get bitmap from url user given
    private Bitmap getPhoto(String input_url) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(input_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200) {
                return BitmapFactory.decodeStream(urlConnection.getInputStream());
            }
            Log.i("bitmap:", "respon not 200");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(urlConnection != null)
            urlConnection.disconnect();
        }
    }
    //Load photo from usre given url
    private  boolean loadPhoto(String url) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        myPregressBar.setVisibility(View.VISIBLE);
                        myTextView.setText("Loading...");
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return getPhoto(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        myPregressBar.setVisibility(View.INVISIBLE);
                        myTextView.setVisibility(View.INVISIBLE);
                        //myImageView.setVisibility(View.VISIBLE);
                        myImageView.setImageBitmap(bitmap);
                    }
                });
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
