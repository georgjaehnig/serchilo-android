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
import android.view.View;
import android.widget.Button;

public class SendQuery extends AsyncTask<String, Void, String> {

	Context context;
	private View mainView;

	/**
	 * Constructor.
	 */
	public SendQuery(Context context, View mainView) {
		this.context = context.getApplicationContext();
		this.mainView = mainView;
	}

	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * Get the JSON from the API.
	 */
	protected String doInBackground(String... urls) {
		// API URL is the first and only one passed.
		String domain = urls[0];
		String pathAndQuery = urls[1];

		String apiUrlStr = domain + "api/" + pathAndQuery;
		String errorUrlStr = domain + pathAndQuery + "&status=not_found";
		System.out.println("step1");
		// Open stream and convert to JSON.
		try {
			System.out.println("step2");
			URL apiUrl = new URL(apiUrlStr);
			InputStream inputStream = apiUrl.openStream();
			String jsonStr = convertStreamToString(inputStream);
			JSONObject json = new JSONObject(jsonStr);
			// Check if found.
			boolean found = json.getJSONObject("status").getBoolean("found");

			if (found) {
				// Get shortcut URL from JSON object.
				String shortcutUrlStr = json.getJSONObject("url").getString(
						"final");
				return shortcutUrlStr;
			} else {
				return errorUrlStr;
			}

		} catch (java.net.UnknownHostException e) {
			return "";
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * Send the intent.
	 */
	protected void onPostExecute(String shortcutUrl) {
		super.onPostExecute(shortcutUrl);
		Button searchSubmit = (Button) mainView.findViewById(R.id.searchSubmit);

		if (shortcutUrl != "") {
			super.onPostExecute(shortcutUrl);

			Intent intent = new Intent(Intent.ACTION_VIEW); //
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse(shortcutUrl));

			context.startActivity(intent);
			searchSubmit.setText("Go");
		} else {
			searchSubmit.setText("No Internet!");
		}

	}

	/**
	 * One of these bullshit Java functions, doing something absolutely trivial,
	 * but with 20 lines of code.
	 */
	private static String convertStreamToString(InputStream is) {
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