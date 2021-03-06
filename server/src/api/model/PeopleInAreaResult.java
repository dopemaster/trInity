package api.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Nick Freeman <nfreeman@linkedin.com>
 */
public class PeopleInAreaResult implements AbstractResult
{

	private static final String PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~/connections:(formatted-name,headline,picture-url,location:(name))?format=json";
	private String area;
	private List<HashMap<String, String>> finalResult;
	private Map<String,HashMap<String,String>> uniqueSet;
	private JSONObject finalJSON;

	public PeopleInAreaResult() {
		this.finalResult = new ArrayList<HashMap<String, String>>();
		this.uniqueSet = new HashMap<String,HashMap<String,String>>();
	}
	
	public PeopleInAreaResult(String area) {
		this.area = area;
		this.finalResult = new ArrayList<HashMap<String, String>>();
		this.uniqueSet = new HashMap<String,HashMap<String,String>>();
	}

	public void setQueryResource(String area) {
		this.area = area;
	}

	public void decode() {
		try {
			JSONObject json  = ScribeClient.getResponse(PROTECTED_RESOURCE_URL);

			JSONArray values = json.getJSONArray("values");
			for (int i = 0; i < values.length(); i++) {
				HashMap<String, String> toAdd = new HashMap<String, String>();
				JSONObject memberattr = values.getJSONObject(i);
				if (memberattr.has("formattedName")) {
					String formattedName = memberattr.getString("formattedName");
					if (memberattr.has("location")) {
						JSONObject locationobj = memberattr.getJSONObject("location");
						if (locationobj.has("name")) {
							String locationname = locationobj.getString("name");
							if (locationname.toLowerCase().replaceAll("\\s","").contains(this.area.toLowerCase().replaceAll("\\s",""))) {

								HashMap<String, String> resultToAdd = new HashMap<String, String>();
								resultToAdd.put("name", formattedName);
								String headLine = "";
								if (memberattr.has("headline")) {
									headLine = memberattr.getString("headline");
									resultToAdd.put("headline", memberattr.getString("headline"));
								}
								if (memberattr.has("pictureUrl")) {
									resultToAdd.put("pictureUrl", memberattr.getString("pictureUrl"));
								}
								this.finalResult.add(resultToAdd);
								this.uniqueSet.put(formattedName+headLine,resultToAdd); 
							}

						}
					}
				}
			}

			//System.out.println(finalResult.toString());
			JSONObject jsontoreturn = new JSONObject();
			jsontoreturn.accumulate("values", this.finalResult);
			this.finalJSON = jsontoreturn;
			//System.out.println(this.finalJSON.toString());

			//System.out.println(values.toString());
		} catch (JSONException e) {
			System.out.println("\n\n\n" + e);
		}

	}

	public Map<String,HashMap<String,String>> getUniqueSet() { 
		return uniqueSet;
	}

	public List<HashMap<String, String>> getFinalResult()
	{
		return finalResult;
	}

	public void setFinalResult(List<HashMap<String, String>> finalResult)
	{
		this.finalResult = finalResult;
	}

	public static String getProtectedResourceUrl()
	{
		return PROTECTED_RESOURCE_URL;
	}

	public JSONObject getFinalJSON()
	{
		return finalJSON;
	}

	public void setFinalJSON(JSONObject finalJSON)
	{
		this.finalJSON = finalJSON;
	}

	public String getArea()
	{
		return area;
	}

	public void setArea(String area)
	{
		this.area = area;
	}

	public static void main(String args[])
	{
		PeopleInAreaResult result = new PeopleInAreaResult("san francisco");
		result.decode();
	}

}
