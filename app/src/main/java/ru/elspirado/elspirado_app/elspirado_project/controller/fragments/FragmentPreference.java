package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import ru.elspirado.elspirado_app.elspirado_project.R;

public class FragmentPreference extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        //Оставляю этот код, чтобы не гуглить как это работает


//        preferenceEnterAccount =
//            findPreference("settings_sign_up");
//
//
//        preferenceEnterAccount.setOnPreferenceClickListener(
//            new androidx.preference.Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(androidx.preference.Preference preference) {
//                    mListener.onFragmentPreferencesSignUp();
//                    return false;
//                }
//            });

    }


//    public interface OnFragmentListener {
//
//    }

   // private FragmentPreference.OnFragmentListener mListener;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        if (context instanceof FragmentPreference.OnFragmentListener) {
//            mListener = (FragmentPreference.OnFragmentListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                + " must implement OnFragmentListener");
//        }
//    }
}