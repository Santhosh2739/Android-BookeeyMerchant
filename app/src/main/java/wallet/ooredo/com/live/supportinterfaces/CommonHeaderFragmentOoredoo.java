package wallet.ooredo.com.live.supportinterfaces;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wallet.ooredo.com.live.R;

public class CommonHeaderFragmentOoredoo extends Fragment {
	

//	public static final String tag = CommonHeaderFragment.class.getCanonicalName();

	public CommonHeaderFragmentOoredoo() {

	}

	String _headerText1 = null;


	@Override
	public void onStart() {
		super.onStart();
		((TextView) getView().findViewById(R.id.common_header_fragment_text1)).setText(_headerText1);

	}

	/*public CommonHeaderFragmentOoredoo(String headerText1) {
		updateReferences(headerText1);
	}*/

	public void updateHeaderText1(String headerText1) {
		_headerText1 = headerText1;
	}

	
	

	public void updateReferences(String headerText1) {
		_headerText1 = headerText1;

		try {
			((TextView) getView().findViewById(R.id.common_header_fragment_text1)).setText(_headerText1);
			
		} catch (Exception e) {

		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.common_header_fragment_ooredoo, container, false);
		((TextView) rootView.findViewById(R.id.common_header_fragment_text1)).setText(_headerText1);

		return rootView;
	}
}
