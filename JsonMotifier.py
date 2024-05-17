import fileinput

file =  open("Data/JsonData/1.json", "r+")
file_text = file.read()
file_text = "[" + file_text

new_text = []

for line in file_text.splitlines():
    print("--------------------------------")
    new_text.append(line + ",")
#    print(line + ",")
new_text[len(new_text)-1] = new_text[len(new_text)-1][:-1] + "]"
print(new_text)
copy_file = open("Data/JsonData/1 copy.json", "r+")
copy_file.writelines(new_text)
