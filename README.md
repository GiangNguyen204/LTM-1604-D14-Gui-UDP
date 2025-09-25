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

<div align="center">

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

</div>

---

## 1. Giới thiệu hệ thống
Đề tài xây dựng ứng dụng truyền tin nhắn broadcast qua giao thức UDP trong mạng LAN, gồm 2 ứng dụng client riêng biệt: 
- UDP Sender (gửi) 
- UDP Receiver (nhận). 

Tính năng chính: 
- Sender gửi 1 lần hoặc Auto Send (định kỳ) và lưu lịch sử. 
- Receiver lắng nghe trên cổng UDP, hiển thị log (Time, Remote IP, Port, Message) và xuất CSV. 
- Giao diện hiện đại (Nimbus + SoftUI bo góc).

---

## ⚙️ 2. Công nghệ sử dụng
Ứng dụng được phát triển bằng:

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)  
[![Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)  
[![JDK](https://img.shields.io/badge/JDK-8+-green?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)  

---

## 3. Một số hình ảnh hệ thống
<p align="center">
    <em>UI UDP Sender</em><br/>
    <img width="850" height="476" alt="UI Sender" src="https://github.com/user-attachments/assets/5797ec8c-f2e2-4a9e-bfd9-d6adc2b6e136" />
</p>

<p align="center">
    <em>UI UDP Receiver</em><br/>
    <img width="979" height="565" alt="UI Receiver" src="https://github.com/user-attachments/assets/b54ecd25-0405-40e6-986b-a0a32ee4f4c7" />
</p>

<p align="center">
    <em>Xuất CSV từ Receiver và mở bằng Excel</em><br/>
    <img width="673" height="502" alt="CSV Export" src="https://github.com/user-attachments/assets/c10f5968-0fda-4c4c-b6bb-92e00c7ab8c3" />
</p>

---

## 🛠️ 4. Các bước cài đặt
🎮 LTM-1604-D14-Gui-UDP 

Dự án Gửi tin nhắn Broadcast qua UDP
README này hướng dẫn cài đặt và chạy ứng dụng.

🛠️ 4.1. Yêu cầu hệ thống

  ☕ Java JDK      Phiên bản 8+ (khuyến nghị JDK 11 hoặc 17, JDK 21 vẫn
                   tương thích)

  💻 Hệ điều hành  Windows
         
  💾 Bộ nhớ        ≥ 4GB RAM, dung lượng trống tối thiểu 500MB

------------------------------------------------------------------------

📥 4.2. Các bước cài đặt

🧰 Bước 1: Chuẩn bị môi trường

1.  Cài đặt Java JDK

    -   Yêu cầu JDK 8 trở lên (khuyến nghị JDK 11/17).

    -   Kiểm tra cài đặt:

            java -version
            javac -version

        Nếu cả hai lệnh hiển thị version ≥ 8 là hợp lệ.

2.  Cấu trúc thư mục dự án

        └── src/
            ├── Client/   
            └── UDP/
            ├── ClientSender/   
            └── ClientReceiver/   

------------------------------------------------------------------------

🏗 Bước 2: Biên dịch mã nguồn

Mở Terminal/Command Prompt và điều hướng đến thư mục dự án:

    cd D:\Download\LTM-1604-D14-Gui-UDP

------------------------------------------------------------------------

▶️ Bước 3: Chạy ứng dụng

1️⃣ Khởi động Server

Chạy lệnh:
-   Chọn SenderMain.java chuột phải chọn RunAs

-   Server mặc định chạy trên port 5005 (có thể chỉnh trong code).


2️⃣ Khởi động Client

Mở một terminal mới và chạy:

-    Chọn RêciverMain.java chuột phải chọn RunAs

------------------------------------------------------------------------


## 📞 5. Liên hệ
- 📧 Email: **giangnguyen27112k4@gmail.com**  
- 📞 SĐT: **0353397306**  
- 🌐 Website/FB: **https://www.facebook.com/jannguyen04**

---

<p align="center">
    ✍️ <em>README này được thiết kế bởi Giang Nguyen</em>
</p>
