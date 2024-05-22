import fileinput
import os

# file =  open("Data/JsonData/1.json", "r+")
# file_text = file.read()
# file_text = "[" + file_text

# new_text = []

# for line in file_text.splitlines():
#     print("--------------------------------")
#     new_text.append(line + ",")
# #    print(line + ",")
# new_text[len(new_text)-1] = new_text[len(new_text)-1][:-1] + "]"
# print(new_text)
# copy_file = open("Data/JsonData/1 copy.json", "r+")
# copy_file.writelines(new_text)

folder = ['Data\JsonData' ,'Data\JsonData2']
output = 'Data\Output copy.json'
output = open(output,"w+" ,encoding="cp437")
new_output = []
new_output.append("[")
for fd in folder:
    for file in os.listdir(fd):
        file = os.path.join(fd, file)
        file = open(file, "r+", encoding="cp437")
        file_text = file.read()

        new_text = []

        for line in file_text.splitlines():
            print("--------------------------------")
            if(line == "]"): continue
            if("/#" in line): continue
            if(line.startswith("[") == True): line = line[1:]
            if(line.endswith(",") == False): line = line + ","
            new_text.append(line + "\n")
    #        line.replace("u0027", "’").replace("u0026", "and")
        #    print(line + ",")
        new_output.extend(new_text)
new_output[len(new_output)-1] = new_output[len(new_output)-1][:-2]
new_output.append("]")
output.writelines(new_output)
output.close()
 
# line = '"Crypo's"'
# line.replace("'", "'")
# print(line)
# output.write(line)
# output.close()