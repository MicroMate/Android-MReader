package com.micromate.mreader;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.micromate.mreader.dialogs.DeleteArticleDialogFragment2;

public class ArticleActivity extends Activity {

	private TextView textView1;
	private ScrollView scrollView;
	private ImageView buttonWeb;
	private String articleTitle;
	private String articleDesc;
	private String articleURL;
	private Bundle bundle;
	private ImageView buttonDelete;
	
	//for debugging
	private static final String LOG_TAG ="ArticleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		
		textView1 = (TextView)findViewById(R.id.article_descriptionView3);
		scrollView = (ScrollView)findViewById(R.id.scrollView1);
		buttonWeb = (ImageView)findViewById(R.id.articleButtonVisitSite);
		buttonDelete = (ImageView)findViewById(R.id.articleButtonDelete);
		
		getActionBar().hide();

		//getting data from parent activity
		//getIntent - return the intent that started this activity
		//getExtrras - retrieves a map of extended data from the intent
		bundle = getIntent().getExtras();
		articleTitle = bundle.getString("ARTICLE_TITLE", "default title");
		articleDesc = bundle.getString("ARTICLE_DESC", "default description");		
				
		//
		String titleMyFormat = "<h3><font color=#cccccc>"+articleTitle+"</font></h3>";
			
		//textView3.setText(Html.fromHtml(articleDesc)); //fromHtml - that converts HTML into a Spannable for use with a TextView		
		URLImageParser p = new URLImageParser(textView1);
		Spanned htmlSpan = Html.fromHtml(titleMyFormat + articleDesc, p, null);
		textView1.setText(htmlSpan);
		
		textView1.setMovementMethod(LinkMovementMethod.getInstance()); //enable links
				
		scrollView.post(new Runnable()
	    { 
	        public void run()
	        { 
	            scrollView.fullScroll(View.FOCUS_UP);
	        } 
	    });
		
		
		articleURL = bundle.getString("ARTICLE_LINK", "default link");
		
		
		buttonWeb.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		    	Log.d(LOG_TAG,"Article link: "+articleURL);
		    	try{
		    		Intent buttonIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(articleURL));
		        	startActivity(buttonIntent);
		        }catch(Exception e){
		        	Toast.makeText(getApplicationContext(), "No Link", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
	
		buttonDelete.setOnClickListener(new OnClickListener() {
		    @Override
		    
		    public void onClick(View arg0) {
		    	//delete Article dialog
				DeleteArticleDialogFragment2 deleteArticle = new DeleteArticleDialogFragment2();
				deleteArticle.setData(articleTitle,articleURL);
				deleteArticle.show(getFragmentManager(), "delete article TAG");	
							
		    }
		});
	
		
	}
	
	
	private class URLDrawable extends BitmapDrawable {
		public URLDrawable(){      // BitmapDrawable() constructor was deprecated in API level 4. 
			super(getResources());   //instead constructor: BitmapDrawable(Resource res);
		}
	    // the drawable that you need to set, you could set the initial drawing
	    // with the loading image if you need to
	    protected Drawable drawable;
	
	    @Override
	    public void draw(Canvas canvas) {
	        // override the draw to FACILITATE REFRESH function later
	        if(drawable != null) {
	            drawable.draw(canvas);
	        }
	    }
	}

	
	private class URLImageParser implements ImageGetter {
	
		private TextView textView;

	    public URLImageParser(TextView t) {
	        this.textView = t;
	    }
	    
	    @Override
	    public Drawable getDrawable(String source) {
	        
	    	URLDrawable urlDrawable = new URLDrawable();
	    
	        // get the actual source
	        ImageGetterTask asyncTask = new ImageGetterTask(urlDrawable);

	        asyncTask.execute(source);

	        // return reference to URLDrawable where I will change with actual image from
	        // the src tag
	        return urlDrawable;
	    }

	    
	    private class ImageGetterTask extends AsyncTask<String, Void, Drawable>  {
	        URLDrawable urlDrawable;

	        public ImageGetterTask(URLDrawable d) {
	            this.urlDrawable = d;
	        }

	        @Override
	        protected Drawable doInBackground(String... inputURL) {
	            //String source = params[0];
	            //return fetchDrawable(source);
	        	Drawable drawable = null;
	        	
	        	//This inSampleSize option reduces memory consumption.
	        	BitmapFactory.Options options = new BitmapFactory.Options();
	        	options.inSampleSize = 2;  //returning smaller image to save memory, 1/2 size
	        
	        	try {
	        		drawable = getResources().getDrawable( R.drawable.ic_action_picture);
	        	        
	        		//InputStream is = fetch(urlString);
	            	URL url = new URL(inputURL[0]);
	                //drawable = Drawable.createFromStream(url.openStream(), "src");
	                //Decode image size
	                Bitmap myBitmap = BitmapFactory.decodeStream(url.openStream(), null, options);
	                //getActivity().getResources();
	                getResources();
					drawable = new BitmapDrawable(Resources.getSystem(),myBitmap);
	            } catch (Exception e) {
	            	e.printStackTrace();
	                
	            } 
	        	
	        	if (drawable !=null)
	        		drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());  
	        	
	        	return drawable;
	        }

	        @Override
	        protected void onPostExecute(Drawable result) {
	           
	        	// set the correct bound according to the result from HTTP call	        	
	 	        if (result != null){
	 	        	urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight()); 

	 	        	// change the reference of the current drawable to the result
	 	        	// from the HTTP call
	 	        	urlDrawable.drawable = result;
	        	
	 	        	// redraw the image by invalidating the container
	 	        	textView.invalidate();
	                      
	 	        	textView.setHeight((textView.getHeight() + result.getIntrinsicHeight()));

	 	        	textView.setEllipsize(null);
	 	        }
	        }

	    }
	}
	
	
	/**
	 * Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.article, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
