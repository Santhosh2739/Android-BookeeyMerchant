package wallet.ooredo.com.live.supportinterfaces;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontedTextView extends TextView {
	
	private Context _context = null;
	
	public FontedTextView(Context context) {
		super(context);
		_context=context;
		init();
	}

	public FontedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context=context;
		init();
	}

	public FontedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_context=context;
		init();
	}

	private void init() {
		Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/Roboto-Light.ttf");
		this.setTypeface(font);

	}
}
