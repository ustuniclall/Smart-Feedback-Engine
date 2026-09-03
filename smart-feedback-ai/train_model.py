import pandas as pd
import pickle
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline

print("1. Veri seti okunuyor...")

# CSV formatı: Genellikle tab veya virgül ile ayrılmış iki sütun (Görüş / Durum)
df = None
for enc in ["utf-16", "utf-16-le", "utf-8", "iso-8859-9"]:
    try:
        df = pd.read_csv("magaza_yorumlari.csv", encoding=enc, sep=None, engine="python", on_bad_lines="skip")
        print(f"-> Başarıyla okundu. Encoding: {enc}")
        break
    except Exception:
        continue

if df is None:
    raise Exception("CSV dosyası okunamadı!")

# İlk iki sütunu al
df = df.iloc[:, [0, 1]]
df.columns = ["text", "label"]
df.dropna(inplace=True)

# Etiketleri standartlaştır
def clean_target(val):
    v = str(val).lower().strip()
    if any(x in v for x in ["olumlu", "pozitif", "1"]):
        return "POSITIVE"
    elif any(x in v for x in ["olumsuz", "negatif", "0", "-1"]):
        return "NEGATIVE"
    return None

df["label"] = df["label"].apply(clean_target)
df = df[df["label"].notnull()]

print(f"2. {len(df)} adet etiketli Türkçe yorum ile n-gram TF-IDF modeli eğitiliyor...")

# (1, 3) n-gram: "memnun kaldım sayılmaz" gibi 3'lü yapıları doğrudan yakalar
pipeline = Pipeline([
    ('tfidf', TfidfVectorizer(ngram_range=(1, 3), max_features=40000, sublinear_tf=True)),
    ('clf', MultinomialNB(alpha=0.1))
])

pipeline.fit(df["text"], df["label"])

# sentiment_model.pkl dosyasını kaydet
with open("sentiment_model.pkl", "wb") as f:
    pickle.dump(pipeline, f)

print("-> sentiment_model.pkl başarıyla oluşturuldu!")