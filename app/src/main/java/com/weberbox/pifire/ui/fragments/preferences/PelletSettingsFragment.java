package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.GrillControl;

import io.socket.client.Socket;

public class PelletSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_pellet_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        EditTextPreference pelletsFull = findPreference(getString(R.string.prefs_pellet_full));
        EditTextPreference pelletsEmpty = findPreference(getString(R.string.prefs_pellet_empty));

        if (pelletsFull != null) {
            pelletsFull.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            editText.setError(getString(R.string.settings_blank_error));
                        } else if (Integer.parseInt(s.toString()) > 100) {
                            editText.setError(getString(R.string.settings_max_hundred_error));
                        } else {
                            editText.setError(null);
                        }

                    }
                });
            });
        }

        if (pelletsEmpty != null) {
            pelletsEmpty.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            editText.setError(getString(R.string.settings_blank_error));
                        } else if (s.toString().equals("0")) {
                            editText.setError(getString(R.string.settings_zero_error));
                        } else if (Integer.parseInt(s.toString()) > 100) {
                            editText.setError(getString(R.string.settings_max_hundred_error));
                        } else {
                            editText.setError(null);
                        }

                    }
                });
            });
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_pellet_empty)
                        .equals(preference.getKey())) {
                    GrillControl.setPelletsEmpty(mSocket, ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_pellet_full)
                        .equals(preference.getKey())) {
                    GrillControl.setPelletsFull(mSocket, ((EditTextPreference) preference).getText());
                }
            }
        }
    }
}
