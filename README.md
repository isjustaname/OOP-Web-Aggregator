Folder Trend là toàn bộ phần t ( còn 2 cái class Pair, PairArray ở Config )

TrendFinder là file java chính, mỗi object của TrendFinder sẽ được gắn với 1 file json và chỉ đào dữ liệu từ file json đấy, có 3 hàm ứng với 3 phần Trend:
(Lưu ý: Hàm sẽ trả về 1 dãy PairArray, muốn thấy giá trị trên terminal thì dùng hàm .printPair() gắn với dãy đấy)
- 1: Sự thay đổi theo thời gian của lần xuất hiện của 1 từ:
    + Nhập vào 1 từ cần tìm (VD: "Bitcoin") và số năm trước đây (bắt đầu từ năm nay) cần biểu diễn. Tùy theo số lượng năm nhiều hay ít sẽ biểu diễn thời gian theo cách khác nhau (Xem rõ các mốc thời gian ở comment trên hàm và file TimeDisplay)
    1 input, output VD:
    Input: trend_finder.trendOverTime("Bitcoin", 15)
    Output: 
    2024 : 1.0
    2023 : 0.0
    2022 : 2.0
    2021 : 1.0
    2020 : 2.0
    2019 : 0.0
    2018 : 0.0
    2017 : 0.0
    2016 : 0.0
    2015 : 0.0
    2014 : 4.0
    2013 : 1.0
    2012 : 0.0
    2011 : 0.0
    2010 : 0.0
    2009 : 0.0
- 2: Phần trăm các homepage được cào
    + Hàm lấy thông tin từ phần "web_url"
    Chỉ cần gọi hàm trend_finder.extractedWeb() là cho kết quả
    VD: Output:
    https://www.coindesk.com/ : 60.0
    http://www.blockchain.new : 40.0
- 3: Các tag trending nhất:
    + Hàm tìm các tag có số lần xuất hiện nhiều nhất trong tất cả các bài báo, liệt kê theo thứ tự giảm dần. Cần nhập số tag trên top muốn lấy
    VD: Input: trend_finder.findMostTrending(14)
    Output:
    CoinDesk : 18.0
    Bitcoin : 12.0
    Crypto : 11.0
    Ethereum : 6.0
    Markets : 5.0
    DeFi : 5.0
    FTX : 4.0
    Market : 4.0
    Regulation : 3.0
    Markets News : 3.0
    TBTC : 2.0
    Blockchain : 2.0
    Regulated : 2.0
    ICOs : 2.0

M vui chưa An, t giải thích rõ ràng rồi.
