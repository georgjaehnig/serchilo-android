package net.serchilo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

public class SendQuery extends AsyncTask<String, Void, String> {

	Context context;

	public SendQuery(Context context) {
		this.context = context.getApplicationContext();
	}

	protected String doInBackground(String... urls) {
		try {
			// API URL is the first and only one passed.
			URL apiUrl = new URL(urls[0]);

			// Open stream and convert to JSON.
			InputStream inputStream = apiUrl.openStream();
			String jsonStr = convertStreamToString(inputStream);
			JSONObject json = new JSONObject(jsonStr);

			// Check if found.
			boolean found = json.getJSONObject("status").getBoolean("found");

			if (found) {
				// Get shortcut URL from JSON object.
				String shortcutUrl = json.getJSONObject("url").getString(
						"final");
				return shortcutUrl;
			} else {
				// TODO: Show error / redirect to Serchilo site.
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(String shortcutUrl) {

		super.onPostExecute(shortcutUrl);

		Intent intent = new Intent(Intent.ACTION_VIEW); //
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(shortcutUrl));

		context.startActivity(intent);
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}