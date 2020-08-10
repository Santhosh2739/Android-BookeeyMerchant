package wallet.ooredo.com.live.mainmenu;

import android.graphics.Bitmap;

public class MainMenuGridInfo {
	private Bitmap mIcon;
	private int selectorId;
	private int mName;
	private int mTag;
	private boolean isSelectorMode = false;


	public MainMenuGridInfo(Bitmap icon, int name, int tag) {
		mIcon = icon;
		mName = name;
		mTag = tag;
	}


	public MainMenuGridInfo(int selectorId, int name,  int tag) {
		this.setSelectorId(selectorId);
		isSelectorMode = true;
//		mIcon = icon;
		mName = name;
		mTag = tag;

	}
	public boolean isSelectorMode(){
		return isSelectorMode;
	}
	public void setIcon(Bitmap icon) {
		mIcon = icon;
	}

	public Bitmap getIcon() {
		return mIcon;
	}

	public void setName(int name) {
		mName = name;
	}

	public int getName() {
		return mName;
	}

	public void setTag(int _tag) {
		mTag = _tag;
	}

	public int getTag() {
		return mTag;
	}
	public void setSelectorId(int selectorId) {
		this.selectorId = selectorId;
	}
	public int getSelectorId() {
		return selectorId;
	}


}