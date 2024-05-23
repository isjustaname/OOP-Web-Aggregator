import java.io.IOException;
import java.lang.module.ModuleDescriptor.Builder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import configPackage.ScrapeData;

public class JsonRead {
    /**
     * Đọc các nội dung trong file output, trả về 1 list ScrapeData (phần content của ScrapeData cần sửa lại thành String)
     * Đồng thời gộp content array về 1 String
     * @param output_file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException
     */
    public static List<ScrapeData> readJson(String output_file) throws FileNotFoundException, IOException, ParseException{
		Object obj = new JSONParser().parse(new FileReader(output_file));
		JSONArray ja = (JSONArray) obj;  //Lấy dữ liệu từ file Json
        Iterator<?> itr = ja.iterator();
		List<ScrapeData> dataList = new ArrayList<>();
 		while (itr.hasNext()){
            JSONObject json_object = (JSONObject) itr.next();
            String url = (String) json_object.get("url");
            String web_url = (String) json_object.get("web_url");
            String type = (String) json_object.get("type");
            String description = (String) json_object.get("description");
            String title = (String) json_object.get("title");
            String content;
            if(json_object.get("content") instanceof JSONArray){
                content = contentCombiner((JSONArray) json_object.get("content"));
            }
            else{
                content = (String) json_object.get("content");
            }
            String create_date = (String) json_object.get("create_date");
            String tag = (String) json_object.get("tag");
            String author = (String) json_object.get("author");

            description = description.replace("'", "’").replace("&", "and");
            title = title.replace("'", "’").replace("&", "and");
            content = content.replace("'", "’").replace("&", "and");

            ScrapeData data = new ScrapeData(url, web_url, type, description, title, content, create_date, tag, author);
            dataList.add(data);
		}
        return dataList;
    }
    
    private static String contentCombiner(JSONArray content_json){
        Iterator<?> content_itr = content_json.iterator();
        StringBuilder content_builder = new StringBuilder();
        while (content_itr.hasNext()) {
            String line_json = (String) content_itr.next();
            String line = line_json;
            if(line.isEmpty() || line == "") {
                continue;
            }
            content_builder.append(line);
            content_builder.append("\n");
        }
        return content_builder.toString();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        Gson gson = new Gson();
        List<ScrapeData> dataList = readJson("Data/Output copy.json");

        // Viết Json theo từng dòng 1 mà vẫn giữ dạng JSONArray
        BufferedWriter writer = new BufferedWriter(new FileWriter("Data/Output.json"));
        writer.write("[");
        for(ScrapeData data : dataList){
            String json_data = gson.toJson(data);
            writer.write(json_data);
            writer.newLine();
            if(dataList.getLast() != data) writer.write(",");
        }
        writer.write("]");
        writer.close();
    }
}
