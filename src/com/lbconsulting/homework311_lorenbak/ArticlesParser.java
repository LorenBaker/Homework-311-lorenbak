package com.lbconsulting.homework311_lorenbak;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

import com.lbconsulting.homework311_lorenbak.database.ArticlesTable;

public class ArticlesParser {

	public final static String TAG_ARTICLES = "articles";
	public final static String TAG_ITEM = "item";
	public final static String TAG_TITLE = "title";
	public final static String TAG_CONTENT = "content";

	// We don't use namespaces
	private static final String ns = null;
	private static Context mContext;

	public static void parse(Context context, InputStream in) throws XmlPullParserException, IOException {

		try {
			mContext = context;
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			readFeed(parser);
		} finally {
			in.close();
		}
	}

	private static void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, TAG_ARTICLES);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(TAG_ITEM)) {
				RreadItemData(parser);

			} else {
				skip(parser);
			}
		}
	}

	private static void RreadItemData(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, TAG_ITEM);
		String title = null;
		String content = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			if (name.equals(TAG_TITLE)) {
				title = readText(parser);

			} else if (name.equals(TAG_CONTENT)) {
				content = readText(parser);

			} else {
				skip(parser);
			}
		}

		if (title != null && !title.isEmpty()) {
			ArticlesTable.CreateArticle(mContext, title, content);
		}

	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
				default:
					break;
			}
		}
	}

	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = null;
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

}
