package com.duckduckgo.mobile.android.adapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGConstants;


public class MainFeedAdapter extends ArrayAdapter<FeedObject> {
	private static final String TAG = "MainFeedAdapter";
	
	private final LayoutInflater inflater;
	
	public boolean scrolling = false;
	
	//TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...
	protected final ImageDownloader imageDownloader;
			
	public MainFeedAdapter(Context context) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageDownloader = DDGApplication.getImageDownloader();
	}
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		if (cv == null) {
			cv = inflater.inflate(R.layout.main_feed_layout, null);
			cv.setTag(new Holder((TextView)cv.findViewById(R.id.feedTitleTextView),
					             (AsyncImageView)cv.findViewById(R.id.feedItemBackground),
					             (AsyncImageView)cv.findViewById(R.id.feedItemSourceIcon),
					             (AsyncImageView)cv.findViewById(R.id.feedItemUrlIcon)));
		}
		
		FeedObject feed = getItem(position);
		
		final Holder holder = (Holder) cv.getTag();
		URL iconUrl = null;
		URL feedUrl = null;

		if (feed != null) {			
			
			//Download the background image
			if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
				imageDownloader.download(feed.getImageUrl(), holder.imageViewBackground, scrolling);
			} else {
				imageDownloader.download(null, holder.imageViewBackground, scrolling);
			}

			//Set the Title
			holder.textViewTitle.setText(feed.getTitle());

			//Set the URL Icon on the Left Hand Side
			if (feed.getUrl() != null && !feed.getUrl().equals("null")) {
				try {
					iconUrl = new URL(feed.getUrl());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				if (iconUrl != null) {
					String host = iconUrl.getHost();
					if (host.indexOf(".") != host.lastIndexOf(".")) {
						//Cut off the beginning, because we don't want/need it
						host = host.substring(host.indexOf(".")+1);
					}
					imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewUrlIcon, scrolling);
				} else {
					imageDownloader.download(null, holder.imageViewUrlIcon, scrolling);
				}
			} else {
				imageDownloader.download(null, holder.imageViewUrlIcon, scrolling);
			}

			if (feed.getFeed() != null && !feed.getFeed().equals("null")) {
				try {
					feedUrl = new URL(feed.getFeed());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				if (feedUrl != null) {
					if (iconUrl != null) {
						if (iconUrl.getHost().equals(feedUrl.getHost())) {
							//They are the same, don't show it
							imageDownloader.download(null, holder.imageViewFeedIcon, scrolling);
						} else {
							String host = feedUrl.getHost();
							if (host.indexOf(".") != host.lastIndexOf(".")) {
								//Cut off the beginning, because we don't want/need it
								host = host.substring(host.indexOf(".")+1);
							}							
							//They are different, so show it
							imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewFeedIcon, scrolling);
						}
					} else {
						String host = feedUrl.getHost();
						if (host.indexOf(".") != host.lastIndexOf(".")) {
							//Cut off the beginning, because we don't want/need it
							host = host.substring(host.indexOf(".")+1);
						}
						imageDownloader.download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", holder.imageViewFeedIcon, scrolling);
					}
				} else {
					imageDownloader.download(null, holder.imageViewFeedIcon, scrolling);
				}
			} else {
				imageDownloader.download(null, holder.imageViewFeedIcon, scrolling);
			}
		}

		return cv;
	}
	

	public void setList(List<FeedObject> feed) {
		this.clear();
		for (FeedObject next : feed) {
			this.add(next);
		}
	}

}