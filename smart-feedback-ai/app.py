import os
import re
import pickle
from collections import Counter
from flask import Flask, request, jsonify
from flask_cors import CORS
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)
CORS(app)

# 1. Duygu Analizi Modelini Yükle
MODEL_FILE = "sentiment_model.pkl"
model = None
if os.path.exists(MODEL_FILE):
    with open(MODEL_FILE, "rb") as f:
        model = pickle.load(f)
    print("Eğitilmiş Makine Öğrenimi Modeli Devrede.")
else:
    print("UYARI: sentiment_model.pkl bulunamadı!")

# 2. Genişletilebilir Kurumsal Kategori ve Kavram Havuzu
CATEGORY_CORPUS = {
    "Yazılım & Bilişim": "yazılım kodlama backend frontend spring docker java python javascript veritabanı api sql sunucu geliştirme mimari git algoritmalar",
    "Finans & Muhasebe": "finans bütçe fatura muhasebe vergi maliyet gelir gider banka ödeme hesap denetim bilanço yatırım sermaye",
    "İnsan Kaynakları & Yönetim": "personel istihdam bordro mülakat çalışan kariyer performans izin işe alım organizasyon eğitim ik oryantasyon",
    "Hukuk & Mevzuat": "hukuk kanun sözleşme dava mevzuat mahkeme yönetmelik protokol vekalet icra tüzük resmi gazete yükümlülük",
    "Eğitim & Akademi": "eğitim üniversite ders öğrenci sınav akademisyen tez araştırma müfredat enstitü fakülte seminer vize final"
}

# Kategori vektörlerini hazırla
category_names = list(CATEGORY_CORPUS.keys())
category_descriptions = list(CATEGORY_CORPUS.values())
topic_vectorizer = TfidfVectorizer(ngram_range=(1, 2))
category_vectors = topic_vectorizer.fit_transform(category_descriptions)


@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json() or {}
    text = data.get('text', '').strip()
    if not text:
        return jsonify({"sentiment": "NEUTRAL", "confidence": 0.5})

    if model is not None:
        prediction = model.predict([text])[0]
        probs = model.predict_proba([text])[0]
        class_idx = list(model.classes_).index(prediction)
        confidence = float(probs[class_idx])
        return jsonify({
            "sentiment": str(prediction),
            "confidence": round(confidence, 2)
        })

    return jsonify({"sentiment": "NEUTRAL", "confidence": 0.5})


@app.route('/analyze-document', methods=['POST'])
def analyze_document():
    data = request.get_json() or {}
    content = data.get('content', '').strip()
    if not content:
        return jsonify({"summary": "İçerik boş.", "category": "Genel Belge", "suggestedTags": []})

    content_lower = content.lower()

    # 1. Metin Temizliği ve Cümle Skorlama ile Gerçek Özetleme
    sentences = [s.strip() for s in re.split(r'(?<=[.!?])\s+', content) if len(s.strip()) > 20]
    words = re.findall(r'\b[a-zA-ZçğıöşüÇĞİÖŞÜ]{3,}\b', content_lower)
    stopwords = {
        "gibi", "için", "daha", "olan", "veya", "kadar", "ile", "bunun", "tarafından",
        "ancak", "çünkü", "olarak", "şekilde", "üzere", "ilgili", "birlikte", "tüm", "her"
    }
    valid_words = [w for w in words if w not in stopwords]
    word_freq = Counter(valid_words)

    # Cümleleri frekansa göre puanla
    sentence_scores = {}
    for i, sent in enumerate(sentences):
        sent_words = re.findall(r'\b[a-zA-ZçğıöşüÇĞİÖŞÜ]{3,}\b', sent.lower())
        score = sum(word_freq.get(w, 0) for w in sent_words if w not in stopwords)
        sentence_scores[i] = score / (len(sent_words) + 1)

    top_indices = sorted(sorted(sentence_scores.keys(), key=lambda i: sentence_scores[i], reverse=True)[:3])
    summary = " ".join([sentences[i] for i in top_indices]) if top_indices else content[:200]

    # 2. Dinamik Semantik Konu Tespiti (TF-IDF Cosine Similarity)
    doc_vector = topic_vectorizer.transform([content_lower])
    similarities = cosine_similarity(doc_vector, category_vectors)[0]
    best_match_idx = similarities.argmax()
    best_similarity_score = similarities[best_match_idx]

    # Eşik değer kontrolü: Benzerlik yeterince yüksekse kategoriyi ata, değilse Genel Doküman yap
    if best_similarity_score > 0.08:
        detected_category = category_names[best_match_idx]
    else:
        detected_category = "Genel Kurumsal Doküman"

    # 3. En Anlamlı 4 Anahtar Kelime
    common_tags = [w for w, _ in word_freq.most_common(4)]

    return jsonify({
        "summary": summary,
        "category": detected_category,
        "suggestedTags": common_tags,
        "similarityScore": round(float(best_similarity_score), 2)
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)