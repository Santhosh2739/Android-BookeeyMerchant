/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.google.zxing.client.android.result;

import br.com.google.zxing.Result;
import br.com.google.zxing.client.android.Contents;
import br.com.google.zxing.client.android.Intents;
import br.com.google.zxing.client.android.LocaleManager;
import br.com.google.zxing.client.android.PreferencesActivity;
import br.com.google.zxing.client.result.ParsedResult;
import br.com.google.zxing.client.result.ParsedResultType;
import br.com.google.zxing.client.result.ResultParser;
import wallet.ooredo.com.live.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A base class for the Android-specific barcode handlers. These allow the app to polymorphically
 * suggest the appropriate actions for each data type.
 *
 * This class also contains a bunch of utility methods to take common actions like opening a URL.
 * They could easily be moved into a helper object, but it can't be static because the Activity
 * instance is needed to launch an intent.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public abstract class ResultHandler {

  private static final String TAG = ResultHandler.class.getSimpleName();

  private static final DateFormat DATE_FORMAT;
  static {
    DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    // For dates without a time, for purposes of interacting with Android, the resulting timestamp
    // needs to be midnight of that day in GMT. See:
    // http://code.google.com/p/android/issues/detail?id=8330
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
  }
  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);

  private static final String GOOGLE_SHOPPER_PACKAGE = "com.google.android.apps.shopper";
  private static final String GOOGLE_SHOPPER_ACTIVITY = GOOGLE_SHOPPER_PACKAGE +
      ".results.SearchResultsActivity";
  private static final String MARKET_URI_PREFIX = "market://details?id=";
  private static final String MARKET_REFERRER_SUFFIX =
      "&referrer=utm_source%3Dbarcodescanner%26utm_medium%3Dapps%26utm_campaign%3Dscan";

  private static final String[] EMAIL_TYPE_STRINGS = {"home", "work", "mobile"};
  private static final String[] PHONE_TYPE_STRINGS = {"home", "work", "mobile", "fax", "pager", "main"};
  private static final String[] ADDRESS_TYPE_STRINGS = {"home", "work"};
  private static final int[] EMAIL_TYPE_VALUES = {
      ContactsContract.CommonDataKinds.Email.TYPE_HOME,
      ContactsContract.CommonDataKinds.Email.TYPE_WORK,
      ContactsContract.CommonDataKinds.Email.TYPE_MOBILE,
  };
  private static final int[] PHONE_TYPE_VALUES = {
      ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
      ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
      ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
      ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
      ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
      ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
  };
  private static final int[] ADDRESS_TYPE_VALUES = {
      ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
      ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK,
  };
  private static final int NO_TYPE = -1;

  public static final int MAX_BUTTON_COUNT = 4;

  private final ParsedResult result;
  private final Activity activity;
  private final Result rawResult;
  private final String customProductSearch;

  private final DialogInterface.OnClickListener shopperMarketListener =
      new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
      launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI_PREFIX +
          GOOGLE_SHOPPER_PACKAGE + MARKET_REFERRER_SUFFIX)));
    }
  };

  ResultHandler(Activity activity, ParsedResult result) {
    this(activity, result, null);
  }

  ResultHandler(Activity activity, ParsedResult result, Result rawResult) {
    this.result = result;
    this.activity = activity;
    this.rawResult = rawResult;
    this.customProductSearch = parseCustomSearchURL();

    // Make sure the Shopper button is hidden by default. Without this, scanning a product followed
    // by a QR Code would leave the button on screen among the QR Code actions.
//    View shopperButton = activity.findViewById(R.id.shopper_button);
//    shopperButton.setVisibility(View.GONE);
  }

  public ParsedResult getResult() {
    return result;
  }

  boolean hasCustomProductSearch() {
    return customProductSearch != null;
  }

  Activity getActivity() {
    return activity;
  }

  /**
   * Indicates how many buttons the derived class wants shown.
   *
   * @return The integer button count.
   */
  public abstract int getButtonCount();

  /**
   * The text of the nth action button.
   *
   * @param index From 0 to getButtonCount() - 1
   * @return The button text as a resource ID
   */
  public abstract int getButtonText(int index);


  /**
   * Execute the action which corresponds to the nth button.
   *
   * @param index The button that was clicked.
   */
  public abstract void handleButtonPress(int index);

  /**
   * Some barcode contents are considered secure, and should not be saved to history, copied to
   * the clipboard, or otherwise persisted.
   *
   * @return If true, do not create any permanent record of these contents.
   */
  public boolean areContentsSecure() {
    return false;
  }

  /**
   * The Google Shopper button is special and is not handled by the abstract button methods above.
   *
   * @param listener The on click listener to install for this button.
   */
  void showGoogleShopperButton(View.OnClickListener listener) {
//    View shopperButton = activity.findViewById(R.id.shopper_button);
//    shopperButton.setVisibility(View.VISIBLE);
//    shopperButton.setOnClickListener(listener);
  }

  /**
   * Create a possibly styled string for the contents of the current barcode.
   *
   * @return The text to be displayed.
   */
  public CharSequence getDisplayContents() {
    String contents = result.getDisplayResult();
    return contents.replace("\r", "");
  }

  /**
   * A string describing the kind of barcode that was found, e.g. "Found contact info".
   *
   * @return The resource ID of the string.
   */
  public abstract int getDisplayTitle();

  /**
   * A convenience method to get the parsed type. Should not be overridden.
   *
   * @return The parsed type, e.g. URI or ISBN
   */
  public final ParsedResultType getType() {
    return result.getType();
  }

  /**
   * Sends an intent to create a new calendar event by prepopulating the Add Event UI. Older
   * versions of the system have a bug where the event title will not be filled out.
   *
   * @param summary A description of the event
   * @param start   The start time as yyyyMMdd or yyyyMMdd'T'HHmmss or yyyyMMdd'T'HHmmss'Z'
   * @param end     The end time as yyyyMMdd or yyyyMMdd'T'HHmmss or yyyyMMdd'T'HHmmss'Z'
   * @param location a text description of the event location
   * @param description a text description of the event itself
   */
  final void addCalendarEvent(String summary,
                              String start,
                              String end,
                              String location,
                              String description) {
    Intent intent = new Intent(Intent.ACTION_EDIT);
    intent.setType("vnd.android.cursor.item/event");
    long startMilliseconds = calculateMilliseconds(start);
    intent.putExtra("beginTime", startMilliseconds);
    boolean allDay = start.length() == 8;
    if (allDay) {
      intent.putExtra("allDay", true);
    }
    long endMilliseconds;
    if (end == null) {
      if (allDay) {
        // + 1 day
        endMilliseconds = startMilliseconds + 24 * 60 * 60 * 1000;
      } else {
        endMilliseconds = startMilliseconds;
      }
    } else {
      endMilliseconds = calculateMilliseconds(end);
    }
    intent.putExtra("endTime", endMilliseconds);
    intent.putExtra("title", summary);
    intent.putExtra("eventLocation", location);
    intent.putExtra("description", description);
    launchIntent(intent);
  }

  private static long calculateMilliseconds(String when) {
    if (when.length() == 8) {
      // Only contains year/month/day
      Date date;
      synchronized (DATE_FORMAT) {
        date = DATE_FORMAT.parse(when, new ParsePosition(0));
      }
      // Note this will be relative to GMT, not the local time zone
      return date.getTime();
    } else {
      // The when string can be local time, or UTC if it ends with a Z
      Date date;
      synchronized (DATE_TIME_FORMAT) {
       date = DATE_TIME_FORMAT.parse(when.substring(0, 15), new ParsePosition(0));
      }
      long milliseconds = date.getTime();
      if (when.length() == 16 && when.charAt(15) == 'Z') {
        Calendar calendar = new GregorianCalendar();
        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        milliseconds += offset;
      }
      return milliseconds;
    }
  }

  final void addPhoneOnlyContact(String[] phoneNumbers,String[] phoneTypes) {
    addContact(null, null, phoneNumbers, phoneTypes, null, null, null, null, null, null, null, null, null, null);
  }

  final void addEmailOnlyContact(String[] emails, String[] emailTypes) {
    addContact(null, null, null, null, emails, emailTypes, null, null, null, null, null, null, null, null);
  }

  final void addContact(String[] names,
                        String pronunciation,
                        String[] phoneNumbers,
                        String[] phoneTypes,
                        String[] emails,
                        String[] emailTypes,
                        String note,
                        String instantMessenger,
                        String address,
                        String addressType,
                        String org,
                        String title,
                        String url,
                        String birthday) {

    // Only use the first name in the array, if present.
    Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI);
    intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
    putExtra(intent, ContactsContract.Intents.Insert.NAME, names != null ? names[0] : null);

    putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation);

    int phoneCount = Math.min(phoneNumbers != null ? phoneNumbers.length : 0, Contents.PHONE_KEYS.length);
    for (int x = 0; x < phoneCount; x++) {
      putExtra(intent, Contents.PHONE_KEYS[x], phoneNumbers[x]);
      if (phoneTypes != null && x < phoneTypes.length) {
        int type = toPhoneContractType(phoneTypes[x]);
        if (type >= 0) {
          intent.putExtra(Contents.PHONE_TYPE_KEYS[x], type);
        }
      }
    }

    int emailCount = Math.min(emails != null ? emails.length : 0, Contents.EMAIL_KEYS.length);
    for (int x = 0; x < emailCount; x++) {
      putExtra(intent, Contents.EMAIL_KEYS[x], emails[x]);
      if (emailTypes != null && x < emailTypes.length) {
        int type = toEmailContractType(emailTypes[x]);
        if (type >= 0) {
          intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], type);
        }
      }
    }

    // No field for URL, birthday; use notes
    StringBuilder aggregatedNotes = new StringBuilder();
    for (String aNote : new String[] { url, birthday, note }) {
      if (aNote != null) {
        if (aggregatedNotes.length() > 0) {
          aggregatedNotes.append('\n');
        }
        aggregatedNotes.append(aNote);
      }
    }
    if (aggregatedNotes.length() > 0) {
      putExtra(intent, ContactsContract.Intents.Insert.NOTES, aggregatedNotes.toString());
    }
    
    putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger);
    putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address);
    if (addressType != null) {
      int type = toAddressContractType(addressType);
      if (type >= 0) {
        intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type);
      }
    }
    putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org);
    putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title);
    launchIntent(intent);
  }

  private static int toEmailContractType(String typeString) {
    return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES);
  }

  private static int toPhoneContractType(String typeString) {
    return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES);
  }

  private static int toAddressContractType(String typeString) {
    return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES);
  }

  private static int doToContractType(String typeString, String[] types, int[] values) {
    if (typeString == null) {
      return NO_TYPE;
    }
    for (int i = 0; i < types.length; i++) {
      String type = types[i];
      if (typeString.startsWith(type) || typeString.startsWith(type.toUpperCase(Locale.ENGLISH))) {
        return values[i];
      }
    }
    return NO_TYPE;
  }

  final void shareByEmail(String contents) {
//    sendEmailFromUri("mailto:", null, activity.getString(R.string.msg_share_subject_line),
//        contents);
  }

  final void sendEmail(String address, String subject, String body) {
    sendEmailFromUri("mailto:" + address, address, subject, body);
  }

  // Use public Intent fields rather than private GMail app fields to specify subject and body.
  final void sendEmailFromUri(String uri, String email, String subject, String body) {
    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(uri));
    if (email != null) {
      intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
    }
    putExtra(intent, Intent.EXTRA_SUBJECT, subject);
    putExtra(intent, Intent.EXTRA_TEXT, body);
    intent.setType("text/plain");
    launchIntent(intent);
  }

  final void shareBySMS(String contents) {
//    sendSMSFromUri("smsto:", activity.getString(R.string.msg_share_subject_line) + ":\n" +
//        contents);
  }

  final void sendSMS(String phoneNumber, String body) {
    sendSMSFromUri("smsto:" + phoneNumber, body);
  }

  final void sendSMSFromUri(String uri, String body) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
    putExtra(intent, "sms_body", body);
    // Exit the app once the SMS is sent
    intent.putExtra("compose_mode", true);
    launchIntent(intent);
  }

  final void sendMMS(String phoneNumber, String subject, String body) {
    sendMMSFromUri("mmsto:" + phoneNumber, subject, body);
  }

  final void sendMMSFromUri(String uri, String subject, String body) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
    // The Messaging app needs to see a valid subject or else it will treat this an an SMS.
    if (subject == null || subject.length() == 0) {
    //  putExtra(intent, "subject", activity.getString(R.string.msg_default_mms_subject));
    } else {
      putExtra(intent, "subject", subject);
    }
    putExtra(intent, "sms_body", body);
    intent.putExtra("compose_mode", true);
    launchIntent(intent);
  }

  final void dialPhone(String phoneNumber) {
    launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
  }

  final void dialPhoneFromUri(String uri) {
    launchIntent(new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
  }

  final void openMap(String geoURI) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(geoURI)));
  }

  /**
   * Do a geo search using the address as the query.
   *
   * @param address The address to find
   * @param title An optional title, e.g. the name of the business at this address
   */
  final void searchMap(String address, CharSequence title) {
    String query = address;
    if (title != null && title.length() > 0) {
      query += " (" + title + ')';
    }
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(query))));
  }

  final void getDirections(double latitude, double longitude) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google." +
        LocaleManager.getCountryTLD(activity) + "/maps?f=d&daddr=" + latitude + ',' + longitude)));
  }

  // Uses the mobile-specific version of Product Search, which is formatted for small screens.
  final void openProductSearch(String upc) {
    Uri uri = Uri.parse("http://www.google." + LocaleManager.getProductSearchCountryTLD(activity) +
        "/m/products?q=" + upc + "&source=zxing");
    launchIntent(new Intent(Intent.ACTION_VIEW, uri));
  }

  final void openBookSearch(String isbn) {
    Uri uri = Uri.parse("http://books.google." + LocaleManager.getBookSearchCountryTLD(activity) +
        "/books?vid=isbn" + isbn);
    launchIntent(new Intent(Intent.ACTION_VIEW, uri));
  }

  final void searchBookContents(String isbnOrUrl) {
//    Intent intent = new Intent(Intents.SearchBookContents.ACTION);
//    intent.setClassName(activity, SearchBookContentsActivity.class.getName());
//    putExtra(intent, Intents.SearchBookContents.ISBN, isbnOrUrl);
//    launchIntent(intent);
  }

  final void openURL(String url) {
    launchIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
  }

  final void webSearch(String query) {
    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
    intent.putExtra("query", query);
    launchIntent(intent);
  }

  final void openGoogleShopper(String query) {

    // Construct Intent to launch Shopper
    Intent intent = new Intent(Intent.ACTION_SEARCH);
    intent.setClassName(GOOGLE_SHOPPER_PACKAGE, GOOGLE_SHOPPER_ACTIVITY);
    intent.putExtra(SearchManager.QUERY, query);

    // Is it available?
    PackageManager pm = activity.getPackageManager();
    Collection<?> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

    if (availableApps != null && !availableApps.isEmpty()) {
      // If something can handle it, start it
      activity.startActivity(intent);
    } else {
      // Otherwise offer to install it from Market.
//      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//      builder.setTitle(R.string.msg_google_shopper_missing);
//      builder.setMessage(R.string.msg_install_google_shopper);
//      builder.setIcon(R.drawable.shopper_icon);
//      builder.setPositiveButton(R.string.button_ok, shopperMarketListener);
//      builder.setNegativeButton(R.string.button_cancel, null);
//      builder.show();
    }
  }

  void launchIntent(Intent intent) {
    if (intent != null) {
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      //Log.d(TAG, "Launching intent: " + intent + " with extras: " + intent.getExtras());
      try {
        activity.startActivity(intent);
      } catch (ActivityNotFoundException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.app_name);
//        builder.setMessage(R.string.msg_intent_failed);
//        builder.setPositiveButton(R.string.button_ok, null);
        builder.show();
      }
    }
  }

  private static void putExtra(Intent intent, String key, String value) {
    if (value != null && value.length() > 0) {
      intent.putExtra(key, value);
    }
  }

  private String parseCustomSearchURL() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    String customProductSearch = prefs.getString(PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH,
        null);
    if (customProductSearch != null && customProductSearch.trim().length() == 0) {
      return null;
    }
    return customProductSearch;
  }

  String fillInCustomSearchURL(String text) {
    if (customProductSearch == null) {
      return text; // ?
    }
    String url = customProductSearch.replace("%s", text);
    if (rawResult != null) {
      url = url.replace("%f", rawResult.getBarcodeFormat().toString());
      if (url.contains("%t")) {
        ParsedResult parsedResultAgain = ResultParser.parseResult(rawResult);
        url = url.replace("%t", parsedResultAgain.getType().toString());
      }
    }
    return url;
  }

}
