package com.micromate.mreader;

import java.net.URL;

import android.app.Fragment;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class ArticleFragment extends Fragment{ //1

	private TextView textView1;
	private TextView textView2;
	private TextView textView3;
	private ScrollView scrollView;
	private Button button;
	private String feedLink;
	private String articleTitle;
	private String articleDesc;
	private String articleURL;
	private Bundle bundle;
	private View rootView;
	
	public ArticleFragment(){}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  //3
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		rootView = inflater.inflate(R.layout.fragment_article, container, false); //4
		
		textView1 = (TextView)rootView.findViewById(R.id.article_rss_titleView1);
		textView2 = (TextView)rootView.findViewById(R.id.article_titleView2);
		textView3 = (TextView)rootView.findViewById(R.id.article_descriptionView3);
		scrollView = (ScrollView)rootView.findViewById(R.id.scrollView1);
		button = (Button)rootView.findViewById(R.id.article_button);
			
		bundle = this.getArguments();
		feedLink = bundle.getString("FEED_LINK", "default link");
		articleTitle = bundle.getString("ARTICLE_TITLE", "default title");
		articleDesc = bundle.getString("ARTICLE_DESC", "default description");
			
		textView1.setText(feedLink);
		textView2.setText(Html.fromHtml(articleTitle));
		
		//textView3.setText(Html.fromHtml(articleDesc)); //fromHtml - that converts HTML into a Spannable for use with a TextView		
		URLImageParser p = new URLImageParser(textView3);
		Spanned htmlSpan = Html.fromHtml(articleDesc, p, null);
		textView3.setText(htmlSpan);
		
		textView3.setMovementMethod(LinkMovementMethod.getInstance()); //enable links
				
		scrollView.post(new Runnable()
	    { 
	        public void run()
	        { 
	            scrollView.fullScroll(View.FOCUS_UP);
	        } 
	    });
		
		
		articleURL = bundle.getString("ARTICLE_LINK", "default link");
		
		button.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View arg0) {
		        Intent buttonIntent = new Intent(Intent.ACTION_VIEW, 
		                Uri.parse(articleURL));
		        startActivity(buttonIntent);
		    }
		});
		
		
		return rootView;  //5
	}
		
	private class URLDrawable extends BitmapDrawable {
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
	        	Drawable drawable = getResources().getDrawable( R.drawable.ic_action_picture);
	        	try {
	                //InputStream is = fetch(urlString);
	            	URL url = new URL(inputURL[0]);
	                drawable = Drawable.createFromStream(url.openStream(), "src");
	               
	            } catch (Exception e) {
	            	e.printStackTrace();
	                
	            } 
	        	drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight()); 
	        	return drawable;
	        }

	        @Override
	        protected void onPostExecute(Drawable result) {
	           
	        	// set the correct bound according to the result from HTTP call	        	
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

