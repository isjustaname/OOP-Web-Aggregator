package trendPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonArray;

/**
 * Bộ phận liệt kê hết các tag và thời gian viết gắn với từng web để bộ phận tìm trend làm việc.
 * Đồng thời tìm các từ khóa quan trọng ở "title" và "description" để gắn làm tag nếu web không có tag
 * @param jsonFile
 */
public class TagRecognition {
    public static List<TrendData> webTagData = new ArrayList<>();
    private static Set<String> tagBase = new HashSet<>();
    private static Set<String> fullTag = new HashSet<>();
    
    
	static{
        String[] base = {"crypto", "currency", "centraliz", "mining", "block", "token", "inance", 
        "immutable", "atomic", "permission", "regulat", "hash", "market", "coin"};

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
            "network congestion", "smart oracle", "token curated registry", "cryptoeconomics", "tokenomics", "hard cap", 
            "soft cap", "whitepaper", "roadmap", "crowdsale", "square", "cash app"};

        Collections.addAll(tagBase, base);
        Collections.addAll(fullTag, full);
	} 

    private JsonArray jsonFile;

    public TagRecognition(JSONArray jsonFile){
        tagScrapping(jsonFile);
    }

    /**
     * Tìm và liệt kê các thông tin cần thiết (tag và createDate) vào webTagData
     */
    public void tagScrapping(JSONArray jsonFile){
        Iterator<?> itr = jsonFile.iterator();
 		while (itr.hasNext()) 
		{
            TrendData trendData = new TrendData();
			JSONObject jsonObject = (JSONObject) itr.next();//Đọc các dữ liệu của từng web một
            String tags = (String) jsonObject.get("tag");
            if(tags.isEmpty() == false && tags != ""){
                List<String> tagList = extractWord(tags, "[,]");
                trendData.tagList.addAll(filterKeyWord(tagList));
            }
            Set<String> listOfTag = new HashSet<>();
            //Web không có tag, phải lọc từ phần title và descripsion
            StringBuilder paragraph = new StringBuilder();
            paragraph.append(jsonObject.get("title")).append(' ');
            paragraph.append(jsonObject.get("description"));
            for(String keyWord : fullTag){
                if(paragraph.toString().toLowerCase().contains(keyWord)){
                    keyWord = DataExtract.upperCaseFirst(keyWord);
                    listOfTag.add(keyWord); //Tìm từ dãy có sẵn
                }
            }
            List<String> listOfWord = extractWord(paragraph.toString(), "[ ]");
            //Dần tìm từng từ một và nhận diện xem từ ấy có liên quan đến blockchain không
            for(String word : listOfWord){
                if(word == null || word == "") continue;
                if(isBlockchainKeyword(word) == true){
                    //Từ có 2 chữ viết hoa chở lên, có thể là từ khóa, cho vào tag
                    word = DataExtract.upperCaseFirst(word);
                    listOfTag.add(word);
                }
            }
            trendData.tagList.addAll(listOfTag);

            trendData.publishedDate = (String) jsonObject.get("createDate");
            webTagData.add(trendData);
        }
    }

    	/**
	 * Phân tách các nội dung trong một dãy
	 * @param splitRegex: kí hiệu dùng làm điểm phân tách
	 */
	private List<String> extractWord(String paragraph,String splitRegex) {
		List<String> listOfWord = new ArrayList<>();
        String[] words = paragraph.split(splitRegex);
        for(int i = 0; i<words.length; i++){
            //Lọc các dấu thừa ra trong từ
            words[i] = polishWord(words[i]);
        }
        Collections.addAll(listOfWord, words);
		return listOfWord;
	}

    private List<String> filterKeyWord(List<String> list){
        List<String> copyList = new ArrayList<>(list);
        for(String word : copyList){
            if(isBlockchainKeyword(word) == false){
                list.remove(word);
            }
        }
        return list;
    }
    
    /**
     * Kiểm tra xem từ được cho có phải là từ khóa (Liên quan đến blockchain ) hay không
     * @param word
     * @return
     */
    public boolean isBlockchainKeyword(String word){
        if(word.matches("\\w*[A-Z]\\w*[A-Z]\\w*")){
            return true;
        }
        word = word.toLowerCase();
        for(String keyWord : tagBase){
            if(word.contains(keyWord)){
//                System.out.println(word + " : " +  keyWord + " accept");
//                fullTag.add(word);
                if(word == "cryptorunner") System.out.println("OK");
                return true;
            }
        }
        if(fullTag.contains(word)) return true;
        return false;
    }
    
	/**
	 * Lọc và chỉnh sửa các từ 
	 * @param word
	 */
	private String polishWord(String word) {
		if(word.isEmpty()) return "";
        word = word.replaceAll("^[?'’,._:)(!# \\-\"]+", ""); //Lọc dấu đằng trước
        word = word.replaceAll("[?'’,._:)(!#\\-\"]+.*", ""); //Lọc dấu đằng sau
        word = word.replaceAll("[ ]$", ""); //Nhỡ có dấu cách đằng cuối
		return word;
		
	}
	
}
