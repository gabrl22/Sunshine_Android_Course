package com.example.gabriel.app.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private final String apiKey = "560e75dcb93bfae3fdee566ea4782578";
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    ArrayAdapter adapter;
    private String location = "";
    SharedPreferences mSharedPreferences;


    public ForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getLocationFromPreferences();
    }


    @Override
    public void onStart() {
        super.onStart();
        updateWeatherData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootview.findViewById(R.id.listview);


        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
//                String forecast = (String) adapter.getItem(position);
                String forecats = (String) parent.getItemAtPosition(position);
//                Toast.makeText(getActivity(), forecats, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("FORECAST", forecats);
                startActivity(intent);
            }
        });


        return rootview;
    }

    private void getLocationFromPreferences() {
        location = mSharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
    }

    private void updateWeatherData() {
        getLocationFromPreferences();
        new FetchWeatherTask().execute(location);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.foreacastfragment, menu);
    }

    private void openPreferredLocation() {
        //Se construye la URI para pasarlo al intent
        String location = mSharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_key));
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(geoLocation);

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getActivity(), "There's no app to open the map", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeatherData();
                return true;
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.open_map:
                openPreferredLocation();

        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


        private final String POSTAL_CODE_PARAM = "q";
        private final String APPID_PARAMS = "APPID";
        private final String UNITS_PARAM = "units";
        private final String DAYS_PARAM = "cnt";

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                adapter.clear();

                for (String weather : strings) {
                    adapter.add(weather);
                }
            }
        }

        @Override
        protected String[] doInBackground(String... params) {
            String[] data = null;

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("http");
            uriBuilder.authority("api.openweathermap.org");
            uriBuilder.appendPath("data");
            uriBuilder.appendPath("2.5");
            uriBuilder.appendPath("forecast");
            uriBuilder.appendPath("daily");
            uriBuilder.appendQueryParameter(POSTAL_CODE_PARAM, params[0]);
            uriBuilder.appendQueryParameter(APPID_PARAMS, apiKey);
            uriBuilder.appendQueryParameter(UNITS_PARAM, "metric");
            uriBuilder.appendQueryParameter(DAYS_PARAM, "7");


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7" + "&appid=" + apiKey);
                URL url2 = new URL(uriBuilder.build().toString());


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url2.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                data = getWeatherDataFromJson(forecastJsonStr, 7);
//                    JSONObject jsonWeather = new JSONObject(forecastJsonStr);
//                    JSONArray list = jsonWeather.getJSONArray("list");
//                    JSONObject oneDay = list.getJSONObject(0);
//                    JSONObject temp = oneDay.getJSONObject("temp");
//                    double maxTemp = temp.getDouble("max");
//                    Log.i(LOG_TAG, "MAX TEMP" + maxTemp);

                Log.i(LOG_TAG, data[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing json");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return data;

        }


    }


    /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh;
        long roundedLow;

        String tempUnit = mSharedPreferences.getString(getString(R.string.pref_temp_key), "metric");

        Resources res = getResources();
        String[] units = res.getStringArray(R.array.pref_temp_values);

        if (tempUnit.equalsIgnoreCase(units[1])) {
            roundedHigh = Math.round(((9 / 5) * high) + 32);
            roundedLow = Math.round(((9 / 5) * low) + 32);
        } else {
            roundedHigh = Math.round(high);
            roundedLow = Math.round(low);
        }

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }
        return resultStrs;

    }

}
