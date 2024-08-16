

r2_scores = [("LR   ",0.18122385143155229),
            ("RFR  ",0.4826345294112676),
            ("LASSO",0.12609537262012427),
            ("GB300",0.4136948199465522),
            ("GB200",0.39343215115875596),
            ("GB100",0.35108998515456724),
            ("KNR5 ",0.08421064301932935),
            ("EN   ",0.15270421724241656),
            ("RR   ",0.18120585558576952),
            ("DTR  ",0.2651821798403251)]

def keychain(e):
    r2 = e[1]
    return r2


r2_scores.sort(key=keychain)
r2_scores.reverse()
print(" ")
print("Sorted by r2 scores")
for x in r2_scores:
    print(x)
print(" ")


kagscores = [("KNR5",0.168),
            ("RFR  ",0.1354),
            ("LASSO",0.1642),
            ("GB300",0.1369),
            ("GB200",0.1397),
            ("GB100",0.1438),
            ("RR   ",0.1601),
            ("DTR  ",0.1516),
            ("LR   ",0.1601)]

kagscores.sort(key=keychain)

print(" ")
print("Sorted by kaggle scores")
for x in kagscores:
    print(x)
print(" ")
