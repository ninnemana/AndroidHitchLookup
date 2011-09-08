package curt.droid;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class PartListingActivity extends ListActivity {

	HttpClient client;
	final static String api_url = "http://docs.curthitch.biz/api/";
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.part_listing);
		client = new DefaultHttpClient();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			String year = URLEncoder.encode(extras.getString("year"));
			String make = URLEncoder.encode(extras.getString("make"));
			String model = URLEncoder.encode(extras.getString("model"));
			String style = URLEncoder.encode(extras.getString("style"));
			try {
				JSONArray parts = GetParts(year, make, model, style);
				ArrayList<String> part_ids = new ArrayList<String>();
				for(int i = 0; i < parts.length(); i++){
					try {
						JSONObject part = (JSONObject) parts.get(i);
						String part_id = part.getString("partID");
						part_ids.add(part_id);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,part_ids));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public JSONArray GetParts(String year, String make, String model, String style) throws ClientProtocolException, IOException{
		StringBuilder url = new StringBuilder(api_url);
		url.append("GetParts?year="+year+"&make="+make+"&model="+model+"&style="+style+"&dataType=JSON");
		HttpGet get = new HttpGet(url.toString());
		HttpResponse resp = null;
		try{
    		resp = client.execute(get);
    	}catch(UnknownHostException e){
    		e.printStackTrace();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		int status = resp.getStatusLine().getStatusCode();
		if(status == 200){
			HttpEntity entity = resp.getEntity();
			String data = EntityUtils.toString(entity);
			JSONArray part_json = null;
			try{
				part_json = new JSONArray(data);
			}catch(JSONException e1){
				e1.printStackTrace();
			}
			return part_json;
		}
		Toast.makeText(PartListingActivity.this, "Failed to find available parts.", Toast.LENGTH_SHORT).show();
		return null;
	}

}
