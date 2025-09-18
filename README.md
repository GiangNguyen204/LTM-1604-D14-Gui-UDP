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

    <p align="center">
        <a href="https://www.facebook.com/DNUAIoTLab"><img src="https://img.shields.io/badge/AIoTLab-green?style=for-the-badge" alt="AIoTLab"></a>
        <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin"><img src="https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge" alt="FIT DNU"></a>
        <a href="https://dainam.edu.vn"><img src="https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge" alt="DaiNam University"></a>
    </p>
</div>

---

## 📖 1. Giới thiệu hệ thống
Đề tài xây dựng ứng dụng **gửi và nhận tin nhắn broadcast qua giao thức UDP** trong mạng LAN.

<p align="center">
    <img width="717" height="689" alt="Kiến trúc tổng quan" src="https://github.com/user-attachments/assets/2a7d4e8a-a4b7-4aa4-8c75-6132402027b8" />
</p>

Ứng dụng có các tính năng chính:
- Gửi tin nhắn một lần hoặc theo chế độ **Auto Send** (tự động định kỳ).  
- **Lắng nghe (Listen)** trên cổng UDP để nhận broadcast từ nhiều máy khác nhau.  
- Hiển thị log chi tiết gồm: **Thời gian, IP nguồn, Cổng nguồn, Nội dung tin nhắn**.  
- Lưu lại **lịch sử tin nhắn đã gửi** và cho phép chọn lại nhanh.  
- Xuất log ra file **CSV** để phân tích bằng Excel.  
- Giao diện **mềm mại, hiện đại** nhờ áp dụng theme Nimbus + bo góc (SoftUI).  

---

## ⚙️ 2. Công nghệ sử dụng
Ứng dụng được phát triển bằng:
- **Ngôn ngữ lập trình**: <a href="https://www.oracle.com/java/"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"></a>
- **Thư viện UI**: <a href="https://docs.oracle.com/javase/tutorial/uiswing/"><img src="https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white" alt="Swing"></a>
- **JDK**: <a href="https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html"><img src="https://img.shields.io/badge/JDK-8+-green?style=for-the-badge&logo=java&logoColor=white" alt="JDK"></a>

- **Mô hình kiến trúc**: tách lớp rõ ràng:
  - `UDP`: core xử lý mạng (Sender, Receiver, NetUtils, Config).  
  - `Client`: giao diện người dùng (BroadcastUI, SoftUI, ReceiverService, HistoryStore).  

---

## 🖼️ 3. Một số hình ảnh hệ thống 

<p align="center">
    <em>Giao diện chính ứng dụng</em><br/>
    <img width="1387" height="819" alt="UI Main" src="https://github.com/user-attachments/assets/ce78f8ff-ea3c-49ab-9305-12ced8e4799e" />
</p>

<p align="center">
    <em>Chế độ Auto Send + Log hiển thị</em><br/>
    <img width="1401" height="842" alt="Auto Send" src="https://github.com/user-attachments/assets/198fa4df-fbea-4e71-ad04-ef50e116eed8" />
</p>

<p align="center">
    <em>Xuất CSV và mở bằng Excel</em><br/>
    <img width="673" height="502" alt="CSV Export" src="https://github.com/user-attachments/assets/e9ed8581-0605-4a64-8b62-775622647998" />
</p>

---

## 🛠️ 4. Các bước cài đặt
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

## 📞 5. Liên hệ
- 📧 Email: **giangnguyen27112k4@gmail.com**  
- 📞 SĐT: **0353397306**  
- 🌐 Website/FB: **https://www.facebook.com/jannguyen04**

---

<p align="center">
    ✍️ <em>README này được thiết kế bởi Giang Nguyen</em>
</p>
