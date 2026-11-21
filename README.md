# 🚀 Smart Feedback & Knowledge Engine

> Kurumsal süreçleri optimize etmek amacıyla geliştirilmiş, **Mikroservis Mimarisi** tabanlı; Doğal Dil İşleme (NLP) destekli müşteri geri bildirim analiz ve akıllı bilgi bankası (doküman özetleme & sınıflandırma) platformudur.

---

## 🏗️ Sistem Mimarisi & Teknoloji Yığını

Bu proje, iş mantığı ile yoğun yapay zeka hesaplama yüklerini birbirinden izole etmek için **mikroservis yaklaşımıyla** tasarlanmıştır.

* **Backend / Kurumsal Omurga:** Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, Maven
* **Yapay Zeka & NLP Servisi:** Python 3.13, Flask, Scikit-learn, Pandas, N-gram TF-IDF, MultinomialNB
* **Konteynerizasyon:** Docker, Docker Compose
* **Arayüz:** HTML5, Bootstrap, JavaScript (Fetch API)

---

## 📂 Proje Dizin Yapısı

```text
Smart-Feedback-Engine/
│
├── smart-feedback-ai/          # Python NLP & Yapay Zeka Mikroservisi
│   ├── app.py                  # Flask REST API uç noktaları
│   ├── train_model.py          # TF-IDF ve Duygu analizi model eğitim betiği
│   ├── magaza_yorumlari.csv    # Eğitim veri seti
│   ├── sentiment_model.pkl     # Serileştirilmiş ML modeli
│   ├── Dockerfile              # AI servis konteyner yapılandırması
│   └── requirements.txt        # Python bağımlılıkları
│
├── smart-feedback-engine/      # Spring Boot 3 Backend & Web Arayüzü
│   ├── src/                    # Java katmanlı mimari (Controller, Service, Repository)
│   ├── Dockerfile              # Backend servis konteyner yapılandırması
│   └── pom.xml                 # Maven bağımlılık yönetimi
│
├── docker-compose.yml          # Tüm sistemin (PostgreSQL, Backend, AI) orchestrator dosyası
└── .gitignore                  # Git dış tutulacak dosyalar listesi
```
## 💡 Temel Özellikler
Akıllı Müşteri Geri Bildirim Analizi (Sentiment Analysis):

Türkçe e-ticaret/mağaza yorumları veri seti (magaza_yorumlari.csv) ile eğitilmiş TF-IDF (1-3 N-gram) ve Multinomial Naive Bayes modeli kullanır.

Cümlelerdeki olumsuzluk eklerini ve bağlamı yakalayarak POSITIVE veya NEGATIVE sınıflandırması ile güven skoru üretir.

Otomatik Doküman Özeti (Extractive Summarization):

Yüklenen PDF veya TXT formatındaki kurumsal dokümanların kelime frekanslarını analiz ederek en yüksek bilgi yoğunluğuna sahip cümleleri seçer ve orijinal akışı bozmadan özetler.

Dinamik Konu Tespiti (Cosine Similarity):

Sabit kural kalıplarına bağlı kalmaksızın, yüklenen metinleri TF-IDF tabanlı Kosinüs Benzerliği ile Afet & Kriz Yönetimi, Sağlık & Medikal, Yazılım & Bilişim, Finans gibi genişletilebilir kurumsal kategorilere otomatik etiketler.


---

## ⚙️ Kurulum ve Çalıştırma (Docker ile)
Projeyi ayağa kaldırmanın en kolay ve hızlı yolu Docker Compose kullanmaktır.

Depoyu klonlayın:

Bash
git clone https://github.com/ustuniclall/Smart-Feedback-Engine.git
cd Smart-Feedback-Engine
Tüm sistemleri (PostgreSQL, Spring Boot Backend ve Python AI Servisi) arkada başlatın:

Bash
docker compose up --build -d
Sistem aktif olduktan sonra tarayıcınız üzerinden arayüze erişebilirsiniz:

Web Arayüzü: http://localhost:8080

AI Mikroservis API: http://localhost:5000


---

## 📊 Örnek API İstekleri
1. Duygu Analizi Tahmini (/predict)
Endpoint: POST http://localhost:5000/predict

Payload (JSON):

JSON
{
  "text": "yani çok memnun kaldım sayılmaz."
}
Yanıt:

JSON
{
  "sentiment": "NEGATIVE",
  "confidence": 0.91
}


---

## 👨‍💻 Geliştirici
İclal Üstün - Computer Engineering Student at Kocaeli University
