package net.serchilo;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	EditTextPreference prefUserName;
	ListPreference prefLanguageNamespace;
	ListPreference prefCountryNamespace;
	EditTextPreference prefCustomNamespaces;
	EditTextPreference prefDefaultKeyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_settings);

		prefUserName = (EditTextPreference) findPreference("user_name");
		prefLanguageNamespace = (ListPreference) findPreference("language_namespace");
		prefCountryNamespace = (ListPreference) findPreference("country_namespace");
		prefCustomNamespaces = (EditTextPreference) findPreference("custom_namespaces");
		prefDefaultKeyword = (EditTextPreference) findPreference("default_keyword");

		// call this only when defaults set!
		// probably enough to set username as empty string in xml
		enableAndDisablePreferences(prefUserName.getText());

		prefUserName
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						final String userName = newValue.toString();
						enableAndDisablePreferences(userName);
						return true;
					}

				});
	}

	protected void enableAndDisablePreferences(String userName) {

		if (userName.equals("")) {
			prefLanguageNamespace.setEnabled(true);
			prefCountryNamespace.setEnabled(true);
			prefCustomNamespaces.setEnabled(true);
			prefDefaultKeyword.setEnabled(true);
		} else {
			prefLanguageNamespace.setEnabled(false);
			prefCountryNamespace.setEnabled(false);
			prefCustomNamespaces.setEnabled(false);
			prefDefaultKeyword.setEnabled(false);
		}
	}

}
