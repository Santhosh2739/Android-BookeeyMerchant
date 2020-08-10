//package wallet.ooredo.com.live.mainmenu;
//
//import java.util.List;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import wallet.ooredo.com.live.R;
//
//public class MainMenuGridViewAdapter extends BaseAdapter {
//	private Context mContext;
//	private List<MainMenuGridInfo> mListAppInfo;
//
//	public MainMenuGridViewAdapter(Context context, List<MainMenuGridInfo> list) {
//		mContext = context;
//		mListAppInfo = list;
//	}
//
//	@Override
//	public int getCount() {
//		return mListAppInfo.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return mListAppInfo.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		MainMenuGridInfo entry = mListAppInfo.get(position);
//		ViewHolder holder = null;
//
//		if (convertView == null) {
//			LayoutInflater inflater = LayoutInflater.from(mContext);
//
//			convertView = inflater.inflate(R.layout.layout_appinfo, null);
//
//			holder = new ViewHolder();
//
//			holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
//
//			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
//
//			holder.tvArabicName = (TextView) convertView.findViewById(R.id.tvArabicName);
//
//			convertView.setTag(holder);
//
//		} else {
//
//			holder = (ViewHolder) convertView.getTag();
//		}
//		if(entry.isSelectorMode()){
//			holder.ivIcon.setBackgroundResource(entry.getSelectorId());
//		}else{
//			holder.ivIcon.setImageBitmap(entry.getIcon());
//		}
//
//		holder.tvName.setText(entry.getName());
////		holder.tvArabicName.setText(entry.getArbicName());
//		holder.tag = entry.getTag();
//
//		return convertView;
//	}
//
//	public static class ViewHolder {
//		public ImageView ivIcon;
//		public TextView tvName;
//		public TextView tvArabicName;
//		public int tag;
//
//
//
//	}
//
//}