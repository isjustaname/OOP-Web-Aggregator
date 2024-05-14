import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.Gson;

import configPackage.ScrapeData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Datascraper {
    public static void main(String[] args) {
        String inputFilePath = "Data/Url.txt"; // Tên file nhập vào
        String outputFilePath = "Data/Output.json"; // Tên file xuất ra
        
        List<ScrapeData> dataList = new ArrayList<>();
        
        try {
            // Tạo kết nối đọc với file Url.txt 
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            
            String line;
            while ((line = reader.readLine()) != null) {
                String url = line.trim(); // Lấy Url trên từng dòng
                
                // Tạo kết nối HTTP đến Url mục tiêu
                Document doc = Jsoup.connect(url).get();
                
                // Lấy các phần tử cần thiết (<<<<<<<<<<<< Mày sửa chỗ này >>>>>>>>>>>>>>)
                String web_url = url.substring(0, url.indexOf(".com")+5);
                Elements type = doc.select("meta[property $= og:type]");
                Elements description = doc.select("meta[name = description]");
                String title = doc.title();  
                Elements contents = doc.select("div#maincontent p, article.fck_detail p, section.at-body p");
                Elements author = doc.select("meta[property $= author]");
                Elements create_date = doc.select("meta[property $= published_time]");
                Elements tag = doc.select("meta[property $= tag]");
                
                // Tạo đối tượng String lưu từng phần của content
                List<String> contentList = new ArrayList<>();
                
                for (Element content : contents)
                    contentList.add(content.text());
                
                // Tạo đối tượng ScrapedData và đẩy vào dataList 
                ScrapeData data = new ScrapeData(url, web_url, type.attr("content"), description.attr("content"), title,  contentList, create_date.attr("content"), tag.attr("content"), author.attr("content"));
                dataList.add(data);
            }
            
            reader.close();

            Gson gson = new Gson();
            String jsonData = gson.toJson(dataList);

            // Tạo kết nối viết với file Output.json 
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

//            for (ScrapeData Data : dataList) {
                // Chuyển từng phần tử Data trong dataList thành JSON
//                String jsonData = gson.toJson(Data);

                writer.write(jsonData);
//                writer.newLine(); // Xuống dòng
//            }
            writer.close();
            
            
           
            
            System.out.println("Scraping completed: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Program failed to send HTTP request due to internet loss");
        }
    }
}