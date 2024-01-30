from sklearn.ensemble import GradientBoostingRegressor
from sklearn.calibration import LabelEncoder
import pandas as pd

from sklearn.metrics import accuracy_score, classification_report
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split

#exemplos de treino a partir do train.csv
df = pd.read_csv(r"C:\Users\Henrique\Desktop\uni\AA\trabalho2\train.csv")

df = df.dropna()

label_encoder = LabelEncoder()
df['scene'] = label_encoder.fit_transform(df['scene'])

X_train=df.iloc[:,1:-1]
y_train=df.iloc[:,-1]

print(X_train)
print(y_train)

#exemplos de teste a partir do test.csv
df = pd.read_csv(r"C:\Users\Henrique\Desktop\uni\AA\trabalho2\test.csv")
df = df.dropna()

df['scene'] = label_encoder.fit_transform(df['scene'])

X_test=df.iloc[:,1:]

from sklearn.ensemble import GradientBoostingRegressor

#GB
GB = GradientBoostingRegressor(n_estimators=200, learning_rate=0.1, random_state=42)
GB.fit(X_train, y_train)

#previsão
y_pred = GB.predict(X_test)


submission = pd.read_csv(r"C:\Users\Henrique\Desktop\uni\AA\trabalho2\sample_submission.csv")


submission['AOT_550'] = y_pred
submission['AOT_550'] = submission['AOT_550'].round(decimals = 3)

submission.to_csv(r"C:\Users\Henrique\Desktop\uni\AA\trabalho2\subGB200.csv", index=False)
print("done!")


