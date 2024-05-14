package configPackage;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class ScrapeData{
    // Khai báo thuộc tính cần thiết (<<<<<<<<<<<< Mày sửa chỗ này >>>>>>>>>>>>>>)
    private String url;
    private String web_url;
    private String type;
    private String description;
    private String title;
    private List<String> content;
    private String create_date;
    private String tag;
    private String author;
}

