package net.serchilo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	EditText searchInput;
	Button searchSubmit;
	Button clearButton;
	Button commaButton;

	public static final String DOMAIN = "https://www.findfind.it/";

	/**
	 * Sets the listeners to subit button and input field.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchInput = (EditText) findViewById(R.id.searchInput);
		searchSubmit = (Button) findViewById(R.id.searchSubmit);
		clearButton = (Button) findViewById(R.id.clearButton);
		commaButton = (Button) findViewById(R.id.commaButton);

		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchInput.setText("");
			}
		});

		commaButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchInput.append(",");

			}
		});

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

		setDefaultSettings();
		updateRecentKeywords();
	}

	/**
	 * Adds a keyword to the list of recent keywords.
	 * 
	 * @param keyword
	 */
	private void addKeyword(String keyword) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();

		String recentKeywordsString = pref.getString("recent_keywords", "");
		String[] recentKeywordsArray = recentKeywordsString.split(" ");

		ArrayList<String> recentKeywords = new ArrayList<String>(
				Arrays.asList(recentKeywordsArray));

		// If keyword is already in the list.
		if (recentKeywords.contains(keyword)) {
			// Remove it (to place it again at front).
			recentKeywords.remove(recentKeywords.indexOf(keyword));
		}
		recentKeywords.add(0, keyword);

		// Limit list to 5.
		if (recentKeywords.size() > 5) {
			recentKeywords.remove(5);
		}
		recentKeywordsString = "";
		for (int i = 0; i < recentKeywords.size(); i++) {
			recentKeywordsString = recentKeywordsString + recentKeywords.get(i)
					+ " ";
		}
		recentKeywordsString = recentKeywordsString.trim();
		editor.putString("recent_keywords", recentKeywordsString);
		editor.commit();
	}

	/**
	 * Update the the buttons of the recent keywords.
	 */
	private void updateRecentKeywords() {
		LinearLayout keywordButtons = (LinearLayout) findViewById(R.id.keywordButtons);
		keywordButtons.removeAllViews();
		String[] recentKeywords = PreferenceManager
				.getDefaultSharedPreferences(this)
				.getString("recent_keywords", "").split(" ");
		for (int i = 0; i < recentKeywords.length; i++) {
			addRecentKeyword(keywordButtons, recentKeywords[i], i + 1);
		}
	}

	/**
	 * Add a recentKeywordButton.
	 * 
	 * @param keywordButtons
	 */
	private Button addRecentKeyword(LinearLayout keywordButtons, String text,
			int id) {

		// Create new button with text.
		Button recentKeywordButton = new Button(this);
		recentKeywordButton.setText(text);

		// TODO: Make sure they actually wrap.
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.20f);

		recentKeywordButton.setLayoutParams(params);
		recentKeywordButton.setSingleLine(true);

		recentKeywordButton.setId(id);

		// On click:
		// set keyword to input.
		recentKeywordButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Button recentKeywordButton = (Button) v;
				searchInput.setText("");
				searchInput.append(recentKeywordButton.getText().toString()
						+ " ");
			}
		});

		// On long click:
		// delete keyword.
		recentKeywordButton.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				v.setVisibility(View.GONE);
				return false;
			}
		});

		// Add button to the view.
		keywordButtons.addView(recentKeywordButton);

		return recentKeywordButton;
	}

	/**
	 * Show the keyboard on start.
	 */
	@Override
	public void onStart() {
		super.onStart();

		// Show soft keyboard.
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		searchInput.postDelayed(new Runnable() {
			@Override
			public void run() {
				searchInput.requestFocus();
				imm.showSoftInput(searchInput, 0);
			}
		}, 200);

		searchInput.selectAll();
	}

	/**
	 * Set the default settings.
	 */
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

		if (!pref.contains("recent_keywords")) {
			editor.putString("recent_keywords", "g w yt gm a");
		}
		editor.commit();
	}

	/**
	 * TODO: find out what this does.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Open the settings.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handle the submit click.
	 * 
	 * @param v
	 *            The current View.
	 */
	private void handleSubmitClick(View v) {
		String query = searchInput.getText().toString();
		String[] keywordAndArguments = parseQuery(query);

		// Add keyword to recent keyword list and update the buttons.
		addKeyword(keywordAndArguments[0]);
		updateRecentKeywords();

		// Process the query.
		processKeywordAndArguments(keywordAndArguments[0],
				keywordAndArguments[1]);

	}

	/**
	 * Parse the query, i.e. split it into keyword and arguments.
	 * 
	 * @param query
	 * @return
	 */
	private String[] parseQuery(String query) {
		// Add whitespace to always get at least 2 parts on split.
		query = query + " ";
		String[] keywordAndArguments = query.split(" ", 2);

		if (keywordAndArguments.length < 2) {
			keywordAndArguments[1] = "";
		}
		return keywordAndArguments;
	}

	private void processKeywordAndArguments(String keyword, String arguments) {
		processQuery(keyword + " " + arguments);
	}

	/**
	 * Process the query.
	 * 
	 * @param query
	 *            The query string.
	 */
	private void processQuery(String query) {

		query = query.trim();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		String userName = pref.getString("user_name", "");
		String languageNamespace = pref.getString("language_namespace", "");
		String countryNamespace = pref.getString("country_namespace", "");
		String customNamespaces = pref.getString("custom_namespaces", "");
		String defaultKeyword = pref.getString("default_keyword", "");

		String pathAndQuery = "";
		if (userName.equals("")) {
			pathAndQuery += "n/" + languageNamespace + "." + countryNamespace;
			if (!customNamespaces.equals("")) {
				pathAndQuery += "." + customNamespaces;
			}
			pathAndQuery += "?";
			if (!defaultKeyword.equals("")) {
				pathAndQuery += "default_keyword=" + defaultKeyword + "&";
			}
		} else {
			pathAndQuery += "u/" + userName + "?";
		}
		try {
			pathAndQuery += "query=" + URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new SendQuery(this).execute(DOMAIN, pathAndQuery);
	}
}
