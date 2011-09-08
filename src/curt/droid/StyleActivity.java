package curt.droid;

import java.io.IOException;
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

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class StyleActivity extends ListActivity {

	HttpClient client;
	final static String api_url = "http://docs.curthitch.biz/api/";
	String year = "";
	String make = "";
	String model = "";
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.style);
		
		client = new DefaultHttpClient();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			year = extras.getString("year");
			make = extras.getString("make");
			model = extras.getString("model");
			
			ArrayList<String> styles = new ArrayList<String>();
			try{
				styles = GetStyles(year,make,model);
				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,styles));
				
				final ListView style_list = getListView();
				style_list.setTextFilterEnabled(true);
				
				style_list.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						String style = style_list.getItemAtPosition(position).toString();
						Intent part_list = new Intent(StyleActivity.this, PartListingActivity.class);
						part_list.putExtra("year", year);
						part_list.putExtra("make", make);
						part_list.putExtra("model", model);
						part_list.putExtra("style", style);
						try{
							startActivity(part_list);
						}catch(ActivityNotFoundException e){
							Toast.makeText(StyleActivity.this, "Error finding parts", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}catch(ClientProtocolException e1){
				e1.printStackTrace();
			}catch(IOException e2){
				e2.printStackTrace();
			}catch(Exception e3){
				e3.printStackTrace();
			}
		}
	}
	
	public ArrayList<String> GetStyles(String year, String make, String model) throws ClientProtocolException, IOException{
		StringBuilder url = new StringBuilder(api_url);
		url.append("GetStyle?year="+year+"&make="+make+"&model="+model+"&dataType=JSON");
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
			JSONArray styles = null;
			try{
				styles = new JSONArray(data);
			}catch(JSONException e1){
				e1.printStackTrace();
			}
			ArrayList<String> styleArray = new ArrayList<String>();
    		for(int i = 0; i < styles.length(); i++){
    			try {
					String m = styles.getString(i);
					try{
						styleArray.add(m);
					}catch(Exception e){
						e.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    		return styleArray;
		}
		Toast.makeText(StyleActivity.this, status + " Error", Toast.LENGTH_SHORT).show();
		return new ArrayList<String>();
	}
	
	
}
