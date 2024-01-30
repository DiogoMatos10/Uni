from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import accuracy_score, classification_report
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split

#exemplos de treino a partir do train.csv
df = pd.read_csv(r"C:\Users\Henrique\Desktop\uni\AA\trabalho2\train.csv")


sns.pairplot(df)
plt.show()


