package net.serchilo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView searchInput;
	Button searchSubmit;

	OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchInput = (TextView) findViewById(R.id.searchInput);
		searchSubmit = (Button) findViewById(R.id.searchSubmit);

		updateContextDisplay();

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

		// updateContextDisplay when preferences changed
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				updateContextDisplay();
			}
		};

		prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

		setDefaultSettings();
	}

	public void updateContextDisplay() {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);

		String userName = pref.getString("user_name", "");

		if (userName.isEmpty()) {
			displayNamespaces(pref.getString("language_namespace", ""),
					pref.getString("country_namespace", ""),
					pref.getString("custom_namespaces", ""));
		} else {
			displayUsername(userName);
		}
	}

	private void displayUsername(String userName) {
		TextView tvNamespacesLabel = (TextView) findViewById(R.id.textViewLabelNamespaces);
		tvNamespacesLabel.setText("Username:");

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

		removeNamespaces(relativeLayout);

		TextView tvNamespace = new TextView(this);
		tvNamespace.setText(userName);
		tvNamespace.setTag("namespace");
		setNamespaceStyles(tvNamespace);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 5, 0);
		params.addRule(RelativeLayout.RIGHT_OF, R.id.textViewLabelNamespaces);
		tvNamespace.setLayoutParams(params);

		relativeLayout.addView(tvNamespace);
	}

	private void displayNamespaces(String languageNamespace,
			String countryNamespace, String customNamespacesString) {

		TextView tvNamespacesLabel = (TextView) findViewById(R.id.textViewLabelNamespaces);
		tvNamespacesLabel.setText("Namespaces:");

		ArrayList<String> namespaces = splitNamespaceString(customNamespacesString);

		namespaces.add(0, languageNamespace);
		namespaces.add(1, countryNamespace);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
		removeNamespaces(relativeLayout);
		addNamespaces(relativeLayout, namespaces);
	}

	/**
	 * @param customNamespacesString
	 * @return
	 */
	private ArrayList<String> splitNamespaceString(String customNamespacesString) {
		customNamespacesString = customNamespacesString.trim();

		String[] customNamespaces = new String[0];
		if (!customNamespacesString.isEmpty()) {
			customNamespaces = customNamespacesString.split("\\.");
		}

		ArrayList<String> namespaces = new ArrayList<String>(
				Arrays.asList(customNamespaces));
		return namespaces;
	}

	private void addNamespaces(RelativeLayout relativeLayout,
			ArrayList<String> namespaces) {
		ArrayList<TextView> tvNamespaces = new ArrayList<TextView>();
		for (int i = 0; i < namespaces.size(); i++) {
			tvNamespaces.add(new TextView(this));
			tvNamespaces.get(i).setId(i + 1);
			tvNamespaces.get(i).setTag("namespace");

			tvNamespaces.get(i).setText(namespaces.get(i));
			setNamespaceStyles(tvNamespaces.get(i));
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			// for the first textView
			if (i == 0) {
				// place it right to the label
				params.addRule(RelativeLayout.RIGHT_OF,
						R.id.textViewLabelNamespaces);
			} else {
				// place it right to the previous item
				params.addRule(RelativeLayout.RIGHT_OF, tvNamespaces.get(i - 1)
						.getId());
			}
			// set right margin;
			params.setMargins(0, 0, 5, 0);
			tvNamespaces.get(i).setLayoutParams(params);
			relativeLayout.addView(tvNamespaces.get(i));
		}
	}

	private void removeNamespaces(RelativeLayout relativeLayout) {

		TextView tvNamespace;
		do {
			tvNamespace = (TextView) relativeLayout
					.findViewWithTag("namespace");
			if (tvNamespace != null) {
				relativeLayout.removeView(tvNamespace);
			}
		} while (tvNamespace != null);
	}

	private void setNamespaceStyles(TextView tvNamespace) {
		tvNamespace.setPadding(5, 5, 5, 5);
		tvNamespace.setBackgroundColor(0xFFAA2C30);
		tvNamespace.setTextColor(0xFFFFFFFF);
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
		String languageNamespace = pref.getString("language_namespace", "");
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
