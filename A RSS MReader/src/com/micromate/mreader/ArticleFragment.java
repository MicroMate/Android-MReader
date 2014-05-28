package com.micromate.mreader;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class ArticleFragment extends Fragment{ //1

	private TextView textView;
	private TextView textView2;
	private TextView textView3;
	private ScrollView scrollView;
	private Button button;
	private String feedLink;
	private String articleTitle;
	private String articleDesc;
	private String articleURL;
	private Bundle bundle;
	
	public ArticleFragment(){}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  //3
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_article, container, false); //4
		
		textView = (TextView)rootView.findViewById(R.id.article_rss_titleView1);
		textView2 = (TextView)rootView.findViewById(R.id.article_titleView2);
		textView3 = (TextView)rootView.findViewById(R.id.article_descriptionView3);
		scrollView = (ScrollView)rootView.findViewById(R.id.scrollView1);
		button = (Button)rootView.findViewById(R.id.article_button);
			
		bundle = this.getArguments();
		feedLink = bundle.getString("FEED_LINK", "default link");
		articleTitle = bundle.getString("ARTICLE_TITLE", "default title");
		articleDesc = bundle.getString("ARTICLE_DESC", "default description");
			
		textView.setText(feedLink);
		textView2.setText(articleTitle);
		
		textView3.setText(Html.fromHtml(articleDesc)); //fromHtml - that converts HTML into a Spannable for use with a TextView
	
		
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
	
}
