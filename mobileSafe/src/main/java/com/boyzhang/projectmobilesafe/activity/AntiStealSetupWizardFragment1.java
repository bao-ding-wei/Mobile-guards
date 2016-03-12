package com.boyzhang.projectmobilesafe.activity;

import com.boyzhang.projectmobilesafe.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AntiStealSetupWizardFragment1 extends BaseFragment {

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(
				R.layout.antisteal_setup_wizard_page1, null);
		return v;
	}

	@Override
	public boolean checkInput() {
		// TODO Auto-generated method stub
		return true;
	}
}
