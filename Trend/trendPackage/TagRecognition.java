package trendPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.print.DocFlavor.STRING;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Bộ phận liệt kê hết các tag và thời gian viết gắn với từng web để bộ phận tìm trend làm việc.
 * Đồng thời tìm các từ khóa quan trọng ở "title" và "description" để gắn làm tag nếu web không có tag
 * @param json_file
 */
public class TagRecognition {
    public static List<TrendData> web_tag_data = new ArrayList<>();
    private static Set<String> tag_base = new HashSet<>();
    private static Set<String> full_tag = new HashSet<>();
    
    
	static{
        String[] base = {"crypto", "currency", "centraliz", "mining", "block", "token", "immutable", "atomic", "permission", "regulat", "hash", "market"};

        String[] full = {"blockchain", "ethereum", "cryptocurrency", "ledger", "smart contract", "consensus",
            "immutable", "transparency", "trustless", "nodes", "mining", "hash", "merkle tree",
            "fork", "wallet", "bitcoin", "peer-to-peer", "proof of work",
            "proof of stake", "private key", "public key", "block reward", "hashrate",
            "scalability", "interoperability", "privacy coin", "sidechain", "forking", "gas",
            "initial coin offering", "decentralized finance", "centralized finance", "51% attack",
            "double spending", "tokenization", "cryptographic hash function", "block height", "oracles", "atomic swap",
            "difficulty", "hard fork", "soft fork", "cold storage", "hot wallet", "cryptography",
            "address", "block explorer", "block header", "genesis block", "cross-border transactions", "smart property",
            "immutable storage", "gas limit", "gas price", "plasma cash", "rollups", "state channels",
            "atomicity", "interoperable", "decentralized identifier", "off-chain", "on-chain",
            "network congestion", "smart oracle", "token curated registry", "cryptoeconomics", "tokenomics", "hard cap", "soft cap",
            "whitepaper", "roadmap", "crowdsale", "square", "cash app"};

        Collections.addAll(tag_base, base);
        Collections.addAll(full_tag, full);
	} 

    /**
     * Tìm và liệt kê các thông tin cần thiết (tag và create_date) vào web_tag_data
     */
    public static void tagScrapping(JSONArray json_file){
        Iterator<?> itr = json_file.iterator();
 		while (itr.hasNext()) 
		{
            TrendData trend_data = new TrendData();
			JSONObject json_object = (JSONObject) itr.next();//Đọc các dữ liệu của từng web một
            String tag = (String) json_object.get("tag");
            if(tag.isEmpty() == false && tag != ""){
                trend_data.tag_list.addAll(extractWord(tag, "[,]"));
            }
            Set<String> list_of_tag = new HashSet<>();
            //Web không có tag, phải lọc từ phần title và descripsion
            StringBuilder paragraph = new StringBuilder();
            paragraph.append(json_object.get("title")).append(' ');
            paragraph.append(json_object.get("description"));
            for(String key_word : full_tag){
                if(paragraph.toString().toLowerCase().contains(key_word)){
                    key_word = DataExtract.upperCaseFirst(key_word);
                    list_of_tag.add(key_word); //Tìm từ dãy có sẵn
                }
            }
            List<String> list_of_word = extractWord(paragraph.toString(), "[ ]");
            //Dần tìm từng từ một và nhận diện xem từ ấy có liên quan đến blockchain không
            for(String word : list_of_word){
                if(word == null || word == "") continue;
                if(isBlockchainKeyword(word) == true){
                    //Từ có 2 chữ viết hoa chở lên, có thể là từ khóa, cho vào tag
                    word = DataExtract.upperCaseFirst(word);
                    list_of_tag.add(word);
                }
            }
            trend_data.tag_list.addAll(list_of_tag);
//            System.out.println("new tag:" + list_of_tag + " in "  + paragraph.toString());
            trend_data.published_date = (String) json_object.get("create_date");
            trend_data.filterKeyWord();
            web_tag_data.add(trend_data);
        }
    }

    	/**
	 * Phân tách các nội dung trong một dãy
	 * @param split_regex: kí hiệu dùng làm điểm phân tách
	 */
	private static List<String> extractWord(String paragraph,String split_regex) {
		List<String> list_of_word = new ArrayList<>();
        String[] words = paragraph.split(split_regex);
        for(int i = 0; i<words.length; i++){
            //Lọc các dấu thừa ra trong từ
            words[i] = polishWord(words[i]);
        }
        Collections.addAll(list_of_word, words);
		return list_of_word;
	}
    
    /**
     * Kiểm tra xem từ được cho có phải là từ khóa (Liên quan đến blockchain ) hay không
     * @param word
     * @return
     */
    public static boolean isBlockchainKeyword(String word){
        if(word.matches("\\w*[A-Z]\\w*[A-Z]\\w*")){
            return true;
        }
        word = word.toLowerCase();
        for(String key_word : tag_base){
            if(word.contains(key_word)){
//                System.out.println(word + " : " +  key_word + " accept");
                return true;
            }
        }
        if(full_tag.contains(word)) return true;
        return false;
    }
    
	/**
	 * Lọc và chỉnh sửa các từ 
	 * @param word
	 */
	private static String polishWord(String word) {
		if(word.isEmpty()) return "";
        word = word.replaceAll("^[?'’,._ \\-\"]+", ""); //Lọc dấu đằng trước
        word = word.replaceAll("[?'’,._\\-\"]+.*", ""); //Lọc dấu đằng sau
        word = word.replaceAll("[ ]$", ""); //Nhỡ có dấu cách đằng cuối
		return word;
		
	}
	
}
