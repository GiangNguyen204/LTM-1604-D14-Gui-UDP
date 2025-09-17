<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   🚀 GỬI TIN NHẮN BROADCAST QUA UDP
</h2>
<div align="center">
    <p align="center">
        <img src="docs/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="docs/fitdnu_logo.png" alt="FIT DNU Logo" width="180"/>
        <img src="docs/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

---

## 📖 1. Giới thiệu hệ thống
Đề tài xây dựng ứng dụng **gửi và nhận tin nhắn broadcast qua giao thức UDP** trong mạng LAN.  
Ứng dụng có các tính năng chính:
- Gửi tin nhắn một lần hoặc theo chế độ **Auto Send** (tự động định kỳ).  
- **Lắng nghe (Listen)** trên cổng UDP để nhận broadcast từ nhiều máy khác nhau.  
- Hiển thị log chi tiết gồm: **Thời gian, IP nguồn, Cổng nguồn, Nội dung tin nhắn**.  
- Lưu lại **lịch sử tin nhắn đã gửi** và cho phép chọn lại nhanh.  
- Xuất log ra file **CSV** để phân tích bằng Excel.  
- Giao diện **mềm mại, hiện đại** nhờ áp dụng theme Nimbus + bo góc (SoftUI).  

---

## 📖 2. Công nghệ sử dụng
Ứng dụng được phát triển bằng:
- **Ngôn ngữ lập trình**: [![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)  
- **Thư viện UI**: [![Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)  
- **JDK**: [![JDK](https://img.shields.io/badge/JDK-8+-green?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)  
- **Mô hình kiến trúc**: tách lớp rõ ràng:
  - `UDP`: core xử lý mạng (Sender, Receiver, NetUtils, Config).  
  - `Client`: giao diện người dùng (BroadcastUI, SoftUI, ReceiverService, HistoryStore).  

---

## 📖 3. Một số hình ảnh hệ thống
👉 *(Bạn thay link ảnh thực tế sau khi chạy ứng dụng)*  

- **Giao diện chính ứng dụng**  
  ![UI Screenshot](docs/ui_main.png)  

- **Chế độ Auto Send + Log hiển thị**  
  ![Auto Send Screenshot](docs/ui_autosend.png)  

- **Xuất CSV và mở bằng Excel**  
  ![CSV Screenshot](docs/ui_csv.png)  

---

## 📖 4. Các bước cài đặt
1. **Clone source code**  
   ```bash
   git clone https://github.com/yourusername/broadcastUDP.git
   ```
2. **Mở project trong IDE** (Eclipse / IntelliJ).  
3. **Cấu hình JDK**: chọn JDK 8 hoặc cao hơn.  
4. **Chạy ứng dụng**:
   - Mở class `Client.BroadcastUI` → Run As → Java Application.  
   - Bấm **Start Listen** để bật chế độ lắng nghe.  
   - Bấm **Send** hoặc bật **Auto Send** để gửi broadcast.  
5. **Kiểm thử trên nhiều máy**:
   - Đảm bảo các máy cùng mạng LAN (cùng subnet).  
   - Bật Listen trên 1 máy, gửi từ máy khác → log sẽ hiển thị.  

---

## 📖 5. Liên hệ
- 📧 Email: **your_email@example.com**  
- 📞 SĐT: **0123 456 789**  
- 🌐 Website/FB: *(bạn thêm sau nếu có)*  

---

✍️ *README này được thiết kế để thầy/cô và người dùng hiểu nhanh về hệ thống, có ảnh minh họa, và hướng dẫn cài đặt rõ ràng.*
