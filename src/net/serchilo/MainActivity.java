package net.serchilo;

import java.util.ArrayList;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView searchInput;
	Button searchSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchInput = (TextView) findViewById(R.id.searchInput);

		searchSubmit = (Button) findViewById(R.id.searchSubmit);

		searchSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handleSubmitClick(v);
			}
		});

		searchInput
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						handleSubmitClick(v);
						return true;
					}
				});


		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		setDefaultSettings();
	}

	private void setDefaultSettings() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();

		if (!pref.contains("language_namespace")) {
			editor.putString("language_namespace", Locale.getDefault()
					.getLanguage().toLowerCase());
		}

		if (!pref.contains("country_namespace")) {
			editor.putString("country_namespace", Locale.getDefault()
					.getISO3Country().toLowerCase());
		}

		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// @Override
	private void handleSubmitClick(View v) {

		String query = searchInput.getText().toString();
		String[] keywordAndArguments = parseQuery(query);
		processKeywordAndArguments(keywordAndArguments[0],
				keywordAndArguments[1]);

	}

	private String[] parseQuery(String query) {
		// add whitespace to the end
		// so we always get at least 2 parts on split
		query = query + " ";
		String[] keywordAndArguments = query.split(" ", 2);

		if (keywordAndArguments.length < 2) {
			keywordAndArguments[1] = "";
		}
		return keywordAndArguments;
	}

	private void processKeywordAndArguments(String keyword, String arguments) {
		sendQuery(keyword + " " + arguments);
	}

	private void sendQuery(String query) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		String userName = pref.getString("user_name", "");
		String languageNamespace = pref
				.getString("language_namespace", "");
		String countryNamespace = pref.getString("country_namespace", "");
		String customNamespaces = pref.getString("custom_namespaces", "");
		String defaultKeyword = pref.getString("default_keyword", "");

		String url = "http://www.serchilo.net/";

		if (userName.equals("")) {
			url += "n/" + languageNamespace + "." + countryNamespace;
			if (!customNamespaces.equals("")) {
				url += "." + customNamespaces;
			}
			url += "?";
			if (!defaultKeyword.equals("")) {
				url += "default_keyword=" + defaultKeyword + "&";
			}
		} else {
			url += "u/" + userName + "?";
		}
		url += "query=" + query;

		Intent httpIntent = new Intent(Intent.ACTION_VIEW);
		httpIntent.setData(Uri.parse(url));

		// send to browser
		// (or other app handling the url)
		startActivity(httpIntent);
	}

}
