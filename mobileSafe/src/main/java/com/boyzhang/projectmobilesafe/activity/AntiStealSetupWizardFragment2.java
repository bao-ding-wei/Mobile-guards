package com.boyzhang.projectmobilesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.utils.ContactUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AntiStealSetupWizardFragment2 extends BaseFragment {

	private Button bSetGuideSelectContact;
	private EditText eSetGuideSafePhoneNum;

	@SuppressLint("InflateParams")
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.antisteal_setup_wizard_page2, null);

		// 获取控件
		bSetGuideSelectContact = (Button) v
				.findViewById(R.id.b_set_guide_selectContact);
		eSetGuideSafePhoneNum = (EditText) v
				.findViewById(R.id.e_set_guide_safePhoneNum);
		// 选择联系人点击监听
		bSetGuideSelectContact.setOnClickListener(new OnClickListener() {

			private ArrayList<HashMap<String, String>> contacts;
			private AlertDialog alertDialog;

			@Override
			public void onClick(View v) {
				contacts = ContactUtils.getContact(getActivity());
				if (contacts.size() > 0) {
					AlertDialog.Builder builder = new Builder(getActivity());
					alertDialog = builder.create();
					View view = View.inflate(getActivity(),
							R.layout.view_list_contact, null);

					ListView lvContact = (ListView) view
							.findViewById(R.id.lv_list_contact);
					lvContact.setAdapter(new SimpleAdapter(getActivity(),
							contacts, R.layout.view_list_contact_item,
							new String[] { "name" },
							new int[] { R.id.tv_list_contact_item }));
					lvContact.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String phone = contacts.get(position).get("phone");
							eSetGuideSafePhoneNum.setText(phone);
							alertDialog.dismiss();
						}
					});

					alertDialog.setView(view, 0, 0, 0, 0);
					alertDialog.show();
				} else {
					Toast.makeText(getActivity(), "手机中没有存储联系人",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		return v;
	}

	@Override
	public boolean checkInput() {
		// 获取用户输入的手机号码并保持到sharePrefrences中
		String phoneNum = eSetGuideSafePhoneNum.getText().toString();
		if (TextUtils.isEmpty(phoneNum)) {
			Toast.makeText(getActivity(), "安全号码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		getActivity()
				.getSharedPreferences("config", Activity.MODE_PRIVATE)
				.edit()
				.putString("safePhoneNum",
						phoneNum.replace(" ", "").replace("-", "")).commit();
		return true;
	}

}
