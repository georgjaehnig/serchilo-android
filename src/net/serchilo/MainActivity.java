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

	int inputCount = 9;

	ArrayList<TextView> searchKeywords = new ArrayList<TextView>();
	ArrayList<TextView> searchInputs = new ArrayList<TextView>();
	ArrayList<Button> searchSubmits = new ArrayList<Button>();

	ArrayList<String> recentKeywords = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchInput = (TextView) findViewById(R.id.searchInput);

		searchInputs.add((TextView) findViewById(R.id.searchInput1));
		searchInputs.add((TextView) findViewById(R.id.searchInput2));
		searchInputs.add((TextView) findViewById(R.id.searchInput3));
		searchInputs.add((TextView) findViewById(R.id.searchInput4));
		searchInputs.add((TextView) findViewById(R.id.searchInput5));
		searchInputs.add((TextView) findViewById(R.id.searchInput6));
		searchInputs.add((TextView) findViewById(R.id.searchInput7));
		searchInputs.add((TextView) findViewById(R.id.searchInput8));
		searchInputs.add((TextView) findViewById(R.id.searchInput9));

		searchSubmit = (Button) findViewById(R.id.searchSubmit);

		searchSubmits.add((Button) findViewById(R.id.searchSubmit1));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit2));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit3));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit4));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit5));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit6));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit7));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit8));
		searchSubmits.add((Button) findViewById(R.id.searchSubmit9));

		// searchSubmit.setOnClickListener((OnClickListener) this);
		// searchSubmit.setOnClickListener((OnClickListener) SearchSubmit);

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

		for (int i = 0; i < searchSubmits.size(); i++) {
			searchSubmits.get(i).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					handleSubmitClick(v);
				}
			});
			searchInputs.get(i).setOnEditorActionListener(
					new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							handleSubmitClick(v);
							return true;
						}
					});
		}

		// default recent keywords
		recentKeywords.add("g");
		recentKeywords.add("w");
		recentKeywords.add("yt");
		recentKeywords.add("gm");
		recentKeywords.add("gi");
		recentKeywords.add("a");
		recentKeywords.add("en");
		recentKeywords.add("en.w");
		recentKeywords.add("db");

		searchKeywords.add((TextView) findViewById(R.id.textView1));
		searchKeywords.add((TextView) findViewById(R.id.textView2));
		searchKeywords.add((TextView) findViewById(R.id.textView3));
		searchKeywords.add((TextView) findViewById(R.id.textView4));
		searchKeywords.add((TextView) findViewById(R.id.textView5));
		searchKeywords.add((TextView) findViewById(R.id.textView6));
		searchKeywords.add((TextView) findViewById(R.id.textView7));
		searchKeywords.add((TextView) findViewById(R.id.textView8));
		searchKeywords.add((TextView) findViewById(R.id.textView9));

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		// get recent keywords from prefs
		for (int i = 0; i < recentKeywords.size(); i++) {
			recentKeywords.set(i, pref.getString("recent_keyword_"
					+ String.valueOf(i), recentKeywords.get(i)));
		}

		setRecentKeywordsToTextViews();

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

		// check first if clicked on a recentKeyword input
		for (int i = 0; i < inputCount; i++) {
			// submitter can be EditText or Button
			if ((v.getId() == searchSubmits.get(i).getId())
					|| (v.getId() == searchInputs.get(i).getId())) {
				String keyword = searchKeywords.get(i).getText().toString();
				String arguments = searchInputs.get(i).getText().toString();
				// delete text in input
				// otherwise it becomes messy when recentKeywords are reordered
				searchInputs.get(i).setText("");
				processKeywordAndArguments(keyword, arguments);
				return;
			}

		}
		// if not used a recent keyword
		// use main input
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

	private void addKeywordtoRecentKeywords(String keyword) {
		// first remove keyword
		// to avoid double entries
		recentKeywords.remove(keyword);
		// add keyword at the beginning
		recentKeywords.add(0, keyword);
		// make sure we're not having too much
		if (recentKeywords.size() > inputCount) {
			// remove at the end
			recentKeywords.remove(inputCount);
		}

	}

	private void saveRecentKeywordsToPrefs() {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();

		// save recent keywords to prefs
		for (int i = 0; i < recentKeywords.size(); i++) {
			editor.putString("recent_keyword_" + String.valueOf(i),
					recentKeywords.get(i));
		}

		editor.commit();
	}

	private void setRecentKeywordsToTextViews() {

		for (int i = 0; i < searchKeywords.size(); i++) {
			searchKeywords.get(i).setText(recentKeywords.get(i));
		}
	}

	private void processKeywordAndArguments(String keyword, String arguments) {
		addKeywordtoRecentKeywords(keyword);
		setRecentKeywordsToTextViews();
		saveRecentKeywordsToPrefs();

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
